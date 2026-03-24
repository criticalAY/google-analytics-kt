package com.criticalay.internal


import com.criticalay.GoogleAnalytics
import com.criticalay.GoogleAnalyticsConfig
import com.criticalay.httpclient.OkHttpClientImpl
import com.criticalay.request.CustomHit
import com.criticalay.request.EventHit
import com.criticalay.request.ExceptionHit
import com.criticalay.request.GaEvent
import com.criticalay.request.GaRequest
import com.criticalay.request.PageViewHit
import com.criticalay.request.ScreenViewHit
import com.criticalay.request.TimingHit
import com.criticalay.response.GaResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.atomic.AtomicLong
import kotlin.random.Random

private val logger = KotlinLogging.logger {}

/**
 * Core implementation of [GoogleAnalytics].
 *
 * Thread-safety is achieved through Kotlin's [Mutex] for the batch buffer and
 * [AtomicLong] for statistics counters.  All HTTP calls run on [Dispatchers.IO]
 * inside the private [scope].
 *
 * Do not instantiate directly — use [com.criticalay.GoogleAnalyticsBuilder].
 */
class GaImpl(
    override val config: GoogleAnalyticsConfig,
    private val httpClient: OkHttpClientImpl,
) : GoogleAnalytics {

    private val scope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    // Mutex protecting the mutable batch buffer
    private val batchMutex = Mutex()
    private val batchBuffer = mutableListOf<GaEvent>()

    // Per-event-name hit counters (best-effort, not guaranteed under extreme concurrency)
    private val hitCounters = mutableMapOf<String, AtomicLong>()
    private val countersMutex = Mutex()

    override var inSample: Boolean = true
        private set

    init {
        performSamplingElection()
    }

    override fun performSamplingElection(): Boolean {
        val roll = Random.nextInt(1, 101) // 1..100 inclusive
        inSample = roll <= config.samplePercentage
        if (inSample) {
            logger.info { "GA4: participating in analytics sample (roll=$roll, threshold=${config.samplePercentage})" }
        } else {
            logger.info { "GA4: NOT participating in analytics sample (roll=$roll, threshold=${config.samplePercentage})" }
        }
        return inSample
    }

    override fun pageView(clientId: String): PageViewHit = PageViewHit(clientId, this)
    override fun event(clientId: String): EventHit = EventHit(clientId, this)
    override fun screenView(clientId: String): ScreenViewHit = ScreenViewHit(clientId, this)
    override fun exception(clientId: String): ExceptionHit = ExceptionHit(clientId, this)
    override fun timing(clientId: String): TimingHit = TimingHit(clientId, this)
    override fun custom(clientId: String, name: String): CustomHit = CustomHit(clientId, name, this)

    override suspend fun send(request: GaRequest): GaResponse {
        if (!config.enabled) {
            logger.debug { "GA4: SDK disabled — dropping hit" }
            return GaResponse()
        }
        if (!inSample) {
            logger.debug { "GA4: not in sample — dropping hit" }
            return GaResponse()
        }

        return if (config.batchingEnabled) {
            addToBatch(request)
            GaResponse() // Batched — no immediate response
        } else {
            val response = httpClient.post(request)
            recordStats(request)
            logValidationWarnings(response)
            response
        }
    }

    override fun sendAsync(request: GaRequest) {
        scope.launch {
            try {
                send(request)
            } catch (e: Exception) {
                logger.error(e) { "GA4: async send failed" }
            }
        }
    }

    private suspend fun addToBatch(request: GaRequest) {
        // Merge the events from this request into the shared batch buffer.
        // Each Ga4Request carries its own clientId, so when batching we
        // combine events assuming the same clientId for the flush request.
        batchMutex.withLock {
            batchBuffer.addAll(request.events)
            logger.debug { "GA4: batch buffer size=${batchBuffer.size}" }
        }
        maybeFlushBatch(force = false)
    }

    private suspend fun maybeFlushBatch(force: Boolean) {
        batchMutex.withLock {
            if (batchBuffer.isEmpty()) return
            if (!force && batchBuffer.size < config.batchSize) return

            // Drain the buffer and send
            val chunk = batchBuffer.toList()
            batchBuffer.clear()
            logger.debug { "GA4: flushing batch of ${chunk.size} events" }

            // For batched sends we use a synthetic clientId — in a real app the
            // caller should use per-user clientIds via individual requests.
            // If batching is used, it's assumed all events share a logical sender.
            val batchRequest = GaRequest(
                clientId = "batch-flush",
                events   = chunk.take(25), // hard GA4 limit
            )
            val response = httpClient.post(batchRequest)
            logValidationWarnings(response)
        }
    }

    override suspend fun flush() = maybeFlushBatch(force = true)

    private suspend fun recordStats(request: GaRequest) {
        if (!config.enabled) return
        countersMutex.withLock {
            request.events.forEach { event ->
                hitCounters.getOrPut(event.name) { AtomicLong(0) }.incrementAndGet()
            }
        }
    }

    /** Returns a snapshot of hit counts keyed by event name. */
    suspend fun stats(): Map<String, Long> = countersMutex.withLock {
        hitCounters.mapValues { it.value.get() }
    }

    private fun logValidationWarnings(response: GaResponse) {
        if (response.validationMessages.isNotEmpty()) {
            response.validationMessages.forEach { msg ->
                logger.warn {
                    "GA4 validation [${msg.validationCode}] at '${msg.fieldPath}': ${msg.description}"
                }
            }
        }
    }

    override fun close() {
        runBlocking {
            try { flush() } catch (_: Exception) {}
        }
        scope.cancel()
        try { httpClient.close() } catch (_: Exception) {}
        logger.info { "GA4: SDK closed" }
    }
}
