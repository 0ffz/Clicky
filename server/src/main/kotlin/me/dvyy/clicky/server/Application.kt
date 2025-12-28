package me.dvyy.clicky.server

import io.ktor.server.application.*
import io.ktor.server.netty.*
import me.dvyy.clicky.server.data.Clicky
import me.dvyy.clicky.server.plugins.configureCaching
import me.dvyy.clicky.server.plugins.configureRateLimit
import me.dvyy.clicky.server.plugins.configureRouting
import me.dvyy.clicky.server.plugins.configureSSE
import me.dvyy.clicky.server.plugins.configureSecurity
import me.dvyy.clicky.server.plugins.configureSerialization
import me.dvyy.clicky.server.plugins.configureStatusPages

fun main(args: Array<String>) {
    EngineMain.main(args)
}

fun Application.module() = with(Clicky(this)) {
    configureSerialization()
    configureSecurity()
    configureSSE()
    configureCaching()
    configureRateLimit()
    configureRouting()
    configureStatusPages()
}
