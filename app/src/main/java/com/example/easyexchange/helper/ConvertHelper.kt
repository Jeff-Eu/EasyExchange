package com.example.easyexchange.helper

import com.example.easyexchange.model.ExchangeRateData
import com.example.easyexchange.model.LiveExchangeRateResponse

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