package com.example.easyexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber

class MainViewModel : ViewModel() {

    var sourceCurrency: String = SharedPreferencesHelper().sourceCurrency
        set(value) {
            field = value
            SharedPreferencesHelper().sourceCurrency = value
            updatePropertiesFromMemory()
        }

    val targetCurrencyList = listOf("AUD", "USD", "JPY", "EUR", "GBP", "TWD")

    private val _exchangeRateDataList: MutableLiveData<List<ExchangeRateData>> = MutableLiveData()
    val exchangeRateDataList: LiveData<List<ExchangeRateData>>
        get() = _exchangeRateDataList

    private val _exchangeRateApiRetrieved = MutableLiveData(false)
    val exchangeRateRetrieved: LiveData<Boolean>
        get() = _exchangeRateApiRetrieved

    init {

        updateProperties()
    }

    private fun updateProperties() {

        updatePropertiesFromPersistentData()
        updatePropertiesByCallCurrencyLayerApi()
    }

    fun updatePropertiesFromMemory() {

        _exchangeRateDataList.value = _exchangeRateDataList.value
    }

    private fun updatePropertiesFromPersistentData() {

        val json = SharedPreferencesHelper().jsonOfCurrencyLayerResponse
        val liveExchangeRateResponse = LiveExchangeRateResponse.convertToObject(json)
        _exchangeRateDataList.value = ConvertHelper.convertToExchangeRateDataList(liveExchangeRateResponse)
    }

    private fun updatePropertiesByCallCurrencyLayerApi() {
        val job = viewModelScope.launch {

            val body: LiveExchangeRateResponse
            withTimeout(3000) {
                body =
                    Api.currencyLayerRetrofitService.getLiveExchangeRates(targetCurrencyList.joinToString())
            }

            if (body.success) {

                val exchangeMap = body.quotes
                for (pair in exchangeMap)
                    Timber.i("${pair.key}, ${pair.value}")

                _exchangeRateDataList.value = ConvertHelper.convertToExchangeRateDataList(body)

                // Convert API response to Json string and save it to SharedPreference
                SharedPreferencesHelper().jsonOfCurrencyLayerResponse =
                    LiveExchangeRateResponse.convertToJson(body)

            } else
                Timber.e(body.error?.toString())

            _exchangeRateApiRetrieved.value = true
        }

        job.invokeOnCompletion {

            it?.message?.let { msg ->

                if (it is CancellationException)
                    Timber.d("$job was cancelled. Reason: $msg")
                else
                    Timber.e("$job throws an error. Reason: $msg")

                _exchangeRateApiRetrieved.value = true
            }
        }
    }

    fun onSwipeRefreshed() {
        updatePropertiesByCallCurrencyLayerApi()
    }

    fun findExchangeRateOfUSD(selectedSourceCurrency: String): Double? {
        return _exchangeRateDataList.value?.find { v ->
            v.targetCurrency == selectedSourceCurrency
        }?.exchangeRateOfUSD
    }
}