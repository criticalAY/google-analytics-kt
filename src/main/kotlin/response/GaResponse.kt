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
