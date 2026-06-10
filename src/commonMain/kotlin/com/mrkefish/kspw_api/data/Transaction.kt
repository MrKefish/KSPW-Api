package com.mrkefish.kspw_api.data

import kotlinx.serialization.Serializable

@Serializable
data class TransactionRequest(
    val receiver: String,
    val amount: Int,
    val comment: String
)
