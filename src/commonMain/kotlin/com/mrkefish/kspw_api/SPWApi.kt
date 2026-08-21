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
import io.ktor.http.isSuccess
import io.ktor.http.contentType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.jvm.JvmStatic

object SpWorldsApi {
    private val client = ApiClient.httpClient
    private val json = Json { ignoreUnknownKeys = true }

    private suspend inline fun <reified T, R> safeParse(response: HttpResponse, crossinline transform: (T) -> R): Result<R> {
        if (!response.status.isSuccess()) {
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
        return if (response.status.isSuccess()) {
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

    suspend fun getCardInfo(card: SpCard): Result<BalanceResponse> {
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

    suspend fun getName(card: SpCard, discordID: String): Result<UserResponse> {
        val response = client.get("https://spworlds.ru/api/public/users/$discordID") {
            header("Authorization", card.authHeader)
        }
        return safeParse(response)
    }

    suspend fun getCards(card: SpCard, username: String): Result<List<CardResponse>> {
        val response = client.get("https://spworlds.ru/api/public/accounts/$username/cards") {
            header("Authorization", card.authHeader)
        }
        return safeParse(response)
    }

    suspend fun postTransaction(card: SpCard, receiver: String, amount: Int, comment: String): Result<TransactionResponse> {
        val response = client.post("https://spworlds.ru/api/public/transactions") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(TransactionRequest(receiver, amount, comment))
        }
        return safeParse(response)
    }

    suspend fun postTransaction(card: SpCard, transaction: TransactionRequest): Result<TransactionResponse> {
        val response = client.post("https://spworlds.ru/api/public/transactions") {
            header("Authorization", card.authHeader)
            contentType(ContentType.Application.Json)
            setBody(transaction)
        }
        return safeParse(response)
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

    // Sync методы для java через Blocking
    fun getCardInfoSync(card: SpCard): Result<BalanceResponse> = runBlocking { getCardInfo(card) }
    fun getProfileSync(card: SpCard): Result<ProfileResponse> = runBlocking { getProfile(card) }
    fun getNameSync(card: SpCard, discordID: String): Result<UserResponse> = runBlocking { getName(card, discordID) }
    fun getCardsSync(card: SpCard, username: String): Result<List<CardResponse>> = runBlocking { getCards(card, username) }
    fun postTransactionSync(card: SpCard, receiver: String, amount: Int, comment: String): Result<TransactionResponse> = runBlocking { postTransaction(card, receiver, amount, comment) }
    fun postTransactionSync(card: SpCard, transaction: TransactionRequest): Result<TransactionResponse> = runBlocking { postTransaction(card, transaction) }
    fun changeCardWebhookSync(card: SpCard, webhookUrl: String): Result<String> = runBlocking { changeCardWebhook(card, webhookUrl) }
    fun postPaymentSync(card: SpCard, items: List<PaymentItem>, redirectUrl: String, webhookUrl: String, data: String): Result<PaymentResponse> = runBlocking { postPayment(card, items, redirectUrl, webhookUrl, data) }
    fun postPaymentSync(card: SpCard, paymentRequest: PaymentRequest): Result<PaymentResponse> = runBlocking { postPayment(card, paymentRequest) }


    private val apiScope = CoroutineScope(Dispatchers.Default)

    private fun <T> Result<T>.toCallback(callback: SpCallback<T>) {
        this.onSuccess { callback.onSuccess(it) }
            .onFailure { callback.onError(it) }
    }

    // Async методы для Java через Callback
    @JvmStatic fun getCardInfoAsync(card: SpCard, callback: SpCallback<BalanceResponse>) = apiScope.launch { getCardInfo(card).toCallback(callback) }
    @JvmStatic fun getProfileAsync(card: SpCard, callback: SpCallback<ProfileResponse>) = apiScope.launch { getProfile(card).toCallback(callback) }
    @JvmStatic fun getNameAsync(card: SpCard, discordID: String, callback: SpCallback<UserResponse>) = apiScope.launch { getName(card, discordID).toCallback(callback) }
    @JvmStatic fun getCardsAsync(card: SpCard, username: String, callback: SpCallback<List<CardResponse>>) = apiScope.launch { getCards(card, username).toCallback(callback) }
    @JvmStatic fun postTransactionAsync(card: SpCard, receiver: String, amount: Int, comment: String, callback: SpCallback<TransactionResponse>) = apiScope.launch { postTransaction(card, receiver, amount, comment).toCallback(callback) }
    @JvmStatic fun postTransactionAsync(card: SpCard, transaction: TransactionRequest, callback: SpCallback<TransactionResponse>) = apiScope.launch { postTransaction(card, transaction).toCallback(callback) }
    @JvmStatic fun changeCardWebhookAsync(card: SpCard, webhookUrl: String, callback: SpCallback<String>) = apiScope.launch { changeCardWebhook(card, webhookUrl).toCallback(callback) }
    @JvmStatic fun postPaymentAsync(card: SpCard, items: List<PaymentItem>, redirectUrl: String, webhookUrl: String, data: String, callback: SpCallback<PaymentResponse>) = apiScope.launch { postPayment(card, items, redirectUrl, webhookUrl, data).toCallback(callback) }
    @JvmStatic fun postPaymentAsync(card: SpCard, paymentRequest: PaymentRequest, callback: SpCallback<PaymentResponse>) = apiScope.launch { postPayment(card, paymentRequest).toCallback(callback) }
}

interface SpCallback<T> {
    fun onSuccess(result: T)
    fun onError(error: Throwable)
}
