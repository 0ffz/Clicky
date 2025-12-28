package me.dvyy.clicky.server.routes

import io.ktor.http.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import me.dvyy.clicky.server.data.Clicky
import me.dvyy.clicky.server.data.RoomViewModel
import me.dvyy.clicky.server.data.ensureRoomOwner
import qrcode.QRCode
import qrcode.color.Colors

context(clicky: Clicky)
fun Route.roomQRRoutes() {
    fun generateCode(room: RoomViewModel, color: Int, background: Int) = QRCode.ofRoundedSquares()
        .withColor(color)
        .withBackgroundColor(background)
        .build("${clicky.config.site}/room/${room.code}")

    get("/qr.png") {
        ensureRoomOwner(call) { room, _ ->
            val qr = generateCode(room, Colors.BLACK, 0xf4f4f5)
            call.respondOutputStream(ContentType.Image.PNG) {
                qr.render().writeImage(this)
            }
        }
    }
    get("/qr-dark.png") {
        ensureRoomOwner(call) { room, _ ->
            val qr = generateCode(room, Colors.WHITE, Colors.TRANSPARENT)
            call.respondOutputStream(ContentType.Image.PNG) {
                qr.render().writeImage(this)
            }
        }
    }
}