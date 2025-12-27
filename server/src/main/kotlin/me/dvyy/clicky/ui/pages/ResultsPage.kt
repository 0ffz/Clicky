package me.dvyy.me.dvyy.clicky.ui.pages


import kotlinx.html.*
import me.dvyy.me.dvyy.clicky.ui.components.adminOptions
import me.dvyy.me.dvyy.clicky.ui.components.barChart
import me.dvyy.me.dvyy.clicky.ui.components.voteOptions
import me.dvyy.me.dvyy.clicky.ui.templates.defaultTemplate

fun HTML.resultsPage(isRoomOwner: Boolean, room: String) = defaultTemplate {
    content {
        h1("mb-0") { +"Room $room" }
        a(href = "/") { span { +"< Back" } }
    }
//        div("flex flex-col items-center justify-center gap-4") {
    div {
        attributes["ws-connect"] = "/rooms/$room/live"
        div("flex flex-col overflow-x-auto") {
            if (isRoomOwner) div {
                id = "chart"
                barChart(room, hidden = false, listOf())
            }
            div {
                id = "options"
                voteOptions(0)
            }
        }
        content {
            h2 { +"Room options" }
            if (isRoomOwner) div {
                id = "admin"
                adminOptions(room)
            }
        }
    }
}


fun FlowContent.content(block: DIV.() -> Unit) {
    div("mx-auto max-w-4xl px-4 lg:px-0") {
        block()
    }
}