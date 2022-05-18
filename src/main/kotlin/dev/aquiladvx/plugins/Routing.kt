package dev.aquiladvx.plugins

import dev.aquiladvx.room.RoomController
import dev.aquiladvx.routes.chatSocket
import dev.aquiladvx.routes.getAllMessages
import io.ktor.server.application.*
import io.ktor.server.routing.*
import org.koin.java.KoinJavaComponent.inject
fun Application.configureRouting() {

    val roomController: RoomController by inject(RoomController::class.java)
    install(Routing) {
        chatSocket(roomController)
        getAllMessages(roomController)
    }
}
