package me.dvyy.clicky.server.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import me.dvyy.clicky.server.data.Clicky
import me.dvyy.clicky.server.data.Limits
import me.dvyy.clicky.server.data.UserSession
import me.dvyy.clicky.server.plugins.CreateRoomRateLimit

context(clicky: Clicky)
fun Route.roomRoutes() = route("/room") {
    get {
        val roomId = call.parameters.getOrFail<String>("name").uppercase()
        call.respondRedirect("/room/${roomId.encodeURLPathPart()}")
    }

    rateLimit(CreateRoomRateLimit) {
        post {
            val roomName = call.receiveParameters().getOrFail<String>("name")
            if (roomName.length > Limits.MAX_ROOM_NAME_LENGTH) throw BadRequestException("Room name too long")
            val session = call.principal<UserSession>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val room = clicky.rooms.create(roomName, session.id)
            call.respondRedirect("/room/${room.code}")
        }
    }

    roomCodeRoutes()
}