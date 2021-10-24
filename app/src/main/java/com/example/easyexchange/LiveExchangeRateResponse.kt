package com.example.easyexchange

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


    }

}