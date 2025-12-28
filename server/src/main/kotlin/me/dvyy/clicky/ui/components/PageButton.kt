package me.dvyy.me.dvyy.clicky.ui.components

import io.ktor.htmx.html.*
import kotlinx.html.*
import me.dvyy.me.dvyy.clicky.data.routes.RoomNotFoundException
import me.dvyy.me.dvyy.clicky.ui.pages.errorBanner

fun DIV.pageButton(exception: Exception?) {
    h2 { +"Join room" }
    optionsForm {
        attributes.hx {
            post = "/rooms"
            swap = "outerHTML"
            target = "body"
        }
        label {
            +"Room code:"

            div("flex gap-2") {
                input(type = InputType.text, classes = "uppercase") {
                    id = "pageName"
                    name = "name"
                }

                submitInput(classes = "primary") {
                    value = "Enter"
                }
            }
        }
        if (exception is RoomNotFoundException) errorBanner("Room not found: ${exception.roomId}")
    }
    h2 { +"Create room" }
    optionsForm {
        attributes.hx {
            post = "/create"
            swap = "outerHTML"
            target = "body"
        }
        label {
            +"Room name:"

            div("flex gap-2") {
                input(type = InputType.text) {
                    id = "pageName"
                    name = "name"
                }

                submitInput(classes = "primary") {
                    value = "Create"
                }
            }
        }
    }
}