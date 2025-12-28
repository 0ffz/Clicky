package me.dvyy.me.dvyy.clicky.ui.pages


import io.ktor.htmx.html.hx
import kotlinx.html.*
import me.dvyy.me.dvyy.clicky.data.routes.RoomViewModel
import me.dvyy.me.dvyy.clicky.ui.components.adminOptions
import me.dvyy.me.dvyy.clicky.ui.components.barChart
import me.dvyy.me.dvyy.clicky.ui.components.qrCode
import me.dvyy.me.dvyy.clicky.ui.components.voteOptions
import me.dvyy.me.dvyy.clicky.ui.templates.defaultTemplate

fun HTML.resultsPage(isRoomOwner: Boolean, room: RoomViewModel) = defaultTemplate {
    val code = room.code
    content {
        div("flex items-start justify-between gap-4") {
            div("flex-1") {
                h1("mb-0 mt-2 text-5xl") {
                    +code
                }
                h2("mb-0 mt-0 break-words font-normal") {
                    +"${room.name} "
                }
                a(href = "/") { span { +"< Back" } }
            }
            if (isRoomOwner) {
                div("not-prose") {
                    qrCode(code, "w-32 h-32")
                }
            }
        }
    }
//        div("flex flex-col items-center justify-center gap-4") {
    div {
//        attributes["ws-connect"] = "/rooms/$room/live"
        attributes["sse-connect"] = "/rooms/$code/live"
        div {
            attributes["sse-swap"] = "room-not-found"
            attributes.hx {
                swap = "outerHTML"
                target = "body"
            }
        }
        div("flex flex-col overflow-x-auto py-2") {
            if (isRoomOwner) div {
                attributes["sse-swap"] = "chart"
                id = "chart"
                barChart(code, hidden = false, listOf())
            }
        }
        content {
            div {
                id = "options"
                attributes["sse-swap"] = "options"
                voteOptions(code, listOf())
            }
            if (isRoomOwner) {
                details {
                    open = true
                    summary {
                        h2("inline-block") { +"Room options" }
                    }
                    div {
                        id = "admin"
                        adminOptions(code)
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