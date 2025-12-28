package me.dvyy.me.dvyy.clicky.data.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import qrcode.QRCode
import qrcode.color.Colors

fun Routing.configureQRRouting() {
    val site = environment.config.property("site.url").getString()
    fun generateCode(room: RoomViewModel, color: Int, background: Int) = QRCode.ofRoundedSquares()
        .withColor(color)
        .withBackgroundColor(background)
        .build("$site/rooms/${room.code}")
    route("/rooms/{room}") {
        get("qr.png") {
            val room = call.getRoomOrStop()
            val qr = generateCode(room, Colors.BLACK, 0xf4f4f5)
            call.respondOutputStream(ContentType.Image.PNG) {
                qr.render().writeImage(this)
            }
        }
        get("qr-dark.png") {
            val room = call.getRoomOrStop()
            val qr = generateCode(room, Colors.WHITE, Colors.TRANSPARENT)
            call.respondOutputStream(ContentType.Image.PNG) {
                qr.render().writeImage(this)
            }
        }
    }
}