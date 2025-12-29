package me.dvyy.clicky.ui.templates

import io.ktor.htmx.html.*
import kotlinx.html.*
import me.dvyy.clicky.ui.components.siteFooter

fun HTML.defaultTemplate(title: String? = null, body: MAIN.() -> Unit) {
    head {
        meta(charset = "utf-8")
        meta(name = "viewport", content = "width=device-width, initial-scale=1.0")
        link(rel = "icon", href = "/icon.svg", type = "image/svg+xml")
        script { src = "/scripts/htmx.min.js" }
        script { src = "/scripts/htmx-ext-sse.js" }
        title { +(title ?: "Clicky") }

        link(rel = LinkRel.stylesheet, href = "/styles/style.css")
    }

    body("bg-zinc-100 dark:bg-zinc-900 h-screen mb-auto flex flex-col justify-between overflow-x-hidden") {
        attributes.hx {
            ext = "sse"
        }
        main("py-8 prose prose-zinc dark:prose-invert max-w-none prose-h2:mb-2 prose-h2:mt-6") {
            body.invoke(this)
        }
        siteFooter()
    }
}