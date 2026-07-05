package com.example.data

import android.util.Log
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.POST
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory

// --- API Models for Flutterwave ---

data class FlutterwavePaymentRequest(
    val tx_ref: String,
    val amount: String,
    val currency: String = "NGN",
    val redirect_url: String = "https://example.com/callback",
    val customer: FlutterwaveCustomer,
    val customizations: FlutterwaveCustomizations
)

data class FlutterwaveCustomer(
    val email: String,
    val phone_number: String,
    val name: String
)

data class FlutterwaveCustomizations(
    val title: String,
    val description: String,
    val logo: String = "https://aurea-fintech.com/logo.png"
)

data class FlutterwavePaymentResponse(
    val status: String,
    val message: String,
    val data: FlutterwavePaymentData?
)

data class FlutterwavePaymentData(
    val link: String
)

data class FlutterwaveTransferRequest(
    val account_bank: String,
    val account_number: String,
    val amount: Double,
    val narration: String,
    val currency: String = "NGN",
    val reference: String
)

data class FlutterwaveTransferResponse(
    val status: String,
    val message: String,
    val data: FlutterwaveTransferData?
)

data class FlutterwaveTransferData(
    val id: Long,
    val account_number: String,
    val bank_code: String,
    val full_name: String,
    val created_at: String,
    val amount: Double,
    val fee: Double,
    val status: String
)

// --- Retrofit API Interface ---

interface FlutterwaveApi {
    @POST("v3/payments")
    suspend fun createPaymentLink(
        @Header("Authorization") token: String,
        @Body request: FlutterwavePaymentRequest
    ): FlutterwavePaymentResponse

    @POST("v3/transfers")
    suspend fun initiateTransfer(
        @Header("Authorization") token: String,
        @Body request: FlutterwaveTransferRequest
    ): FlutterwaveTransferResponse
}

// --- Flutterwave Integration Client ---

object FlutterwaveClient {
    private const val TAG = "FlutterwaveClient"
    private const val BASE_URL = "https://api.flutterwave.com/"

    private val moshi = Moshi.Builder()
        .addLast(KotlinJsonAdapterFactory())
        .build()

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val api: FlutterwaveApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .build()
            .create(FlutterwaveApi::class.java)
    }

    /**
     * Attempts to call Flutterwave API to create a Deposit Payment Link.
     * If the API Key is invalid or empty, it returns null.
     */
    suspend fun createDepositLink(
        secretKey: String,
        txRef: String,
        amount: Double,
        email: String,
        phone: String,
        name: String
    ): String? {
        if (secretKey.isBlank() || secretKey.contains("YOUR_") || secretKey.contains("MY_") || secretKey.contains("placeholder")) {
            Log.w(TAG, "Flutterwave API Secret Key not configured or placeholder. Falling back to secure simulated sandbox flow.")
            return null
        }

        return try {
            val response = api.createPaymentLink(
                token = "Bearer $secretKey",
                request = FlutterwavePaymentRequest(
                    tx_ref = txRef,
                    amount = amount.toString(),
                    customer = FlutterwaveCustomer(email = email, phone_number = phone, name = name),
                    customizations = FlutterwaveCustomizations(
                        title = "Aurea Wallet Deposit",
                        description = "Fund your Aurea organic wealth wallet"
                    )
                )
            )
            if (response.status == "success") {
                response.data?.link
            } else {
                Log.e(TAG, "Flutterwave returned error status: ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Flutterwave createDepositLink: ${e.message}", e)
            null
        }
    }

    /**
     * Attempts to call Flutterwave API to process a withdrawal (bank transfer).
     * Returns true if successful, or false if API call fails.
     */
    suspend fun processWithdrawal(
        secretKey: String,
        bankCode: String,
        accountNumber: String,
        amount: Double,
        reference: String,
        narration: String
    ): FlutterwaveTransferData? {
        if (secretKey.isBlank() || secretKey.contains("YOUR_") || secretKey.contains("MY_") || secretKey.contains("placeholder")) {
            Log.w(TAG, "Flutterwave API Secret Key not configured. Falling back to secure simulated sandbox transfer.")
            return null
        }

        return try {
            val response = api.initiateTransfer(
                token = "Bearer $secretKey",
                request = FlutterwaveTransferRequest(
                    account_bank = bankCode,
                    account_number = accountNumber,
                    amount = amount,
                    narration = narration,
                    reference = reference
                )
            )
            if (response.status == "success") {
                response.data
            } else {
                Log.e(TAG, "Flutterwave Transfer returned error status: ${response.message}")
                null
            }
        } catch (e: Exception) {
            Log.e(TAG, "Exception during Flutterwave processWithdrawal: ${e.message}", e)
            null
        }
    }
}
