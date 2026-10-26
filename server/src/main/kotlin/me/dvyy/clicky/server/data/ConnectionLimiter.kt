package me.dvyy.clicky.server.data

import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.Deferred
import java.util.concurrent.ConcurrentHashMap

/**
 * Tracks open long-lived connections (ex. SSE) per key, allowing at most [max] at once.
 * Opening a new connection past the limit evicts the oldest one, since disconnected clients (ex. on page reload)
 * aren't noticed until the next write.
 */
class ConnectionLimiter(private val max: Int) {
    private val connections = ConcurrentHashMap<Any, ArrayDeque<CompletableDeferred<Unit>>>()

    /** Registers a new connection, returning a signal that completes when it gets evicted. */
    fun acquire(key: Any): Deferred<Unit> {
        val connection = CompletableDeferred<Unit>()
        var evicted: CompletableDeferred<Unit>? = null
        connections.compute(key) { _, existing ->
            val queue = existing ?: ArrayDeque()
            if (queue.size >= max) evicted = queue.removeFirst()
            queue.addLast(connection)
            queue
        }
        evicted?.complete(Unit)
        return connection
    }

    fun release(key: Any, connection: Deferred<Unit>) {
        connections.computeIfPresent(key) { _, queue ->
            queue.remove(connection)
            queue.ifEmpty { null }
        }
    }
}
