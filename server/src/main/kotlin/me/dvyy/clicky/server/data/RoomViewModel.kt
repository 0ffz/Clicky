package me.dvyy.clicky.server.data

import kotlinx.collections.immutable.persistentHashMapOf
import kotlinx.collections.immutable.persistentListOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlin.uuid.Uuid

class RoomViewModel(
    val name: String,
    val code: String,
    val admin: Uuid,
) {
    val votes = MutableStateFlow(persistentHashMapOf<Uuid, Int>())
    val options = MutableStateFlow(persistentListOf("A", "B", "C", "D"))
    val hidden = MutableStateFlow(false)
}