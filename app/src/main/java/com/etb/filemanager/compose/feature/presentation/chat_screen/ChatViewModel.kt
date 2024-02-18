/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - ChatViewModel.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.feature.presentation.chat_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.etb.filemanager.compose.core.extensions.hasPendingMessage
import com.etb.filemanager.compose.core.extensions.toContent
import com.etb.filemanager.compose.core.models.Chat
import com.etb.filemanager.compose.core.models.Message
import com.etb.filemanager.compose.core.models.Participant
import com.etb.filemanager.compose.mapper.toChat
import com.etb.filemanager.compose.mapper.toChatEntity
import com.etb.filemanager.data.entities.ChatEntity
import com.etb.filemanager.data.repository.ChatRepository
import com.etb.filemanager.data.repository.GenerativeModelManager
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val modelRepository: GenerativeModelManager,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state = _state.asStateFlow()

    init {
        loadChatList()
        if (createNewChatIsAvailable()){
            newChat()
        }
    }

    fun sendMessage(message: String) {
        viewModelScope.launch {
            val previousMessages = _state.value.chat.messages
            val messages = mutableListOf<Message>()
            val userMessage = Message(
                text = message, participant = Participant.USER,
            )
            val loadingMessage = Message(
                text = "Generating message...",
                participant = Participant.MODEL,
                isPending = true
            )
            messages.apply {
                addAll(previousMessages)
                add(userMessage)
                add(loadingMessage)
            }
            updateMessages(messages)
            chatRepository.upsert(_state.value.chat.toChatEntity())
            val modelMessage = modelRepository.generateContent(
                message = message,
                chatHistory = previousMessages.toContent(),
                scope = this
            )

            messages.remove(loadingMessage)
            messages.add(modelMessage)

            if (modelMessage.participant == Participant.ERROR) {
                val index = messages.size - 2
                val msg = messages[index].copy(participant = Participant.USER_ERROR)
                messages[index] = msg
            }

            updateMessages(messages)
            chatRepository.upsert(_state.value.chat.toChatEntity())
        }

    }

    private fun updateMessages(messages: List<Message>) {
        val chat = _state.value.chat.copy(
            messages = messages)

        _state.update {
            it.copy(chat = chat)
        }
        setCurrentChat(chat)

    }

    private fun loadChatList() {
        viewModelScope.launch {
            chatRepository.getAllChat().collectLatest { listChatEntity ->
                _state.update { listChat ->
                    val list = listChatEntity.map { it.toChat() }.reversed()
                    listChat.copy(chatList = list)
                }
            }
        }
    }

    fun newChat() {
        val chatEntity = ChatEntity()

        viewModelScope.launch {
            if (!_state.value.chat.messages.hasPendingMessage()) {
                chatRepository.upsert(chatEntity)
                delay(500)
                setCurrentChat(_state.value.chatList.first())
            }
        }
    }

    fun setCurrentChat(chat: Chat) {
        if (!_state.value.chat.messages.hasPendingMessage()) {
            _state.update {
                it.copy(chat = chat)
            }
        }
    }

   private fun createNewChatIsAvailable(): Boolean {
        val chatList = _state.value.chatList
       return chatList.isNotEmpty() && chatList.last().messages.isNotEmpty()
    }
}