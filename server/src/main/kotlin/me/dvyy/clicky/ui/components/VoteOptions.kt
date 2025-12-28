package me.dvyy.me.dvyy.clicky.ui.components

import io.ktor.htmx.html.*
import kotlinx.html.*

fun FlowContent.voteOptions(room: String, options: List<String>) {
    form(classes = "grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 md:justify-center p-4 gap-2") {
        attributes.hx {
            post = "/rooms/$room/vote"
            swap = "none"
            trigger = "change"
        }
        options.forEachIndexed { index, name ->
            radioButton(index, name)
        }
    }
}

fun FORM.radioButton(index: Int, name: String) {
    div("grid grid-rows-[subgrid] row-span-2") {
        span("text-xl md:text-lg font-bold self-end wrap-anywhere") { +"$name:" }
        radioInput(
            classes = """flex-shrink-0 appearance-none w-full h-16 rounded-md border-4 
            ${Colors.color(index)} ${Colors.border(index)}
            transition-transform duration-200 cursor-pointer 
            checked:border-12 md:checked:border-8 checked:scale-105 md:checked:scale-103""".trimIndent()
        ) {
            id = "option$index"
            this.name = "option"
            this.value = "$index"
        }

    }
}