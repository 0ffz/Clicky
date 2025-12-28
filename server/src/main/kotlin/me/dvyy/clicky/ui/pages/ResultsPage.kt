package me.dvyy.me.dvyy.clicky.ui.pages


import io.ktor.htmx.html.hx
import kotlinx.html.*
import me.dvyy.me.dvyy.clicky.ui.components.adminOptions
import me.dvyy.me.dvyy.clicky.ui.components.barChart
import me.dvyy.me.dvyy.clicky.ui.components.qrCode
import me.dvyy.me.dvyy.clicky.ui.components.voteOptions
import me.dvyy.me.dvyy.clicky.ui.templates.defaultTemplate

fun HTML.resultsPage(isRoomOwner: Boolean, room: String) = defaultTemplate {
    content {
        div("flex items-start justify-between gap-4") {
            div("flex-1") {
                h1("mb-0") {
                    +"Room: "
                    span("font-mono") { +room }
                }
                a(href = "/") { span { +"< Back" } }
            }
            if (isRoomOwner) {
                qrCode(room, "w-32 h-32")
            }
        }
    }
//        div("flex flex-col items-center justify-center gap-4") {
    div {
//        attributes["ws-connect"] = "/rooms/$room/live"
        attributes["sse-connect"] = "/rooms/$room/live"
        div("flex flex-col overflow-x-auto py-2") {
            if (isRoomOwner) div {
                attributes["sse-swap"] = "chart"
                id = "chart"
                barChart(room, hidden = false, listOf())
            }
        }
        content {
            div {
                id = "options"
                attributes["sse-swap"] = "options"
                voteOptions(room, listOf())
            }
            if (isRoomOwner) {
                details {
                    open = true
                    summary {
                        h2("inline-block") { +"Room options" }
                    }
                    div {
                        id = "admin"
                        adminOptions(room)
                    }
                }
            }
        }
    }
}


fun FlowContent.content(block: DIV.() -> Unit) {
    div("mx-auto max-w-4xl px-4 lg:px-0") {
        block()
    }
}