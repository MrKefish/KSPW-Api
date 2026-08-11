package com.mrkefish.kspw_api.data

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val balance: Int,
    val webhook: String
)

@Serializable
data class ProfileResponse(
    val id: String,
    val username: String,
    val minecraftUUID: String,
    val status: String,
    val roles: List<String>,
    val city: City? = null,
    val cards: List<CardResponse>,
    val createdAt: String
)

@Serializable
data class City(
    val id: String,
    val name: String,
    val description: String,
    val x: Int,
    val z: Int,
    val netherX: Int,
    val netherZ: Int,
    val isMayor: Boolean
)

@Serializable
data class UserResponse(
    val username: String? = null,
    val uuid: String? = null
)

@Serializable
data class CardResponse(
    val id: String? = null, // Missing in some endpoints
    val name: String,
    val number: String,
    val color: Int? = null // Missing in some endpoints
)

@Serializable
data class TransactionResponse(
    val balance: Int
)

@Serializable
data class PaymentResponse(
    val url: String,
    val code: String,
    val card: String
)
