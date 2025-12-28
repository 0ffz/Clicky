package me.dvyy.me.dvyy.clicky.data

import io.ktor.server.application.*
import io.ktor.server.netty.*
import io.ktor.util.collections.ConcurrentMap
import me.dvyy.me.dvyy.clicky.data.routes.RoomViewModel
import me.dvyy.me.dvyy.clicky.data.routes.configureRouting
import org.sqids.Sqids
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.random.Random
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun main(args: Array<String>) {
    EngineMain.main(args)
}


val rooms = ConcurrentMap<String, RoomViewModel>()
val random = Random(System.currentTimeMillis())

@OptIn(ExperimentalUuidApi::class)
fun getRoom(room: String) = rooms[room]

@OptIn(ExperimentalUuidApi::class, ExperimentalAtomicApi::class)
fun createRoom(name: String, admin: Uuid): RoomViewModel? {
    val code = sqids.encode(listOf(counter.fetchAndIncrement(), random.nextLong(1024)))
    val room = RoomViewModel(name = name, code = code, admin = admin)
    if (rooms.contains(code)) return null
    rooms[code] = room
    return room
}

@OptIn(ExperimentalAtomicApi::class)
val counter = AtomicLong(0L)
private val sqids = Sqids(
    alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray().apply { shuffle() }.concatToString(),
//    minLength = 4,
)

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureSockets()
    configureRouting()
    configureRateLimit()
    configureStatusPages()
}
