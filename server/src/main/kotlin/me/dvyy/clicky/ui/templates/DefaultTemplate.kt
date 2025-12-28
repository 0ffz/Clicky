package me.dvyy.clicky.ui.templates

import io.ktor.htmx.html.*
import kotlinx.html.*

fun HTML.defaultTemplate(body: MAIN.() -> Unit) {
    head {
        meta(charset = "utf-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        script { src = "/scripts/htmx.min.js" }
        script { src = "/scripts/htmx-ext-sse.js" }
        link(rel = LinkRel.stylesheet, href = "/styles/style.css")
    }

    body("bg-zinc-100 dark:bg-zinc-900 min-h-screen overflow-x-hidden") {
        attributes.hx {
            ext = "sse"
        }
        main("min-h-screen py-8 prose prose-zinc dark:prose-invert max-w-none") {
            body.invoke(this)
        }
    }
}