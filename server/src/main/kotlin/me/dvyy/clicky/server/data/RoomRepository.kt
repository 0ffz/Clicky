package me.dvyy.clicky.server.data

import io.ktor.util.collections.ConcurrentMap
import me.dvyy.clicky.server.data.RoomViewModel
import org.sqids.Sqids
import kotlin.collections.set
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.random.Random
import kotlin.uuid.Uuid

class RoomRepository {
    private val rooms = ConcurrentMap<String, RoomViewModel>()
    private val random = Random(System.currentTimeMillis())
    private val sqids = Sqids(
        alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray().apply { shuffle() }.concatToString(),
    )

    @OptIn(ExperimentalAtomicApi::class)
    private val counter = AtomicLong(0L)

    operator fun get(room: String) = rooms[room]

    @OptIn(ExperimentalAtomicApi::class)
    fun create(name: String, admin: Uuid): RoomViewModel? {
        val code = sqids.encode(listOf(counter.fetchAndIncrement(), random.nextLong(1024)))
        val room = RoomViewModel(name = name, code = code, admin = admin)
        if (rooms.contains(code)) return null
        rooms[code] = room
        return room
    }
}