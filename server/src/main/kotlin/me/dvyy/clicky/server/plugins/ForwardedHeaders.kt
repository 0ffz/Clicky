package me.dvyy.clicky.server.plugins

import io.ktor.server.application.*
import io.ktor.server.plugins.forwardedheaders.*
import me.dvyy.clicky.server.data.Clicky

/** Reads client IPs from a reverse proxy, only enable when one is in front, otherwise clients can spoof their IP. */
context(clicky: Clicky)
fun Application.configureForwardedHeaders() {
    if (!clicky.config.trustProxyHeaders) return
    install(XForwardedHeaders) {
        // Use the address added by our proxy, earlier entries are client-controlled
        useLastProxy()
    }
}
