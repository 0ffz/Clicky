package me.dvyy.me.dvyy.clicky.data

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import kotlin.time.Duration.Companion.seconds


fun Application.configureRateLimit() {
    install(RateLimit) {
        global {
            rateLimiter(limit = 50, refillPeriod = 10.seconds)
        }
    }
}
