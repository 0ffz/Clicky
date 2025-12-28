package me.dvyy.me.dvyy.clicky.data.routes

import io.ktor.htmx.*
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.sse.*
import io.ktor.server.util.*
import kotlinx.collections.immutable.immutableListOf
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.html.DIV
import kotlinx.html.div
import kotlinx.html.html
import kotlinx.html.id
import kotlinx.html.stream.appendHTML
import kotlinx.html.stream.createHTML
import me.dvyy.me.dvyy.clicky.data.UserSession
import me.dvyy.me.dvyy.clicky.data.createRoom
import me.dvyy.me.dvyy.clicky.data.ensureRoomOwner
import me.dvyy.me.dvyy.clicky.data.getRoom
import me.dvyy.me.dvyy.clicky.ui.components.barChart
import me.dvyy.me.dvyy.clicky.ui.components.voteOptions
import me.dvyy.me.dvyy.clicky.ui.pages.homePage
import me.dvyy.me.dvyy.clicky.ui.pages.resultsPage
import kotlin.time.Duration.Companion.seconds
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
class RoomViewModel(
    val name: String,
    val code: String,
    val admin: Uuid,
) {
    val votes = MutableStateFlow(persistentHashMapOf<Uuid, Int>())
    val options = MutableStateFlow(persistentListOf("A", "B", "C", "D"))
    val hidden = MutableStateFlow(false)
}

class RoomNotFoundException(val roomId: String) : Exception()

fun ApplicationCall.getRoomOrNull(): RoomViewModel? {
    val roomId = parameters.getOrFail<String>("room")
    return getRoom(roomId)
}

fun ApplicationCall.getRoomOrStop(): RoomViewModel =
    getRoomOrNull() ?: throw RoomNotFoundException(parameters.getOrFail<String>("room"))

@OptIn(ExperimentalUuidApi::class)
fun Application.configureRouting() {
    install(SSE)
    routing {
        staticResources("/", "/web")
        configureQRRouting()
        authenticate("session") {
            get("/") {
                call.respondHtml {
                    homePage()
                }
            }

            post("/create") {
                val roomId = call.receiveParameters().getOrFail<String>("name")
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
                        resultsPage(isOwner, room)
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

            }

            post("/rooms/{room}/vote") {
                val room = call.getRoomOrStop()
                val session = call.principal<UserSession>() ?: return@post
                val formParams = call.receiveParameters()
                val option = formParams.getOrFail<Int>("option")
                room.votes.update { it.put(session.id, option) }
                call.respond(HttpStatusCode.OK)
            }

            sse("/rooms/{room}/live") {
                val room = call.getRoomOrNull() ?: return@sse run {
                    val roomId = call.parameters.getOrFail<String>("room")
                    send(buildString { appendHTML().html { homePage(RoomNotFoundException(roomId)) } }, event = "room-not-found")
                    close()
                }
                val session = call.principal<UserSession>()
                    ?: return@sse close()//CloseReason(CloseReason.Codes.VIOLATED_POLICY, "No session"))
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
                    }.debounce(0.25.seconds).collectLatest { result ->
                        send(result, event = "chart")
                    }
                }
//                launch {
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