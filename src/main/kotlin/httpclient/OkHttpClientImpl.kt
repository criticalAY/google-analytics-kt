package com.criticalay.httpclient

import com.criticalay.GoogleAnalyticsConfig
import com.criticalay.request.GaRequest
import com.criticalay.response.GaResponse
import com.criticalay.response.ValidationMessage
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.net.InetSocketAddress
import java.net.Proxy
import java.util.concurrent.TimeUnit


private val logger = KotlinLogging.logger {}

/**
 * OkHttp 4-based implementation of the GA4 Measurement Protocol HTTP transport.
 *
 * Responsibilities:
 * - Builds the OkHttpClient with optional proxy support and configurable timeouts.
 * - Serializes [GaRequest] to JSON using kotlinx.serialization.
 * - Appends `measurement_id` and `api_secret` as URL query parameters.
 * - Optionally appends `debug=1` when [GoogleAnalyticsConfig.debug] is true.
 * - Parses validation messages from the debug endpoint response body.
 * - Executes HTTP calls on [Dispatchers.IO].
 */
class OkHttpClientImpl(private val config: GoogleAnalyticsConfig) : AutoCloseable {

    @OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)
    private val json = Json {
        encodeDefaults = false
        explicitNulls   = false
    }

    private val client: OkHttpClient = buildClient()

    private fun buildClient(): OkHttpClient {
        val builder = OkHttpClient.Builder()
            .connectTimeout(config.connectTimeoutMs, TimeUnit.MILLISECONDS)
            .readTimeout(config.readTimeoutMs, TimeUnit.MILLISECONDS)

        if (!config.proxyHost.isNullOrBlank() && config.proxyPort > 0) {
            logger.debug { "Configuring proxy: ${config.proxyHost}:${config.proxyPort}" }
            builder.proxy(
                Proxy(
                    Proxy.Type.HTTP,
                    InetSocketAddress(config.proxyHost, config.proxyPort),
                )
            )
        }

        return builder.build()
    }

    /**
     * Serializes [request] to JSON and POSTs it to the GA4 MP endpoint.
     * Runs on [Dispatchers.IO] so it is safe to call from any coroutine context.
     *
     * @return [GaResponse] with the HTTP status code and any debug validation messages.
     */
    suspend fun post(request: GaRequest): GaResponse = withContext(Dispatchers.IO) {
        val bodyJson = json.encodeToString(request)
        logger.debug { "GA4 request body: $bodyJson" }

        val url = buildUrl()
        val okRequest = Request.Builder()
            .url(url)
            .post(bodyJson.toRequestBody("application/json; charset=utf-8".toMediaType()))
            .build()

        try {
            client.newCall(okRequest).execute().use { response ->
                val statusCode = response.code
                val responseBody = response.body?.string() ?: ""

                logger.debug { "GA4 response: $statusCode — $responseBody" }

                val validationMessages = if (config.debug && responseBody.isNotBlank()) {
                    parseValidationMessages(responseBody)
                } else {
                    emptyList()
                }

                GaResponse(
                    statusCode          = statusCode,
                    requestBody         = bodyJson,
                    validationMessages  = validationMessages,
                )
            }
        } catch (e: Exception) {
            logger.error(e) { "Failed to send GA4 request to $url" }
            GaResponse(statusCode = -1, requestBody = bodyJson)
        }
    }

    /**
     * Builds the full POST URL with required query parameters.
     *
     * Standard:  `https://www.google-analytics.com/mp/collect?measurement_id=G-XXX&api_secret=XXX`
     * Debug:     `https://www.google-analytics.com/debug/mp/collect?measurement_id=G-XXX&api_secret=XXX`
     */
    private fun buildUrl(): String {
        val base = config.effectiveEndpointUrl().toHttpUrl().newBuilder()
            .addQueryParameter("measurement_id", config.measurementId)
            .addQueryParameter("api_secret", config.apiSecret)
            .build()
        return base.toString()
    }

    /**
     * Parses the JSON response from the debug endpoint.
     *
     * Example debug response:
     * ```json
     * {
     *   "validationMessages": [
     *     {
     *       "fieldPath": "events[0].name",
     *       "description": "Event name is reserved.",
     *       "validationCode": "NAME_RESERVED"
     *     }
     *   ]
     * }
     * ```
     */
    private fun parseValidationMessages(responseBody: String): List<ValidationMessage> {
        return try {
            val root: JsonObject = Json.parseToJsonElement(responseBody).jsonObject
            val messagesArray: JsonArray = root["validationMessages"]?.jsonArray
                ?: return emptyList()

            messagesArray.map { element ->
                val obj = element.jsonObject
                ValidationMessage(
                    fieldPath      = obj["fieldPath"]?.jsonPrimitive?.content ?: "",
                    description    = obj["description"]?.jsonPrimitive?.content ?: "",
                    validationCode = obj["validationCode"]?.jsonPrimitive?.content ?: "",
                )
            }
        } catch (e: Exception) {
            logger.warn(e) { "Could not parse debug validation response: $responseBody" }
            emptyList()
        }
    }

    override fun close() {
        client.dispatcher.executorService.shutdown()
        client.connectionPool.evictAll()
        client.cache?.close()
    }
}