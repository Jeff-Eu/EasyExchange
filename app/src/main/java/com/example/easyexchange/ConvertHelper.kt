package com.example.easyexchange

class ConvertHelper {

    companion object {
        fun convertToExchangeRateDataList(data: LiveExchangeRateResponse?): List<ExchangeRateData>? {

            data ?: return null

            val output = data.quotes.map { entry ->
                ExchangeRateData(
                    entry.value,
                    entry.key.substring(3)
                )
            }
            return output
        }
    }
}