package me.dvyy.me.dvyy.clicky.data

import io.ktor.http.HttpStatusCode
import io.ktor.http.parameters
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.request.uri
import io.ktor.server.response.respond
import io.ktor.server.sessions.*
import io.ktor.server.util.getOrFail
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("user_session", SessionStorageMemory()) {
            cookie.extensions["SameSite"] = "lax"
        }
    }
    authentication {
        session<UserSession>("session") {
            validate { session -> session }
            challenge {
                val session = call.principal<UserSession>() ?: UserSession(Uuid.random())
                call.sessions.set(session)
            }
        }
    }
}

@OptIn(ExperimentalUuidApi::class)
suspend fun ensureRoomOwner(call: ApplicationCall, block: suspend (UserSession) -> Unit) {
    val roomId = call.parameters.getOrFail<String>("room")
    val session = call.principal<UserSession>() ?: return call.respond(HttpStatusCode.Unauthorized)
    if (getRoom(roomId).admin != session.id) return call.respond(HttpStatusCode.Unauthorized)
    block(session)
}
@OptIn(ExperimentalUuidApi::class)
@Serializable
data class UserSession(val id: Uuid, val count: Int = 0)
