package me.dvyy.me.dvyy.clicky.data.routes

import io.ktor.htmx.HxResponseHeaders
import io.ktor.http.*
import io.ktor.serialization.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.html.DIV
import kotlinx.html.div
import kotlinx.html.id
import kotlinx.html.stream.appendHTML
import me.dvyy.me.dvyy.clicky.data.*
import me.dvyy.me.dvyy.clicky.ui.components.barChart
import me.dvyy.me.dvyy.clicky.ui.components.voteOptions
import me.dvyy.me.dvyy.clicky.ui.pages.homePage
import me.dvyy.me.dvyy.clicky.ui.pages.resultsPage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RoomViewModel(
    val name: String,
    val code: String,
    val admin: Uuid,
) {
    val votes = MutableStateFlow(mapOf<Uuid, Int>())
    val options = MutableStateFlow(listOf("A", "B", "C"))
    val hidden = MutableStateFlow(false)
}

class RoomNotFoundException(val roomId: String) : Exception()

fun ApplicationCall.getRoomOrStop(): RoomViewModel {
    val roomId = parameters.getOrFail<String>("room")
    return getRoom(roomId) ?: throw RoomNotFoundException(roomId)
}

@OptIn(ExperimentalUuidApi::class)
fun Application.configureRouting() {
    routing {
        staticResources("/", "/web")

        authenticate("session") {
            get("/") {
                call.respondHtml {
                    homePage()
                }
            }

            post("/create") {
                val roomId = call.receiveParameters().getOrFail<String>("name").uppercase()
                val session = call.principal<UserSession>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                val room = createRoom(roomId, session.id) ?: return@post call.respond(HttpStatusCode.Conflict)
                call.respondRedirect("/rooms/${room.code}")
            }
            route("/rooms") {
                post {
                    val roomId = call.receiveParameters().getOrFail<String>("name").uppercase()
                    val exists = getRoom(roomId) != null
                    call.respondRedirect("/rooms/$roomId")
                }

                get("/{room}") {
                    val room = call.getRoomOrStop()
                    val isOwner = room.admin == call.principal<UserSession>()?.id
                    call.response.headers.append(HxResponseHeaders.PushUrl, "/rooms/${room.code}")
                    call.respondHtml {
                        resultsPage(isOwner, room.code)
                    }
                }
                post("/{room}/admin") {
                    ensureRoomOwner(call) {
                        val room = call.getRoomOrStop()
                        val formParams = call.receiveParameters()
                        val action = formParams.getOrFail<String>("action")
                        when (action) {
                            "add" -> {
                                val newOption = formParams.getOrFail<String>("option")
                                if (newOption != "") {
                                    room.options.update { it + newOption }
                                    call.respond(HttpStatusCode.OK)
                                } else {
                                    call.respond(HttpStatusCode.BadRequest)
                                }
                            }

                            "delete" -> {
                                val optionToDelete = formParams.getOrFail<Int>("option")
                                room.options.update { it.filterIndexed { index, _ -> index != optionToDelete } }
                                call.respond(HttpStatusCode.OK)
                            }

                            "reset" -> {
                                room.votes.update { mapOf() }
                            }
                        }
                    }
                }

            }

            webSocket("/rooms/{room}/live") {
                val room = call.getRoomOrStop()
                val session = call.principal<UserSession>()
                    ?: return@webSocket close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                val isOwner = room.admin == call.principal<UserSession>()?.id
                println(session)
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
                    }.collectLatest { result ->
                        send(result)
                    }
                }
                launch {
                    room.options.collectLatest {
                        send(buildString {
                            appendHTML().div {
                                id = "options"
                                voteOptions(it)
                            }
                        })
                    }
                }
                // receive
                while (true) {
                    val text = incoming.receive() as? Frame.Text ?: break
                    println(text.readText())
                    val selection = converter!!.deserialize<Selection>(text)
                    println(selection)
                    selection.apply {
                        when {
                            option != null -> room.votes.update { it + (session.id to option) }
                        }
                    }
                }
//            close(CloseReason(CloseReason.Codes.NORMAL, "All done"))
            }
        }
    }
}


suspend fun ApplicationCall.respondDiv(id: String, block: DIV.() -> Unit) {
    respond(buildString {
        appendHTML().div {
            this.id = id
            block()
        }
    })
}