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
data class LiveExchangeRateResponse(
    var success: Boolean,
    val timestamp: Long,
    val source: String,
    val quotes: Map<String, Double>
)

data class ExchangeRateData(
    var exchangeRate: Double,
    var targetCurrency: String,
    var sourceCurrency: String = "USD"
)
