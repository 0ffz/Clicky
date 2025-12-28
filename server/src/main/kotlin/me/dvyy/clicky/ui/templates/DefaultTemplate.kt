package me.dvyy.me.dvyy.clicky.ui.templates

import io.ktor.htmx.html.hx
import kotlinx.html.BODY
import kotlinx.html.HTML
import kotlinx.html.LinkRel
import kotlinx.html.MAIN
import kotlinx.html.body
import kotlinx.html.head
import kotlinx.html.link
import kotlinx.html.main
import kotlinx.html.meta
import kotlinx.html.script

fun HTML.defaultTemplate(body: MAIN.() -> Unit) {
    head {
        meta(charset = "utf-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        script { src = "/scripts/htmx.min.js" }
//        script { src = "/scripts/htmx-ext-ws.js" }
        script { src = "/scripts/htmx-ext-sse.js" }
        link(rel = LinkRel.stylesheet, href = "/styles/tailwind.css")
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