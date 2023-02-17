package com.quotalogic.plugins

import io.ktor.server.application.*
import kotlin.concurrent.thread

val BackgroundJobPlugin = createApplicationPlugin(
        name = "BackgroundJob",
        createConfiguration = ::JobConfiguration
    ) {
        val name = pluginConfig.name
        val job = pluginConfig.job
        pluginConfig.apply {
            pluginConfig.job?.let { thread(name = pluginConfig.name) { it.run() } }
        }
    }

public class JobConfiguration {
    var name: String? = null
    var job: Runnable? = null
}
