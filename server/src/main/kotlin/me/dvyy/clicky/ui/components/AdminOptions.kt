package me.dvyy.clicky.ui.components

import io.ktor.htmx.html.*
import kotlinx.html.*

fun FlowContent.adminOptions(room: String) {
    form(classes = "px-4") {
        attributes.hx {
            post = "/room/$room/admin"
            vals = """{ "action": "add" }"""
            swap = "none"
            on[":after-request"] = "if(event.detail.successful) this.reset()"
        }
        label {
            span { +"Add option:" }
            div("flex gap-2") {

                textInput {
                    id = "option"
                    required = true
                    name = "option"
                }
                submitInput(classes = "primary") {
                    value = "Add"
                }
            }
        }
        div("flex flex-wrap gap-4 sm:gap-2") {
            button(classes="max-sm:w-full") {
                id = "resetButton"
                attributes.hx {
                    post = "/room/$room/admin"
                    vals = """{ "action": "reset" }"""
                }
                +"Reset votes"
            }
            button(classes = "error max-sm:w-full") {
                id = "closeButton"

                attributes.hx {
                    post = "/room/$room/admin"
                    vals = """{ "action": "close" }"""
                }
                +"Close"
            }
        }
    }
}
