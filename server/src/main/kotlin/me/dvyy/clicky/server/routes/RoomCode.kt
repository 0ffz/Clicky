package me.dvyy.clicky.server.routes

import io.ktor.htmx.*
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.server.util.*
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.launch
import kotlinx.html.html
import kotlinx.html.stream.appendHTML
import me.dvyy.clicky.server.data.*
import me.dvyy.clicky.server.data.exceptions.RoomNotFoundException
import me.dvyy.clicky.ui.pages.homePage
import me.dvyy.clicky.ui.pages.resultsPage

@OptIn(FlowPreview::class)
context(clicky: Clicky)
fun Route.roomCodeRoutes() = route("/{room}") {
    get {
        val room = call.getRoomOrStop()
        val id = call.principal<UserSession>()?.id ?: return@get
        val isOwner = room.admin == id
        call.response.headers.append(HxResponseHeaders.PushUrl, "/room/${room.code}")
        call.respondHtml {
            resultsPage(isOwner, room)
        }
    }

    roomQRRoutes()

    post("/vote") {
        val room = call.getRoomOrStop()
        val session = call.principal<UserSession>() ?: return@post
        val formParams = call.receiveParameters()
        val option = formParams.getOrFail<Int>("option")
        room.vote(session.id, option)
        call.respond(HttpStatusCode.OK)

    }

    post("/admin") {
        ensureRoomOwner(call) { room, _ ->
            val formParams = call.receiveParameters()
            val action = formParams.getOrFail<String>("action")
            when (action) {
                "add" -> {
                    val newOption = formParams.getOrFail<String>("option")
                    if (newOption != "") {
                        room.addOption(newOption)
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

                "delete" -> {
                    val optionToDelete = formParams.getOrFail<Int>("option")
                    room.removeOption(optionToDelete)
                    call.respond(HttpStatusCode.OK)
                }

                "reset" -> {
                    room.clearVotes()
                    call.respond(HttpStatusCode.OK)
                }

                "close" -> {
                    clicky.rooms.close(room)
                }
            }
        }
    }

    sse("/live") {
        val roomId = call.parameters.getOrFail<String>("room")
        suspend fun sendRoomClosed() {
            send(
                buildString { appendHTML().html { homePage(RoomNotFoundException(roomId)) } },
                event = "room-not-found"
            )
            close()
        }

        val room = call.getRoomOrNull() ?: return@sse sendRoomClosed()
        call.principal<UserSession>() ?: return@sse close()
        val isOwner = room.admin == call.principal<UserSession>()?.id

        if (isOwner) launch {
            room.renderedChart.collect {
                send(it, event = "chart")
            }
        }
        launch {
            room.renderedVotes.collect { send(it.first, event = "options") }
        }
        room.awaitClose()
        sendRoomClosed()
    }
}
