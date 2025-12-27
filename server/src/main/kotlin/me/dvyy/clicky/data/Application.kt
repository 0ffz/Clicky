package me.dvyy.me.dvyy.clicky.data

import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

fun main(args: Array<String>) {
    EngineMain.main(args)
}


val rooms = mutableMapOf<String, RoomViewModel>()

@OptIn(ExperimentalUuidApi::class)
fun getRoom(room: String) = rooms.getValue(room)

@OptIn(ExperimentalUuidApi::class)
fun createRoom(room: String, admin: Uuid) = rooms.getOrPut(room) { RoomViewModel(room, admin) }

fun Application.module() {
    configureSerialization()
    configureSecurity()
    configureSockets()
    configureRouting()
}
