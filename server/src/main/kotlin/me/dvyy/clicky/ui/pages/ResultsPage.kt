package me.dvyy.clicky.ui.pages


import io.ktor.htmx.html.*
import kotlinx.html.*
import me.dvyy.clicky.server.data.RoomViewModel
import me.dvyy.clicky.ui.components.adminOptions
import me.dvyy.clicky.ui.components.barChart
import me.dvyy.clicky.ui.components.centeredContent
import me.dvyy.clicky.ui.components.qrCode
import me.dvyy.clicky.ui.components.voteOptions
import me.dvyy.clicky.ui.templates.defaultTemplate

fun HTML.resultsPage(
    isRoomOwner: Boolean,
    room: RoomViewModel,
) = defaultTemplate(title = "Clicky - " + room.name) {
    val code = room.code
    centeredContent {
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

    div {
        attributes["sse-connect"] = "/room/$code/live"
        div {
            attributes["sse-swap"] = "room-not-found"
            attributes.hx {
                swap = "outerHTML"
                target = "body"
            }
        }
        div("flex flex-col overflow-x-auto py-2") {
            if (isRoomOwner) barChart(code, hidden = false, room.options.value.map { it to 0 })
        }
        centeredContent {
            voteOptions(code, room.options.value)
            if (isRoomOwner) {
                details {
                    open = true
                    summary {
                        h2("inline-block") { +"Room options" }
                    }
                    adminOptions(code)
                }
            }
        }
    }
}
