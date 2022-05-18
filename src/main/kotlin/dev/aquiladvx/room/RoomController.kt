package dev.aquiladvx.room

import dev.aquiladvx.data.MessageDataSource
import dev.aquiladvx.data.model.Message
import io.ktor.websocket.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.util.concurrent.ConcurrentHashMap

class RoomController(
    private val messageDataSource: MessageDataSource
) {
    private val members = ConcurrentHashMap<String, Member>()


    fun onJoin(
        username: String,
        sessionId: String,
        socket: WebSocketSession
    ) {
        if (members.contains(username)) {
            throw MemberAlreadyExistException()
        }

        members[username] = Member(
            username,
            sessionId,
            socket
        )
    }

    suspend fun sendMessage(senderUsername: String, messageContent: String) {
        members.values.forEach { member ->
            val message = Message(
                messageContent,
                senderUsername,
                System.currentTimeMillis()
            )

            messageDataSource.insertMessage(message)

            val parsedMessage = Json.encodeToString(message)
            member.socket.send(Frame.Text(parsedMessage))
        }
    }

    suspend fun getAllMessages(): List<Message> {
        return messageDataSource.getAllMessages()
    }

    suspend fun tryDisconnect(username: String) {
        members[username]?.socket?.close()

        if (members.containsKey(username)) {
            members.remove(username )
        }
    }
}