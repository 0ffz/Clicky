package me.dvyy.clicky.server.plugins

import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import me.dvyy.clicky.server.data.Limits

/** Rejects large request bodies, chunked bodies are rejected since their size isn't known upfront. */
val RequestSizeLimit = createApplicationPlugin("RequestSizeLimit") {
    onCall { call ->
        val length = call.request.contentLength()
        when {
            call.request.header(HttpHeaders.TransferEncoding) != null -> call.respond(HttpStatusCode.LengthRequired)
            length != null && length > Limits.MAX_REQUEST_BYTES -> call.respond(HttpStatusCode.PayloadTooLarge)
        }
    }
}

fun Application.configureRequestLimits() {
    install(RequestSizeLimit)
}
