package me.dvyy.clicky.helpers

import kotlinx.html.BODY
import kotlinx.html.body
import kotlinx.html.consumers.filter
import kotlinx.html.stream.appendHTML

fun partialHtml(block: BODY.() -> Unit): String = buildString {
    appendHTML().filter { if (it.tagName in listOf("html", "body")) SKIP else PASS }.body {
        block(this)
    }
}