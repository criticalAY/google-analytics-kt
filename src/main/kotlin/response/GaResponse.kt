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

package com.criticalay.response

/**
 * Result of a GA4 Measurement Protocol request.
 *
 * GA4 may return a 2xx status even for invalid payloads. Validation errors
 * are only available in debug mode via [validationMessages].
 *
 * @property statusCode HTTP status code returned by GA4.
 * @property requestBody Serialized request payload (useful for debugging).
 * @property validationMessages Validation issues reported by the debug endpoint.
 */
data class GaResponse(
    val statusCode: Int = 0,
    val requestBody: String = "",
    val validationMessages: List<ValidationMessage> = emptyList(),
) {
    /** Returns true if the HTTP request completed with a 2xx status. */
    val isSuccess: Boolean get() = statusCode in 200..299

    /** Returns true if no validation issues were reported. */
    val isValid: Boolean get() = validationMessages.isEmpty()
}

/**
 * Validation message returned by the GA4 debug endpoint.
 *
 * @property fieldPath JSON path of the field causing the issue.
 * @property description Description of the validation problem.
 * @property validationCode GA4-specific validation code.
 */
data class ValidationMessage(
    val fieldPath: String = "",
    val description: String = "",
    val validationCode: String = "",
)
