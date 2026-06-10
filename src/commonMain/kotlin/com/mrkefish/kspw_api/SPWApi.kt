package com.mrkefish.kspw_api

import com.mrkefish.kspw_api.data.SpCard
import com.mrkefish.kspw_api.data.TransactionRequest
import com.mrkefish.kspw_api.data.WebHook
import com.mrkefish.kspw_api.data.PaymentRequest
import com.mrkefish.kspw_api.data.PaymentItem
import com.mrkefish.kspw_api.network.ApiClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType

object SpWorldsApi {
    private val client = ApiClient.httpClient

    suspend fun getBalance(card: SpCard): String {
        val response = client.get("https://spworlds.ru/api/public/card") {
            header("Authorization", card.authHeader)
        }
        return response.bodyAsText()
    }

    suspend fun getProfile(card: SpCard): String {
        val response = client.get("https://spworlds.ru/api/public/accounts/me") {
            header("Authorization", card.authHeader)
        }
        return response.bodyAsText()
    }

    suspend fun getName(card: SpCard, discordID: String): String {
        val response = client.get("https://spworlds.ru/api/public/users/$discordID") {
            header("Authorization", card.authHeader)
        }
        return response.bodyAsText()
    }
    suspend fun getCards(card: SpCard, username: String): String {
        val response = client.get("https://spworlds.ru/api/public/accounts/$username/cards") {
            header("Authorization", card.authHeader)
        }
        return response.bodyAsText()
    }
    suspend fun postTransaction(card: SpCard, receiver: String, amount: Int, comment: String): String {
        val response = client.post("https://spworlds.ru/api/public/transactions") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(TransactionRequest(receiver, amount, comment))
        }
        return response.bodyAsText()
    }
    suspend fun postTransaction(card: SpCard, transaction: TransactionRequest): String {
        val response = client.post("https://spworlds.ru/api/public/transactions") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(transaction)
        }
        return response.bodyAsText()
    }
    suspend fun changeCardWebhook(card: SpCard, webhookUrl: String): String {
        val response = client.put("https://spworlds.ru/api/public/card/webhook") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(WebHook(url = webhookUrl))
        }
        return response.bodyAsText()
    }
    suspend fun postPayment(
        card: SpCard,
        items: List<PaymentItem>,
        redirectUrl: String,
        webhookUrl: String,
        data: String
    ): String {
        val response = client.post("https://spworlds.ru/api/public/payments") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(PaymentRequest(items, redirectUrl, webhookUrl, data))
        }
        return response.bodyAsText()
    }

    suspend fun postPayment(card: SpCard, paymentRequest: PaymentRequest): String {
        val response = client.post("https://spworlds.ru/api/public/payments") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(paymentRequest)
        }
        return response.bodyAsText()
    }
}
