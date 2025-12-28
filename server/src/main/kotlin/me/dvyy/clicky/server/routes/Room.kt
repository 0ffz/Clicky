package me.dvyy.clicky.server.routes

import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.util.*
import me.dvyy.clicky.server.data.Clicky
import me.dvyy.clicky.server.data.UserSession

context(clicky: Clicky)
fun Route.roomRoutes() = route("/room") {
    get {
        val roomId = call.parameters.getOrFail<String>("name").uppercase()
        call.respondRedirect("/room/$roomId")
    }

    post {
        val roomId = call.receiveParameters().getOrFail<String>("name")
        val session = call.principal<UserSession>() ?: return@post call.respond(HttpStatusCode.Unauthorized)
        val room = clicky.rooms.create(roomId, session.id)
        call.respondRedirect("/room/${room.code}")
    }

    roomCodeRoutes()
}