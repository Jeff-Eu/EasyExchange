package com.example.easyexchange

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.EOFException

/* Example of API response

{
    "success": true,
    "terms": "https://currencylayer.com/terms",
    "privacy": "https://currencylayer.com/privacy",
    "timestamp": 1634976603,
    "source": "USD",
    "quotes": {
        "USDAUD": 1.339585,
        "USDUSD": 1,
        "USDCAD": 1.236715,
        "USDPLN": 3.95365,
        "USDMXN": 20.175104
    }
}

 */

// Remember to initialize variables' values,
// otherwise Moshi would get crash if response json doesn't include the key-values.
data class LiveExchangeRateResponse(
    var success: Boolean = false,
    var timestamp: Long = 0,
    var source: String = "USD",
    var quotes: Map<String, Double> = mutableMapOf(),
    var error: Any? = null
) {
    companion object {

        fun convertToJson(liveExchangeRateResponse: LiveExchangeRateResponse): String {

            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()

            val jsonAdapter: JsonAdapter<LiveExchangeRateResponse> =
                moshi.adapter(LiveExchangeRateResponse::class.java)

            return jsonAdapter.toJson(liveExchangeRateResponse)
        }

        fun convertToObject(json: String): LiveExchangeRateResponse? {

            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            val jsonAdapter =
                moshi.adapter(LiveExchangeRateResponse::class.java)

            try {
                return jsonAdapter.fromJson(json)
            } catch (e: EOFException) {
                return null
            }
        }
    }

}