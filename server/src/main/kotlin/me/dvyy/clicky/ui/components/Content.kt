package me.dvyy.clicky.ui.components

import kotlinx.html.*
import me.dvyy.shocky.icons.Icons

fun FlowContent.centeredContent(block: DIV.() -> Unit) {
    div("mx-auto max-w-4xl px-4 lg:px-0") {
        block()
    }
}

fun FlowContent.siteFooter() =
    footer("prose prose-zinc dark:prose-invert max-w-none prose-zinc text-sm text-zinc-500 prose-a:text-zinc-600 dark:prose-a:text-zinc-400 dark:prose-invert py-4") {
        centeredContent {
            span {
                +"Clicky is free and open source software, learn how to contribute or host it yourself "
                a(href = "https://github.com/0ffz/Clicky") { +"here!" }
            }
        }
    }
