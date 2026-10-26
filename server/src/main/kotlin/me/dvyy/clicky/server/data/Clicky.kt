package me.dvyy.clicky.server.data

import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class Clicky(app: Application) {
    val logger = app.log
    val config = ClickyConfig(app.environment.config)
    val rooms: RoomRepository = RoomRepository(logger, config)
    val liveConnections = ConnectionCounter(Limits.MAX_CONNECTIONS_PER_SESSION)
}

class ClickyConfig(conf: ApplicationConfig) {
    val site = conf.property("site.url").getString()
    val chartUpdateInterval = conf.property("site.chart.update_interval").getAs<Double>().seconds
    val inactiveRoomTimeout = conf.property("site.inactive_room_timeout").getAs<Double>().hours
    val trustProxyHeaders = conf.property("site.trust_proxy_headers").getString().toBoolean()
    val maxRooms = conf.property("site.max_rooms").getAs<Int>()
    val rateLimit = RateLimitConfig(conf.config("site.rate_limit"))
}

class RateLimitConfig(conf: ApplicationConfig) {
    val perIp = conf.property("per_ip").getAs<Int>()
    val perSession = conf.property("per_session").getAs<Int>()
    val newSessionsPerIp = conf.property("new_sessions_per_ip").getAs<Int>()
    val roomsPerIp = conf.property("rooms_per_ip").getAs<Int>()
}

/** Hard limits on user input, these protect memory use so aren't meant to be configured per deployment. */
object Limits {
    const val MAX_ROOM_NAME_LENGTH = 100
    const val MAX_OPTION_LENGTH = 50
    const val MAX_OPTIONS = 26
    const val MAX_REQUEST_BYTES = 8 * 1024L
    const val MAX_CONNECTIONS_PER_SESSION = 3
}
