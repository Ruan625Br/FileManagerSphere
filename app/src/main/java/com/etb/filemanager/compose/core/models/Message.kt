/*
 * Copyright (c)  2024  Juan Nascimento
 * Part of FileManagerSphere - Message.kt
 * SPDX-License-Identifier: GPL-3.0-or-later
 * More details at: https://www.gnu.org/licenses/
 */

package com.etb.filemanager.compose.core.models

import android.net.Uri
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

enum class Participant {
    USER,
    MODEL,
    ERROR,
    USER_ERROR
}

@Serializable
data class Message(
    val id: Int = 0 ,
    val text: String = "",
    val participant: Participant = Participant.MODEL,
    val isPending: Boolean = false,
    val operationResults: List<FileOperationResult> = emptyList()
){
    override fun toString(): String {
        return "id: $id\ntext: $text\nparticipant: $participant\nisPending: $isPending"
    }
}

object UriSerializer : KSerializer<Uri> {
    override val descriptor: SerialDescriptor
        get() = PrimitiveSerialDescriptor("Uri", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Uri {
        return Uri.parse(decoder.decodeString())
    }

    override fun serialize(encoder: Encoder, value: Uri) {
       encoder.encodeString(value.toString())
    }
}