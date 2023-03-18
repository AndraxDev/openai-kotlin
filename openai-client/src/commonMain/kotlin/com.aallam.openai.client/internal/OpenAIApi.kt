package com.aallam.openai.client.internal

import com.aallam.openai.client.*
import com.aallam.openai.client.internal.api.*
import com.aallam.openai.client.internal.http.HttpRequester

/**
 * Implementation of [OpenAI].
 *
 * @param requester http transport layer
 */
@OptIn(ExperimentalStdlibApi::class)
internal class OpenAIApi(
    private val requester: HttpRequester
) : OpenAI,
    Completions by CompletionsApi(requester),
    Files by FilesApi(requester),
    Edits by EditsApi(requester),
    Embeddings by EmbeddingsApi(requester),
    Models by ModelsApi(requester),
    Moderations by ModerationsApi(requester),
    FineTunes by FineTunesApi(requester),
    Images by ImagesApi(requester),
    Chat by ChatApi(requester),
    Audio by AudioApi(requester),
    AutoCloseable by requester
