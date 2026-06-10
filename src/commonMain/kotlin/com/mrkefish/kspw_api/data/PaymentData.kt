package com.mrkefish.kspw_api.data

import kotlinx.serialization.Serializable

@Serializable
data class PaymentRequest(
    val items: List<PaymentItem>,
    val redirectUrl: String,
    val webhookUrl: String,
    val data: String
)

@Serializable
data class PaymentItem(
    val name: String,
    val count: Int,
    val price: Int,
    val comment: String? = null
)

@Serializable
data class PaymentResponse(
    val url: String,
    val code: String? = null,
    val card: String? = null
)
