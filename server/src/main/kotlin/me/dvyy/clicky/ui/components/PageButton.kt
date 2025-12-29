package me.dvyy.clicky.ui.components

import io.ktor.htmx.html.*
import kotlinx.html.*
import me.dvyy.clicky.server.data.exceptions.RoomNotFoundException
import me.dvyy.clicky.ui.pages.errorBanner

fun DIV.pageButton(exception: Exception?) {
    h2 { +"Join room" }
    form {
        attributes.hx {
            get = "/room"
            swap = "outerHTML"
            target = "body"
        }
        label {
            +"Room code:"
            div("flex flex-wrap gap-2") {
                input(type = InputType.text, classes = "uppercase text-3xl font-bold flex-1 max-sm:w-full") {
                    id = "pageName"
                    required = true
                    name = "name"

                    if(exception is RoomNotFoundException) value = exception.roomCode
                }

                submitInput(classes = "primary max-sm:w-full") {
                    value = "Enter"
                }
            }
        }
        if (exception is RoomNotFoundException) errorBanner("Room not found: ${exception.roomCode}")
    }
    details {
        summary {
            h2("inline-block") { +"Create room" }
        }
        form {
            attributes.hx {
                post = "/room"
                swap = "outerHTML"
                target = "body"
            }
            label {
                +"Room name:"

                div("flex flex-wrap gap-2") {
                    input(type = InputType.text, classes = "flex-1 max-sm:w-full") {
                        id = "pageName"
                        required = true
                        name = "name"
                    }

                    submitInput(classes = "max-sm:w-full") {
                        value = "Create"
                    }
                }
            }
        }
    }
}