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
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.stream.appendHTML
import me.dvyy.clicky.server.data.UserSession
import me.dvyy.clicky.server.data.Clicky
import me.dvyy.clicky.server.data.ensureRoomOwner
import me.dvyy.clicky.server.data.getRoomOrNull
import me.dvyy.clicky.server.data.getRoomOrStop
import me.dvyy.clicky.server.data.exceptions.RoomNotFoundException
import me.dvyy.clicky.ui.components.barChart
import me.dvyy.clicky.ui.components.voteOptions
import me.dvyy.clicky.ui.pages.homePage
import me.dvyy.clicky.ui.pages.resultsPage

@OptIn(FlowPreview::class)
context(clicky: Clicky)
fun Route.roomCodeRoutes() = route("/{room}") {
    get {
        val room = call.getRoomOrStop()
        val isOwner = room.admin == call.principal<UserSession>()?.id
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
        room.votes.update { it.put(session.id, option) }
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
                        room.options.update { it.add(newOption) }
                        call.respond(HttpStatusCode.OK)
                    } else {
                        call.respond(HttpStatusCode.BadRequest)
                    }
                }

                "delete" -> {
                    val optionToDelete = formParams.getOrFail<Int>("option")
                    room.options.update { it.removeAt(optionToDelete) }
                    call.respond(HttpStatusCode.OK)
                }

                "reset" -> {
                    room.votes.update { it.clear() }
                }
            }
        }
    }

    sse("/live") {
        val room = call.getRoomOrNull() ?: return@sse run {
            val roomId = call.parameters.getOrFail<String>("room")
            send(
                buildString { appendHTML().html { homePage(RoomNotFoundException(roomId)) } },
                event = "room-not-found"
            )
            close()
        }
        call.principal<UserSession>() ?: return@sse close()
        val isOwner = room.admin == call.principal<UserSession>()?.id

        if (isOwner) launch {
            combine(
                room.hidden,
                room.votes,
                room.options
            ) { hidden, votes, options ->
                val counts = votes.entries.groupingBy { it.value }.eachCount()
                buildString {
                    appendHTML().div {
                        id = "chart"
                        barChart(
                            room.code,
                            hidden,
                            options.mapIndexed { index, string -> string to (counts[index] ?: 0) }
                        )
                    }
                }
            }.debounce(clicky.config.chartUpdateInterval).collectLatest { result ->
                send(result, event = "chart")
            }
        }

        room.options.collectLatest {
            send(buildString {
                appendHTML().div {
                    id = "options"
                    voteOptions(room.code, it)
                }
            }, event = "options")
        }
    }
}