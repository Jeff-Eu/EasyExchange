package com.example.easyexchange


data class ExchangeRateData(
    // exchangeRateOfUSD is equal to USD/targetCurrency. E.g. USD/TWD ~= 28
    var exchangeRateOfUSD: Double,
    var targetCurrency: String
) {
    private fun getExchangeRateOfTargetCurrency(sourceRateOfUSD: Double): Double {
        return exchangeRateOfUSD / sourceRateOfUSD
    }

    private fun getTargetCurrencyValue(sourceValue: Double, sourceRateOfUSD: Double): Double {
        return sourceValue * getExchangeRateOfTargetCurrency(sourceRateOfUSD)
    }

    fun getTargetCurrencyValueText(sourceValue: Double?, sourceRateOfUSD: Double): String {
        if (sourceValue == null)
            return "-"
        else
            return getTargetCurrencyValue(sourceValue, sourceRateOfUSD).toString()
    }
}
