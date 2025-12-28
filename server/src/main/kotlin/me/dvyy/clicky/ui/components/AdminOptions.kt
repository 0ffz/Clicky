package me.dvyy.me.dvyy.clicky.ui.components

import io.ktor.htmx.html.*
import kotlinx.html.*

fun FlowContent.adminOptions(room: String) {
    optionsForm {
        attributes.hx {
            post = "/rooms/$room/admin"
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
        button {
            id = "resetButton"
            attributes.hx {
                post = "/rooms/$room/admin"
                vals = """{ "action": "reset" }"""
            }
            +"Reset"
        }
    }
}

fun FlowContent.optionsForm(content: FORM.() -> Unit) {
    form {
        content()
    }
}