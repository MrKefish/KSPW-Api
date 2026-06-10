package com.mrkefish.kspw_api.data

import io.ktor.util.encodeBase64

data class SpCard(
    val id: String,
    val token: String
) {
    val authHeader: String get() = "Bearer " + "$id:$token".encodeBase64()
}
