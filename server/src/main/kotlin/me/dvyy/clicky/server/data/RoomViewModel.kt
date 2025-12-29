package me.dvyy.clicky.server.data

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import app.cash.molecule.RecompositionMode
import app.cash.molecule.launchMolecule
import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.job
import me.dvyy.clicky.helpers.partialHtml
import me.dvyy.clicky.ui.components.barChart
import me.dvyy.clicky.ui.components.voteOptions
import kotlin.uuid.Uuid

class RoomViewModel(
    val name: String,
    val code: String,
    val admin: Uuid,
    val config: ClickyConfig,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO),
) {
    private val _votes = MutableStateFlow(persistentHashMapOf<Uuid, Int>())
    private val _options = MutableStateFlow(persistentListOf("A", "B", "C", "D"))
    private val _hidden = MutableStateFlow(false)
    private val _clearedTimes = MutableStateFlow(0)

    val votes = _votes.asStateFlow()
    val options = _options.asStateFlow()
    val hidden = _hidden.asStateFlow()
    val clearedTimes = _clearedTimes.asStateFlow()

    // TODO UI doesn't really make sense to keep in ViewModel, but since we want to avoid rendering html many times for
    //  each client these are placed here. In a complete framework ideally it would be possible to define these in
    //  our kotlinx.html pages, but this requires a lot more state tracking that molecule doesn't provide us with.
    val renderedVotes = scope.launchMolecule(RecompositionMode.Immediate) {
        val options by options.collectAsState()
        val cleared by clearedTimes.collectAsState()
        partialHtml {
            voteOptions(code, options)
        } to cleared
    }

    val renderedChart = scope.launchMolecule(RecompositionMode.Immediate) {
        val votes by votes.debounce(config.chartUpdateInterval).collectAsState(votes.value)
        val options by options.collectAsState()
        val counts = votes.entries.groupingBy { it.value }.eachCount()
        partialHtml {
            barChart(
                code,
                hidden = false,
                options.mapIndexed { index, string -> string to (counts[index] ?: 0) }
            )
        }
    }

    fun vote(user: Uuid, option: Int) {
        _votes.update { it.put(user, option) }
    }

    fun clearVotes() {
        _clearedTimes.update { it + 1 }
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

