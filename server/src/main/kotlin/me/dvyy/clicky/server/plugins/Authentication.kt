package me.dvyy.clicky.server.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import me.dvyy.clicky.server.data.UserSession
import kotlin.uuid.Uuid

fun Application.configureSecurity() {
    install(Sessions) {
        cookie<UserSession>("user_session") {
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
