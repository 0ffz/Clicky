package me.dvyy.clicky.server.data

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class UserSession(val id: Uuid)