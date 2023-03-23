package com.aallam.openai.client.internal.api

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.audio.Transcription
import com.aallam.openai.api.audio.TranscriptionRequest
import com.aallam.openai.api.audio.Translation
import com.aallam.openai.api.audio.TranslationRequest
import com.aallam.openai.client.Audio
import com.aallam.openai.client.internal.extension.appendFileSource
import com.aallam.openai.client.internal.http.HttpRequester
import com.aallam.openai.client.internal.http.perform
import io.ktor.client.request.forms.*

/**
 * Implementation of [Audio].
 */
internal class AudioApi(val requester: HttpRequester) : Audio {
    @BetaOpenAI
    override suspend fun transcription(request: TranscriptionRequest): Transcription {
        return when (request.responseFormat) {
            "json", "verbose_json", null -> transcriptionAsJson(request)
            else -> transcriptionAsString(request)
        }
    }

    private suspend fun transcriptionAsJson(request: TranscriptionRequest): Transcription {
        return requester.perform {
            it.submitFormWithBinaryData(url = TranscriptionPathV1, formData = formDataOf(request))
        }
    }

    private suspend fun transcriptionAsString(request: TranscriptionRequest): Transcription {
        val text = requester.perform<String> {
            it.submitFormWithBinaryData(url = TranscriptionPathV1, formData = formDataOf(request))
        }
        return Transcription(text)
    }

    /**
     * Build transcription request as form-data.
     */
    private fun formDataOf(request: TranscriptionRequest, format: String? = null) = formData {
        appendFileSource("file", request.audio)
        append(key = "model", value = request.model.id)
        request.prompt?.let { prompt -> append(key = "prompt", value = prompt) }
        val responseFormat = format ?: request.responseFormat
        responseFormat?.let { append(key = "response_format", value = it) }
        request.temperature?.let { append(key = "temperature", value = it) }
        request.language?.let { append(key = "language", value = it) }
    }

    @BetaOpenAI
    override suspend fun translation(request: TranslationRequest): Translation {
        return when (request.responseFormat) {
            "json", "verbose_json", null -> translationAsJson(request)
            else -> translationAsString(request)
        }
    }

    private suspend fun translationAsJson(request: TranslationRequest): Translation {
        return requester.perform {
            it.submitFormWithBinaryData(url = TranslationPathV1, formData = formDataOf(request))
        }
    }

    private suspend fun translationAsString(request: TranslationRequest): Translation {
        val text = requester.perform<String> {
            it.submitFormWithBinaryData(url = TranslationPathV1, formData = formDataOf(request))
        }
        return Translation(text)
    }

    /**
     * Build transcription request as form-data.
     */
    private fun formDataOf(request: TranslationRequest) = formData {
        appendFileSource("file", request.audio)
        append(key = "model", value = request.model.id)
        request.prompt?.let { prompt -> append(key = "prompt", value = prompt) }
        request.responseFormat?.let { append(key = "response_format", value = it) }
        request.temperature?.let { append(key = "temperature", value = it) }
    }

    companion object {
        private const val TranslationPathV1 = "v1/audio/translations"
        private const val TranscriptionPathV1 = "v1/audio/transcriptions"
    }
}
