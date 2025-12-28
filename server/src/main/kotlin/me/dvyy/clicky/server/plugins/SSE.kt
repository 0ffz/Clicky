package me.dvyy.clicky.server.plugins

import io.ktor.server.application.*
import io.ktor.server.sse.*

fun Application.configureSSE() {
    install(SSE)
}
