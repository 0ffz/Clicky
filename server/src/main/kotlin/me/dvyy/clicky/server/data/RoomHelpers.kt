package me.dvyy.clicky.server.data

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.response.*
import io.ktor.server.util.*
import me.dvyy.clicky.server.data.exceptions.RoomNotFoundException

context(clicky: Clicky)
fun ApplicationCall.getRoomOrNull(): RoomViewModel? {
    val roomId = parameters.getOrFail<String>("room")
    return clicky.rooms[roomId]
}

context(clicky: Clicky)
fun ApplicationCall.getRoomOrStop(): RoomViewModel =
    getRoomOrNull() ?: throw RoomNotFoundException(parameters.getOrFail<String>("room"))

context(clicky: Clicky)
suspend fun ensureRoomOwner(call: ApplicationCall, block: suspend (RoomViewModel, UserSession) -> Unit) {
    val session = call.principal<UserSession>() ?: return call.respond(HttpStatusCode.Unauthorized)
    val room = call.getRoomOrStop()
    if (room.admin != session.id) return call.respond(HttpStatusCode.Unauthorized)
    block(room, session)
}