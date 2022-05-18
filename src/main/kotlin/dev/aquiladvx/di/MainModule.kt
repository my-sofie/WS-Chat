package dev.aquiladvx.di

import dev.aquiladvx.data.MessageDataSource
import dev.aquiladvx.data.MessageDataSourceImpl
import dev.aquiladvx.room.RoomController
import org.koin.dsl.module
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo

val mainModule = module {
    single {
        KMongo.createClient()
            .coroutine
            .getDatabase("db")
    }

    single<MessageDataSource> {
        MessageDataSourceImpl(get())
    }

    single {
        RoomController(get())
    }
}