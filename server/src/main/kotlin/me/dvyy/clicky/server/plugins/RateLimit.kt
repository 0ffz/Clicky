package me.dvyy.clicky.server.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.ratelimit.*
import me.dvyy.clicky.server.data.Clicky
import kotlin.time.Duration.Companion.seconds


context(clicky: Clicky)
fun Application.configureRateLimit() {
    val rateLimit = clicky.config.rateLimit
    if (rateLimit <= 0) return

    install(RateLimit) {
        global {
            rateLimiter(limit = rateLimit, refillPeriod = 10.seconds)
        }
    }
}
