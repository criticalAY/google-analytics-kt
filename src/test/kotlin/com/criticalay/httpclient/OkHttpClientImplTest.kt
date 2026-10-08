/*
 * Copyright 2026 Ashish Yadav <mailtoashish693@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.criticalay.httpclient

import ch.qos.logback.classic.Level
import ch.qos.logback.classic.Logger
import ch.qos.logback.classic.spi.ILoggingEvent
import ch.qos.logback.core.read.ListAppender
import com.criticalay.GoogleAnalyticsConfig
import com.criticalay.request.GaEvent
import com.criticalay.request.GaRequest
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.slf4j.LoggerFactory

class OkHttpClientImplTest {
    private val rootLogger = LoggerFactory.getLogger(org.slf4j.Logger.ROOT_LOGGER_NAME) as Logger
    private val appender = ListAppender<ILoggingEvent>()

    @BeforeEach
    fun setUp() {
        appender.start()
        rootLogger.addAppender(appender)
    }

    @AfterEach
    fun tearDown() {
        rootLogger.detachAppender(appender)
    }

    @Test
    fun `a failed send does not log the api secret`() =
        runBlocking {
            val config =
                GoogleAnalyticsConfig(
                    measurementId = "G-TEST",
                    apiSecret = "do-not-log-me",
                    endpointUrl = "http://127.0.0.1:1/mp/collect",
                )

            val response =
                OkHttpClientImpl(config).use {
                    it.post(GaRequest(clientId = "client", events = listOf(GaEvent("test_event"))))
                }

            assertEquals(-1, response.statusCode)
            val failure = appender.list.single { it.level == Level.ERROR }
            assertFalse("do-not-log-me" in failure.formattedMessage)
            assertFalse("do-not-log-me" in failure.throwableProxy?.message.orEmpty())
        }
}
