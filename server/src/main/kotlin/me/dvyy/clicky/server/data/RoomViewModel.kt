package me.dvyy.clicky.server.data

import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.job
import kotlin.uuid.Uuid

class RoomViewModel(
    val name: String,
    val code: String,
    val admin: Uuid,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) {
    private val _votes = MutableStateFlow(persistentHashMapOf<Uuid, Int>())
    private val _options = MutableStateFlow(persistentListOf("A", "B", "C", "D"))
    private val _hidden = MutableStateFlow(false)
    private val _reset = MutableSharedFlow<Unit>(replay = 1, onBufferOverflow = BufferOverflow.DROP_OLDEST)

    val votes = _votes.asStateFlow()
    val options = _options.asStateFlow()
    val hidden = _hidden.asStateFlow()
    val reset = _reset.asSharedFlow()

    fun vote(user: Uuid, option: Int) {
        _votes.update { it.put(user, option) }
    }

    fun clearVotes() {
        _reset.tryEmit(Unit)
        _votes.update { it.clear() }
    }

    fun addOption(option: String) {
        _options.update { it.add(option) }
    }

    fun removeOption(index: Int) {
        _options.update { it.removeAt(index) }
    }

    fun toggleHidden() {
        _hidden.update { !it }
    }

    fun close() {
        scope.cancel()
    }

    suspend fun awaitClose() = scope.coroutineContext.job.join()
}