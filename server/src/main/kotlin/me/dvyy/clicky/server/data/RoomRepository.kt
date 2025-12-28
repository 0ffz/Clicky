package me.dvyy.clicky.server.data

import com.github.benmanes.caffeine.cache.Caffeine
import io.ktor.util.logging.*
import org.sqids.Sqids
import kotlin.concurrent.atomics.AtomicLong
import kotlin.concurrent.atomics.ExperimentalAtomicApi
import kotlin.concurrent.atomics.fetchAndIncrement
import kotlin.random.Random
import kotlin.time.toJavaDuration
import kotlin.uuid.Uuid

class RoomRepository(
    private val logger: Logger,
    config: ClickyConfig,
) {
    private val rooms = Caffeine.newBuilder()
        .expireAfterAccess(config.inactiveRoomTimeout.toJavaDuration())
        .removalListener<String, RoomViewModel> { _, value, _ ->
            logger.info("Room ${value?.code} expired")
            value?.close()
        }
        .build<String, RoomViewModel>()

    private val random = Random(System.currentTimeMillis())
    private val sqids = Sqids(
        alphabet = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray().apply { shuffle() }.concatToString(),
    )

    @OptIn(ExperimentalAtomicApi::class)
    private val counter = AtomicLong(0L)

    operator fun get(room: String) = rooms.getIfPresent(room)

    @OptIn(ExperimentalAtomicApi::class)
    fun create(name: String, admin: Uuid): RoomViewModel {
        val code = sqids.encode(listOf(counter.fetchAndIncrement(), random.nextLong(1024)))
        val room = RoomViewModel(name = name, code = code, admin = admin)
        rooms.put(code, room)
        logger.info("Created room ${room.code}")
        return room
    }
}