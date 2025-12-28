package me.dvyy.me.dvyy.clicky.data

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.html.respondHtml
import io.ktor.server.plugins.statuspages.StatusPages
import me.dvyy.me.dvyy.clicky.data.routes.RoomNotFoundException
import me.dvyy.me.dvyy.clicky.ui.pages.homePage

fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<RoomNotFoundException> { call, cause ->
            call.respondHtml { homePage(cause) }
        }
    }
}