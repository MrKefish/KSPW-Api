package com.mrkefish.kspw_api.data

import kotlinx.serialization.Serializable

@Serializable
data class BalanceResponse(
    val balance: Int
)

@Serializable
data class ProfileResponse(
    val id: String,
    val username: String,
    val uuid: String? = null
)

@Serializable
data class UserResponse(
    val username: String
)

@Serializable
data class CardResponse(
    val id: String,
    val name: String,
    val number: String,
    val color: Int
)
