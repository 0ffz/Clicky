package me.dvyy.me.dvyy.clicky.data

import kotlinx.serialization.Serializable

@Serializable
data class Selection(
    val option: Int? = null,
    val newOption: String? = null,
    val action: String? = null,
)