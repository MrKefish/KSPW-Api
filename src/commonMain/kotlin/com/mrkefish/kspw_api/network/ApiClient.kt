package com.mrkefish.kspw_api.network


import io.ktor.client.HttpClient

object ApiClient {
    val httpClient: HttpClient by lazy {
        createPlatformClient()
    }
}