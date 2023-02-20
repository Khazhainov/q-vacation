package com.quotalogic

import com.quotalogic.kafka.buildConsumer
import io.ktor.server.application.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*
import com.quotalogic.plugins.*

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    install(BackgroundJobPlugin) {
        name = "DayOff-Consumer-Job"
        job = buildConsumer<String, String>()
    }
    configureSerialization()
    configureDatabases()
    configureRouting()
}

