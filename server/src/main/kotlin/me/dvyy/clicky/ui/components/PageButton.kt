package me.dvyy.me.dvyy.clicky.ui.components

import io.ktor.htmx.html.*
import kotlinx.html.*

fun DIV.pageButton() {
    optionsForm {
        attributes.hx {
            post = "/rooms"
            pushUrl = "true"
            swap = "outerHTML"
            target = "body"
        }
        label {
            +"Room code:"

            div("flex gap-2") {
                input(type = InputType.text) {
                    id = "pageName"
                    name = "name"
                }

                submitInput(classes = "primary") {
                    value = "Enter"
                }
            }
        }
    }
}