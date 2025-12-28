package me.dvyy.me.dvyy.clicky.ui.components

import io.ktor.htmx.html.*
import kotlinx.html.*
import me.dvyy.shocky.icons.Icons

/** @return a random hex color code based on the string. */
//fun String.colorHash(): String {
//    val hash = this.hashCode()
//    val r = (hash and 0xFF0000) shr 16
//    val g = (hash and 0x00FF00) shr 8
//    val b = hash and 0x0000FF
//    return "#%02x%02x%02x".format(r, g, b)
//}

object Colors {
    private val colors =
        listOf("bg-red-500", "bg-amber-500", "bg-emerald-500", "bg-sky-500", "bg-blue-500", "bg-indigo-500")
    private val borders = listOf(
        "border-red-600",
        "border-amber-600",
        "border-emerald-600",
        "border-sky-600",
        "border-blue-600",
        "border-indigo-600"
    )


    fun border(index: Int) = borders.getOrElse(index) { "border-blue-600" }
    fun color(index: Int) = colors.getOrElse(index) { "bg-blue-500" }
}

fun FlowContent.barChart(room: String, hidden: Boolean, map: List<Pair<String, Int>>) {
    div("grid grid-flow-col auto-cols-min grid-rows-[auto_1fr] gap-x-4 md:justify-center") {
        if (hidden) return@div
        val max = map.maxByOrNull { it.second }?.second ?: 1
        map.forEachIndexed { index, (column, value) ->
            div("grid grid-rows-[subgrid] row-span-2 items-center gap-2  w-20") {
                div("flex-1 flex items-end justify-center h-64") {
                    val percentage = if (max > 0) (value.toDouble() / max * 100).toInt() else 0
                    div("${Colors.color(index)} ${Colors.border(index)} border-4 w-20 rounded-t-lg flex flex-col items-center justify-start pt-2 text-xs text-white font-semibold transition-all duration-300") {
                        id = "bar-$index"
                        style = "height: ${percentage}%"
                        if (percentage > 10) {
                            +value.toString()
                        }
                    }
                }
                div("text-sm text-center font-medium items-start h-full gap-1") {
                    span("break-words wrap-anywhere min-w-0 text-ellipsis") { +column }
                    button {
                        attributes.hx {
                            post = "/rooms/$room/admin"
                            vals = """{ "action": "delete", "option": "$index" }"""
                        }
                        icon("trash", "demphasized")
                    }
                }
            }
        }
    }
}

fun FlowContent.icon(name: String, classes: String? = null) {
    div(classes = classes) {
        unsafe { +Icons.renderFromMarkdown(":$name:") }
    }
}