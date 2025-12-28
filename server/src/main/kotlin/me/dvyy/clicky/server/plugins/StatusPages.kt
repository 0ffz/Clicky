package me.dvyy.clicky.server.plugins

import io.ktor.server.application.*
import io.ktor.server.html.*
import io.ktor.server.plugins.statuspages.*
import me.dvyy.clicky.server.data.exceptions.RoomNotFoundException
import me.dvyy.clicky.ui.pages.homePage

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<RoomNotFoundException> { call, cause ->
            call.respondHtml { homePage(cause) }
        }
    }
}