package me.dvyy.clicky.server.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.*
import io.ktor.server.plugins.ratelimit.*
import io.ktor.server.sessions.*
import me.dvyy.clicky.server.data.Clicky
import me.dvyy.clicky.server.data.UserSession
import kotlin.time.Duration
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

/** Limits each session, or each IP for requests without a session (i.e. new users joining.) */
val SessionRateLimit = RateLimitName("session")
val CreateRoomRateLimit = RateLimitName("create-room")

private fun limiter(limit: Int, refillPeriod: Duration) =
    if (limit > 0) RateLimiter.default(limit, refillPeriod) else RateLimiter.Unlimited

context(clicky: Clicky)
fun Application.configureRateLimit() {
    val limits = clicky.config.rateLimit

    install(RateLimit) {
        // Generous backstop, many students may share one IP
        global {
            rateLimiter { _, _ -> limiter(limits.perIp, 10.seconds) }
            requestKey { it.request.origin.remoteAddress }
        }
        // Session ids can't be forged since cookies are signed, so new sessions are only limited by the per-IP bucket
        register(SessionRateLimit) {
            requestKey { it.sessions.get<UserSession>() ?: it.request.origin.remoteAddress }
            rateLimiter { _, key ->
                if (key is UserSession) limiter(limits.perSession, 10.seconds)
                else limiter(limits.newSessionsPerIp, 1.minutes)
            }
        }
        register(CreateRoomRateLimit) {
            rateLimiter { _, _ -> limiter(limits.roomsPerIp, 1.hours) }
            requestKey { it.request.origin.remoteAddress }
        }
    }
}
