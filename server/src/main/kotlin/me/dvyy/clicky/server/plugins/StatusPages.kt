package me.dvyy.clicky.server.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.statuspages.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import me.dvyy.clicky.server.data.exceptions.RoomNotFoundException
import me.dvyy.clicky.server.data.exceptions.TooManyRoomsException
import me.dvyy.clicky.ui.pages.homePage

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<RoomNotFoundException> { call, cause ->
            call.respondHtml { homePage(cause) }
        }
        exception<TooManyRoomsException> { call, _ ->
            call.respond(HttpStatusCode.ServiceUnavailable)
        }
        // Includes missing or malformed parameters
        exception<BadRequestException> { call, _ ->
            call.respond(HttpStatusCode.BadRequest)
        }
        exception<Throwable> { call, cause ->
            call.application.log.error("Unhandled error for ${call.request.uri}", cause)
            call.respond(HttpStatusCode.InternalServerError)
        }
    }
}
