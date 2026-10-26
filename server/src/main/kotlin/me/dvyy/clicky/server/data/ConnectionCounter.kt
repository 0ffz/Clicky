package me.dvyy.clicky.server.data

import java.util.concurrent.ConcurrentHashMap

/** Tracks open long-lived connections (ex. SSE) per key, allowing at most [max] at once. */
class ConnectionCounter(private val max: Int) {
    private val counts = ConcurrentHashMap<Any, Int>()

    fun tryAcquire(key: Any): Boolean {
        var acquired = false
        counts.compute(key) { _, count ->
            val current = count ?: 0
            if (current < max) {
                acquired = true
                current + 1
            } else current
        }
        return acquired
    }

    fun release(key: Any) {
        counts.computeIfPresent(key) { _, count -> if (count <= 1) null else count - 1 }
    }
}
