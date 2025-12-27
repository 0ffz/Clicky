package me.dvyy.me.dvyy.clicky.ui.components

import io.ktor.htmx.html.*
import kotlinx.html.*

fun FlowContent.voteOptions(count: Int) {
    form(classes = "flex md:justify-center p-4") {
        attributes["ws-send"] = ""
        attributes.hx {
            trigger = "change"
        }
        repeat(count) {
            radioButton(it)
        }
    }
}

fun FORM.radioButton(value: Int) {
    radioInput(classes = "flex-shrink-0 appearance-none w-16 h-16 md:w-10 md:h-10 rounded-full border-4 ${Colors.color(value)} ${Colors.border(value)} checked:border-12 md:checked:border-8 transition-all duration-200 cursor-pointer hover:scale-110") {
        id = "option$value"
        name = "option"
        this.value = "$value"
    }
}