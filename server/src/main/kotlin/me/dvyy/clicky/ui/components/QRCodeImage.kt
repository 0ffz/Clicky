package me.dvyy.clicky.ui.components

import kotlinx.html.FlowContent
import kotlinx.html.a
import kotlinx.html.img


fun FlowContent.qrCode(room: String, classes: String = "") {
    val url = "/room/$room/qr.png"
    val darkUrl = "/room/$room/qr-dark.png"
    a(href = url, target = "_blank") {
        img(classes = "dark:hidden animate-fade-in $classes") { src = url }
        img(classes = "hidden dark:block animate-fade-in $classes") { src = darkUrl }
    }
}