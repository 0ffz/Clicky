package me.dvyy.clicky.ui.components

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
        listOf(
            "bg-red-500",
            "bg-amber-500",
            "bg-emerald-500",
            "bg-blue-500",
            "bg-indigo-500",
            "bg-fuchsia-500",
            "bg-orange-500",
            "bg-lime-500",
            "bg-teal-500",
            "bg-violet-500",
            "bg-rose-500",
            "bg-yellow-500",
            "bg-green-500",
            "bg-cyan-500",
            "bg-purple-500",
            "bg-pink-500",
        )
    private val borders = listOf(
        "border-red-700",
        "border-amber-700",
        "border-emerald-700",
        "border-blue-700",
        "border-indigo-700",
        "border-fuchsia-700",
        "border-orange-700",
        "border-lime-700",
        "border-teal-700",
        "border-violet-700",
        "border-rose-700",
        "border-yellow-700",
        "border-green-700",
        "border-cyan-700",
        "border-purple-700",
        "border-pink-700",
    )


    fun border(index: Int) = borders.getOrElse(index) { "border-sky-600" }
    fun color(index: Int) = colors.getOrElse(index) { "bg-sky-500" }
}

fun FlowContent.barChart(room: String, hidden: Boolean, map: List<Pair<String, Int>>) {
    div("wrapper") {
        div("grid grid-flow-col auto-cols-min grid-rows-[auto_1fr] gap-x-4 px-4") {
            if (hidden) return@div
            val max = map.maxByOrNull { it.second }?.second ?: 1
            map.forEachIndexed { index, (column, value) ->
                div("grid grid-rows-[subgrid] row-span-2 items-center gap-2 w-20") {
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
                                post = "/room/$room/admin"
                                vals = """{ "action": "delete", "option": "$index" }"""
                            }
                            icon("trash", "demphasized")
                        }
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