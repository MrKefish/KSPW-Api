package com.mrkefish.kspw_api.network

import io.ktor.client.HttpClient
import io.ktor.client.plugins.cache.HttpCache
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

fun createPlatformClient(): HttpClient {
    return HttpClient {
        expectSuccess = false
        install(Logging) { level = LogLevel.INFO }
        install(ContentNegotiation) { 
            json(Json { 
                ignoreUnknownKeys = true 
            }) 
        }
        install(HttpCache)
    }
}
