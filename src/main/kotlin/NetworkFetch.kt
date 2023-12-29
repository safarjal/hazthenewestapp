@file:Suppress("unused")

import io.ktor.client.fetch.*
import io.ktor.client.fetch.Headers as FetchHeaders
import io.ktor.client.fetch.RequestInit
import io.ktor.http.*
import io.ktor.http.Headers
import io.ktor.utils.io.core.*
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import org.w3c.fetch.*
import org.w3c.fetch.Response

suspend inline fun fetch(
    url: Url,
    method: HttpMethod,
    headers: Headers = Headers.Empty,
    mode: RequestMode = RequestMode.CORS,
    credentials: RequestCredentials = RequestCredentials.SAME_ORIGIN,
    cache: RequestCache = RequestCache.DEFAULT,
    redirect: RequestRedirect = RequestRedirect.FOLLOW,
    referrer: Referrer = Referrer.Client,
    referrerPolicy: ReferrerPolicy = ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN,
    integrity: String = "",
    keepAlive: Boolean = false,
) = fetch<Any?>(
    url,
    method,
    body = null,
    headers,
    mode,
    credentials,
    cache,
    redirect,
    referrer,
    referrerPolicy,
    integrity,
    keepAlive
)

suspend inline fun <reified T> fetch(
    url: Url,
    method: HttpMethod,
    body: T,
    headers: Headers = Headers.Empty,
    mode: RequestMode = RequestMode.CORS,
    credentials: RequestCredentials = RequestCredentials.SAME_ORIGIN,
    cache: RequestCache = RequestCache.DEFAULT,
    redirect: RequestRedirect = RequestRedirect.FOLLOW,
    referrer: Referrer = Referrer.Client,
    referrerPolicy: ReferrerPolicy = ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN,
    integrity: String = "",
    keepAlive: Boolean = false,
) = suspendFetch(
    url.toString(),
    buildRequestInit(
        method,
        body,
        headers,
        mode,
        credentials,
        cache,
        redirect,
        referrer,
        referrerPolicy,
        integrity,
        keepAlive
    )
)

inline fun <reified T> buildRequestInit(
    method: HttpMethod,
    body: T,
    headers: Headers = Headers.Empty,
    mode: RequestMode = RequestMode.CORS,
    credentials: RequestCredentials = RequestCredentials.SAME_ORIGIN,
    cache: RequestCache = RequestCache.DEFAULT,
    redirect: RequestRedirect = RequestRedirect.FOLLOW,
    referrer: Referrer = Referrer.Client,
    referrerPolicy: ReferrerPolicy = ReferrerPolicy.STRICT_ORIGIN_WHEN_CROSS_ORIGIN,
    integrity: String = "",
    keepAlive: Boolean = false,
): RequestInit = buildJsObject {
    this.method = method.value

    val bodyString = body?.let { Json.encodeToString(it) }
    if (bodyString != null) {
        this.body = bodyString
    }

    val finalHeaders = Headers.build {
        appendAll(headers)

        remove(HttpHeaders.Accept)
        remove(HttpHeaders.ContentType)
        remove(HttpHeaders.ContentLength)

        append(HttpHeaders.Accept, ContentType.Application.Json)
        append(HttpHeaders.ContentType, ContentType.Application.Json)
        if (bodyString != null) {
            append(HttpHeaders.ContentLength, bodyString.toByteArray().size.toString())
        }
    }
    @Suppress("UNCHECKED_CAST_TO_EXTERNAL_INTERFACE")
    this.headers = finalHeaders.entries()
        .fold(js("new Headers()") as FetchHeaders) { jsHeaders, (key, values) ->
            jsHeaders.apply { append(key, values.joinToString(",")) }
        }

    this.mode = mode
    this.credentials = credentials
    this.cache = cache
    this.redirect = redirect
    this.referrer = referrer.value
    this.referrerPolicy = referrerPolicy.value
    this.integrity = integrity
    this.keepalive = keepAlive
}

enum class ReferrerPolicy(val value: String) {
    EMPTY(""),
    NO_REFERRER("no-referrer"),
    NO_REFERRER_WHEN_DOWNGRADE("no-referrer-when-downgrade"),
    SAME_ORIGIN("same-origin"),
    ORIGIN("origin"),
    STRICT_ORIGIN("strict-origin"),
    ORIGIN_WHEN_CROSS_ORIGIN("origin-when-cross-origin"),
    STRICT_ORIGIN_WHEN_CROSS_ORIGIN("strict-origin-when-cross-origin"),
    UNSAFE_URL("unsafe-url)"),
}

sealed interface Referrer {
    val value: String

    object None : Referrer {
        override val value = ""
    }

    object Client : Referrer {
        override val value = "about:client"
    }

    @Suppress("MemberVisibilityCanBePrivate")
    class Url(val url: io.ktor.http.Url) : Referrer {
        override val value: String get() = url.toString()
    }
}

suspend fun suspendFetch(
    url: String,
    init: RequestInit
): Response = suspendCancellableCoroutine { continuation ->
    @Suppress("UnsafeCastFromDynamic")
    val controller: AbortController = js("new AbortController()")
    init.signal = controller.signal
    continuation.invokeOnCancellation { controller.abort() }

    fetch(url, init).then(
        onFulfilled = { response -> continuation.resumeWith(Result.success(response)) },
        onRejected =  { error    -> continuation.resumeWith(Result.failure(error))    }
    )
}
