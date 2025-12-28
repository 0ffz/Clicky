package me.dvyy.me.dvyy.clicky.ui.pages

import kotlinx.html.FlowContent
import kotlinx.html.HTML
import kotlinx.html.div
import kotlinx.html.h1
import me.dvyy.me.dvyy.clicky.ui.components.pageButton
import me.dvyy.me.dvyy.clicky.ui.templates.defaultTemplate

fun HTML.homePage(
    exception: Exception? = null,
    message: String? = null,
) = defaultTemplate {
    content {
        h1 {
            +"Clicky"
        }
        pageButton(exception)
    }
}

fun FlowContent.errorBanner(message: String) {
    div("w-full p-4 rounded-md bg-red-50 dark:bg-red-950/30 border border-red-200 dark:border-red-900/50 text-red-800 dark:text-red-200") { +message }
}