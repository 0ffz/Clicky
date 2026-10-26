package me.dvyy.clicky.server.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.sessions.*
import me.dvyy.clicky.server.data.Clicky
import me.dvyy.clicky.server.data.UserSession
import java.security.SecureRandom
import kotlin.uuid.Uuid

context(clicky: Clicky)
fun Application.configureSecurity() {
    // Rooms are in-memory, so sessions don't need to outlive a restart and the key can be random
    val signKey = ByteArray(32).also { SecureRandom().nextBytes(it) }
    install(Sessions) {
        cookie<UserSession>("user_session") {
            cookie.extensions["SameSite"] = "lax"
            cookie.secure = clicky.config.site.startsWith("https://")
            transform(SessionTransportTransformerMessageAuthentication(signKey))
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
