package me.dvyy.me.dvyy.clicky.ui.pages

import kotlinx.html.HTML
import kotlinx.html.h1
import me.dvyy.me.dvyy.clicky.ui.components.pageButton
import me.dvyy.me.dvyy.clicky.ui.templates.defaultTemplate

fun HTML.homePage() = defaultTemplate {
    content {
        h1 {
            +"Clicky"
        }
        pageButton()
    }
}