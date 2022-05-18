package dev.aquiladvx

import dev.aquiladvx.di.mainModule
import dev.aquiladvx.plugins.*
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.ktor.util.*
import org.koin.core.KoinApplication
import org.koin.core.context.GlobalContext.startKoin
import org.koin.core.context.GlobalContext.stopKoin

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0") {
        install(KoinPlugin) {
            modules(mainModule)
        }
        configureSockets()
        configureRouting()
        configureSerialization()
        configureMonitoring()
        configureSecurity()
    }.start(wait = true)
}

object KoinPlugin : BaseApplicationPlugin<Application, KoinApplication, Unit> {

    override val key: AttributeKey<Unit>
        get() = AttributeKey("Koin")

    override fun install(
        pipeline: Application,
        configure: KoinApplication.() -> Unit
    ) {
        val monitor = pipeline.environment.monitor
        val koinApplication = startKoin(appDeclaration = configure)
        monitor.raise(EventDefinition(), koinApplication)

        monitor.subscribe(ApplicationStopping) {
            monitor.raise(EventDefinition(), koinApplication)
            stopKoin()
            monitor.raise(EventDefinition(), koinApplication)
        }
    }
}