package com.example.easyexchange

import com.jakewharton.retrofit2.adapter.kotlin.coroutines.CoroutineCallAdapterFactory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.*

private const val CURRENCY_LAYER_BASE_URL = "http://api.currencylayer.com/"

// Moshi: converting JSON to Kotlin data classes and back
private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val CurrencyLayerRetrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .addCallAdapterFactory(CoroutineCallAdapterFactory())
    .baseUrl(CURRENCY_LAYER_BASE_URL)
    .build()

interface ApiService {

    // Request the most recent exchange rate data
    @GET("live")
    suspend fun getLiveExchangeRate(
        @Query("currencies") currencies: String,
        @Query("access_key") access_key: String = "7254d2810ad44d983b2ba441ffcb5147",
        @Query("format") format: Int = 1
    ): LiveExchangeRateResponse

}

/**
 * A public Api object that exposes the lazy-initialized Retrofit service
 */
object Api {
    val currencyLayerRetrofitService: ApiService by lazy { CurrencyLayerRetrofit.create(ApiService::class.java) }
}
