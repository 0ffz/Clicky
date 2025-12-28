package me.dvyy.me.dvyy.clicky.ui.components

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.img


fun FlowContent.qrCode(room: String, classes: String = "") {
    val url = "/rooms/$room/qr.png"
    val darkUrl = "/rooms/$room/qr-dark.png"
    a(href = url, target = "_blank") {
        img(classes = "dark:hidden $classes") { src = url }
        img(classes = "hidden dark:block $classes") { src = darkUrl }
    }
}