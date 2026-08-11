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
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json

object SpWorldsApi {
    private val client = ApiClient.httpClient
    private val json = Json { ignoreUnknownKeys = true }

    private suspend inline fun <reified T, R> safeParse(response: HttpResponse, crossinline transform: (T) -> R): Result<R> {
        if (response.status != HttpStatusCode.OK) {
            return Result.failure(Exception("HTTP error: ${response.status}"))
        }
        return try {
            val text = response.bodyAsText()
            val parsed = json.decodeFromString<T>(text)
            Result.success(transform(parsed))
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Перегрузка для случаев, когда трансформация не нужна (T == R)
    private suspend inline fun <reified T> safeParse(response: HttpResponse): Result<T> {
        return safeParse<T, T>(response) { it }
    }

    // Вспомогательный метод для безопасного получения текста из POST/PUT запросов
    private suspend fun safeText(response: HttpResponse): Result<String> {
        return if (response.status == HttpStatusCode.OK) {
            try {
                Result.success(response.bodyAsText())
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Result.failure(e)
            }
        } else {
            Result.failure(Exception("HTTP error: ${response.status}"))
        }
    }

    suspend fun getBalance(card: SpCard): Result<BalanceResponse> {
        val response = client.get("https://spworlds.ru/api/public/card") {
            header("Authorization", card.authHeader)
        }
        return safeParse(response)
    }

    suspend fun getProfile(card: SpCard): Result<ProfileResponse> {
        val response = client.get("https://spworlds.ru/api/public/accounts/me") {
            header("Authorization", card.authHeader)
        }
        return safeParse(response)
    }

    suspend fun getName(card: SpCard, discordID: String): Result<String> {
        val response = client.get("https://spworlds.ru/api/public/users/$discordID") {
            header("Authorization", card.authHeader)
        }
        // Парсим UserResponse, но возвращаем String (username)
        return safeParse<UserResponse, String>(response) { it.username }
    }

    suspend fun getCards(card: SpCard, username: String): Result<List<CardResponse>> {
        val response = client.get("https://spworlds.ru/api/public/accounts/$username/cards") {
            header("Authorization", card.authHeader)
        }
        return safeParse(response)
    }

    suspend fun postTransaction(card: SpCard, receiver: String, amount: Int, comment: String): Result<String> {
        val response = client.post("https://spworlds.ru/api/public/transactions") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(TransactionRequest(receiver, amount, comment))
        }
        return safeText(response)
    }

    suspend fun postTransaction(card: SpCard, transaction: TransactionRequest): Result<String> {
        val response = client.post("https://spworlds.ru/api/public/transactions") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(transaction)
        }
        return safeText(response)
    }

    suspend fun changeCardWebhook(card: SpCard, webhookUrl: String): Result<String> {
        val response = client.put("https://spworlds.ru/api/public/card/webhook") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(WebHook(url = webhookUrl))
        }
        return safeText(response)
    }

    suspend fun postPayment(
        card: SpCard,
        items: List<PaymentItem>,
        redirectUrl: String,
        webhookUrl: String,
        data: String
    ): Result<PaymentResponse> {
        val response = client.post("https://spworlds.ru/api/public/payments") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(PaymentRequest(items, redirectUrl, webhookUrl, data))
        }
        return safeParse(response)
    }

    suspend fun postPayment(card: SpCard, paymentRequest: PaymentRequest): Result<PaymentResponse> {
        val response = client.post("https://spworlds.ru/api/public/payments") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(paymentRequest)
        }
        return safeParse(response)
    }

    // Blocking methods с сохранением строгих типов данных
    fun getBalanceBlocking(card: SpCard): Result<BalanceResponse> = runBlocking { getBalance(card) }
    fun getProfileBlocking(card: SpCard): Result<ProfileResponse> = runBlocking { getProfile(card) }
    fun getNameBlocking(card: SpCard, discordID: String): Result<String> = runBlocking { getName(card, discordID) }
    fun getCardsBlocking(card: SpCard, username: String): Result<List<CardResponse>> = runBlocking { getCards(card, username) }
    fun postTransactionBlocking(card: SpCard, receiver: String, amount: Int, comment: String): Result<String> = runBlocking { postTransaction(card, receiver, amount, comment) }
    fun postTransactionBlocking(card: SpCard, transaction: TransactionRequest): Result<String> = runBlocking { postTransaction(card, transaction) }
    fun changeCardWebhookBlocking(card: SpCard, webhookUrl: String): Result<String> = runBlocking { changeCardWebhook(card, webhookUrl) }
    fun postPaymentBlocking(card: SpCard, items: List<PaymentItem>, redirectUrl: String, webhookUrl: String, data: String): Result<PaymentResponse> = runBlocking { postPayment(card, items, redirectUrl, webhookUrl, data) }
    fun postPaymentBlocking(card: SpCard, paymentRequest: PaymentRequest): Result<PaymentResponse> = runBlocking { postPayment(card, paymentRequest) }
}