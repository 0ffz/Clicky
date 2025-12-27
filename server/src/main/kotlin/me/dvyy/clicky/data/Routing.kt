package me.dvyy.me.dvyy.clicky.data

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
import me.dvyy.me.dvyy.clicky.ui.components.barChart
import me.dvyy.me.dvyy.clicky.ui.components.voteOptions
import me.dvyy.me.dvyy.clicky.ui.pages.homePage
import me.dvyy.me.dvyy.clicky.ui.pages.resultsPage
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RoomViewModel(
    name: String,
    val admin: Uuid,
) {
    val name = MutableStateFlow(name)
    val votes = MutableStateFlow(mapOf<Uuid, Int>())
    val options = MutableStateFlow(listOf("A", "B", "C"))
    val hidden = MutableStateFlow(false)
}

@OptIn(ExperimentalUuidApi::class)
fun Application.configureRouting() {
    routing {
        staticResources("/", "/web")

        get("/") {
            call.respondHtml {
                homePage()
            }
        }
        authenticate("session") {
            route("/rooms") {
                post {
                    val roomId = call.receiveParameters().getOrFail<String>("name")
                    val session = call.principal<UserSession>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    createRoom(roomId, session.id)
                    call.respondRedirect("/rooms/$roomId")
                }

                get("/{room}") {
                    val roomId = call.parameters.getOrFail<String>("room")
                    val room = getRoom(roomId)
                    val isOwner = room.admin == call.principal<UserSession>()?.id
                    call.respondHtml {
                        resultsPage(isOwner, roomId)
                    }
                }
                post("/{room}/admin") {
                    ensureRoomOwner(call) {
                        val roomId = call.parameters.getOrFail<String>("room")
                        val formParams = call.receiveParameters()
                        val action = formParams.getOrFail<String>("action")
                        val room = getRoom(roomId)
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
                                val optionToDelete = formParams.getOrFail<String>("option")
                                room.options.update { it - optionToDelete }
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
                val roomId = call.parameters.getOrFail<String>("room")
                val session = call.principal<UserSession>()
                    ?: return@webSocket close(CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
                val room = getRoom(roomId)
                println(session)
                launch {
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
                                    roomId,
                                    hidden,
                                    options.mapIndexed { index, string -> string to (counts[index] ?: 0) })
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
                                voteOptions(it.count())
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