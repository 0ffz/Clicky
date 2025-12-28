package me.dvyy.clicky.server.data

import io.ktor.server.application.*
import io.ktor.server.config.*
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.seconds

class Clicky(app: Application) {
    val logger = app.log
    val config = ClickyConfig(app.environment.config)
    val rooms: RoomRepository = RoomRepository(logger, config)
}

class ClickyConfig(conf: ApplicationConfig) {
    val site = conf.property("site.url").getString()
    val chartUpdateInterval = conf.property("site.chart.update_interval").getAs<Double>().seconds
    val rateLimit = conf.property("site.rate_limit").getAs<Int>()
    val inactiveRoomTimeout = conf.property("site.inactive_room_timeout").getAs<Double>().hours
}
