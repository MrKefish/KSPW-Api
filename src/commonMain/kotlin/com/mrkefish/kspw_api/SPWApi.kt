package com.mrkefish.kspw_api

import com.mrkefish.kspw_api.data.*
import com.mrkefish.kspw_api.network.ApiClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object SpWorldsApi {
    private val client = ApiClient.httpClient
    private val json = Json { ignoreUnknownKeys = true }

    private suspend inline fun <reified T> safeParse(response: HttpResponse, crossinline transform: (T) -> Any = { it as Any }): Any {
        val text = response.bodyAsText()

        if (response.status != HttpStatusCode.OK) {
            return text
        }

        return try {
            val parsed = json.decodeFromString<T>(text)
            transform(parsed)
        } catch (e: Exception) {
            text
        }
    }

    suspend fun getBalance(card: SpCard): Any {
        val response = client.get("https://spworlds.ru/api/public/card") {
            header("Authorization", card.authHeader)
        }
        return safeParse<BalanceResponse>(response) { it.balance }
    }

    suspend fun getProfile(card: SpCard): Any {
        val response = client.get("https://spworlds.ru/api/public/accounts/me") {
            header("Authorization", card.authHeader)
        }
        return safeParse<ProfileResponse>(response)
    }

    suspend fun getName(card: SpCard, discordID: String): Any {
        val response = client.get("https://spworlds.ru/api/public/users/$discordID") {
            header("Authorization", card.authHeader)
        }
        return safeParse<UserResponse>(response) { it.username }
    }

    suspend fun getCards(card: SpCard, username: String): Any {
        val response = client.get("https://spworlds.ru/api/public/accounts/$username/cards") {
            header("Authorization", card.authHeader)
        }
        return safeParse<List<CardResponse>>(response)
    }

    suspend fun postTransaction(card: SpCard, receiver: String, amount: Int, comment: String): Any {
        val response = client.post("https://spworlds.ru/api/public/transactions") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(TransactionRequest(receiver, amount, comment))
        }
        return response.bodyAsText()
    }

    suspend fun postTransaction(card: SpCard, transaction: TransactionRequest): Any {
        val response = client.post("https://spworlds.ru/api/public/transactions") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(transaction)
        }
        return response.bodyAsText()
    }

    suspend fun changeCardWebhook(card: SpCard, webhookUrl: String): Any {
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
    ): Any {
        val response = client.post("https://spworlds.ru/api/public/payments") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(PaymentRequest(items, redirectUrl, webhookUrl, data))
        }
        return safeParse<PaymentResponse>(response)
    }

    suspend fun postPayment(card: SpCard, paymentRequest: PaymentRequest): Any {
        val response = client.post("https://spworlds.ru/api/public/payments") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(paymentRequest)
        }
        return safeParse<PaymentResponse>(response)
    }

    // Blocking methods
    fun getBalanceBlocking(card: SpCard) = runBlocking { getBalance(card) }
    fun getProfileBlocking(card: SpCard) = runBlocking { getProfile(card) }
    fun getNameBlocking(card: SpCard, discordID: String) = runBlocking { getName(card, discordID) }
    fun getCardsBlocking(card: SpCard, username: String) = runBlocking { getCards(card, username) }
    fun postTransactionBlocking(card: SpCard, receiver: String, amount: Int, comment: String) = runBlocking { postTransaction(card, receiver, amount, comment) }
    fun postTransactionBlocking(card: SpCard, transaction: TransactionRequest) = runBlocking { postTransaction(card, transaction) }
    fun changeCardWebhookBlocking(card: SpCard, webhookUrl: String) = runBlocking { changeCardWebhook(card, webhookUrl) }
    fun postPaymentBlocking(card: SpCard, items: List<PaymentItem>, redirectUrl: String, webhookUrl: String, data: String) = runBlocking { postPayment(card, items, redirectUrl, webhookUrl, data) }
    fun postPaymentBlocking(card: SpCard, paymentRequest: PaymentRequest) = runBlocking { postPayment(card, paymentRequest) }
}
