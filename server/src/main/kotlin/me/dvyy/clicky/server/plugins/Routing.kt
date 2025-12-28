package me.dvyy.clicky.server.plugins

import io.ktor.server.application.*
import io.ktor.server.auth.*
import io.ktor.server.html.*
import io.ktor.server.http.content.*
import io.ktor.server.routing.*
import me.dvyy.clicky.server.data.Clicky
import me.dvyy.clicky.server.routes.roomRoutes
import me.dvyy.clicky.ui.pages.homePage

context(clicky: Clicky)
fun Application.configureRouting() {
    routing {
        staticResources("/", "/web")

        authenticate("session") {
            get("/") {
                call.respondHtml {
                    homePage()
                }
            }
            roomRoutes()
        }
    }
}