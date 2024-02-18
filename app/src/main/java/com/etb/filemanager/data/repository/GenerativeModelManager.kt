/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - GenerativeModelManager.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.data.repository

import com.etb.filemanager.BuildConfig
import com.etb.filemanager.compose.core.models.FileOperationResult
import com.etb.filemanager.compose.core.models.Message
import com.etb.filemanager.compose.core.models.Participant
import com.etb.filemanager.compose.core.presentation.util.FileOperationCallback
import com.etb.filemanager.compose.core.presentation.util.FileOperations
import com.etb.filemanager.compose.core.presentation.util.Prompt
import com.google.ai.client.generativeai.GenerativeModel
import com.google.ai.client.generativeai.type.Content
import com.google.ai.client.generativeai.type.InvalidStateException
import com.jn.airesponsematcher.extensions.process
import com.jn.airesponsematcher.processor.Output
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

class GenerativeModelManager @Inject constructor(
) {

    private val generativeModel = GenerativeModel(
        modelName = "gemini-pro", apiKey = BuildConfig.apiKey
    )

    val chat = generativeModel.startChat(
        history = Prompt.chatHistory
    )
    private val errorText = "An error occurred while generating the message."

    suspend fun generateContent(
        message: String,
        chatHistory: List<Content>,
        scope: CoroutineScope
    ): Message {

        chat.history.addAll(chatHistory)

        return try {
            val response = chat.sendMessage(message)
            processResponse(response.text, scope)

        } catch (e: InvalidStateException) {
            handleErrorState(e)
        } catch (e: Exception) {
            handleGenericError(e)
        }
    }


    private fun processResponse(text: String?, scope: CoroutineScope): Message {
        val isError = text == null
        var textResult = errorText
        var participant = Participant.ERROR

        if (!isError) {
            textResult = text!!
            participant = Participant.MODEL
        }
        val (operationResults, textProcessed) = processOutput(textResult, scope)

        return Message(
            text = textProcessed, participant = participant, operationResults = operationResults
        )
    }

    private fun handleErrorState(e: InvalidStateException): Message {
        val errorStateText = "Instance has an active request."
        return Message(
            text = "$errorStateText\n\nError:\n${e.message}", participant = Participant.ERROR
        )
    }

    private fun handleGenericError(e: Exception): Message {
        return Message(
            text = "$errorText\n\nError:\n${e.message}", participant = Participant.ERROR
        )
    }

    private fun processOutput(aiOutput: String, scope: CoroutineScope): Pair<List<FileOperationResult>, String> {
        val operationResults = mutableListOf<FileOperationResult>()
        val operationCallback = object  : FileOperationCallback {
            override fun onResolve(operationResult: FileOperationResult) {
                operationResults.add(operationResult)
            }

        }
        val fileOperation = FileOperations(scope, operationCallback)
        val output = Output(aiOutput, fileOperation.operations)
        return operationResults to output.process().trim()
    }

}
