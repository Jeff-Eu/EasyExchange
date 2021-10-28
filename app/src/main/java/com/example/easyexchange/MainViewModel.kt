package com.example.easyexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*
import timber.log.Timber
import java.lang.Exception

class MainViewModel : ViewModel() {

    companion object {

        const val millisecondsOf30minutes = 30 * 60 * 1000
    }

    var sourceCurrency: String = SharedPreferencesHelper().sourceCurrency
        set(value) {
            field = value
            SharedPreferencesHelper().sourceCurrency = value
            updatePropertiesFromMemory()
        }

    val targetCurrencyList = listOf("AUD", "USD", "JPY", "EUR", "GBP", "TWD")

    private var systemTimeOfPrevCallOnCurrencyLayerAPI =
        SharedPreferencesHelper().systemTimeOfPrevCallOnCurrencyLayerAPI

    // Bonus: timestamp on the response of CurrencyLayer API
    private val _timestamp: MutableLiveData<Long> = MutableLiveData(0)
    val timestamp: LiveData<Long>
        get() = _timestamp

    private val _exchangeRateDataList: MutableLiveData<List<ExchangeRateData>> = MutableLiveData()
    val exchangeRateDataList: LiveData<List<ExchangeRateData>>
        get() = _exchangeRateDataList

    private val _exchangeRateApiCalled = MutableLiveData(false)
    val exchangeRateRetrieved: LiveData<Boolean>
        get() = _exchangeRateApiCalled

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
        _timestamp.value = liveExchangeRateResponse?.timestamp ?: 0
        _exchangeRateDataList.value =
            ConvertHelper.convertToExchangeRateDataList(liveExchangeRateResponse)
    }

    private fun updatePropertiesByCallCurrencyLayerApi() {
        val job = viewModelScope.launch {

            val body: LiveExchangeRateResponse
            try {
                withTimeout(3000) {
                    val presentTime = System.currentTimeMillis()
                    if (presentTime - systemTimeOfPrevCallOnCurrencyLayerAPI < millisecondsOf30minutes)
                        throw CancellationException("We restrict the CurrencyLayer API can only be called once per 30 minutes.")

                    SharedPreferencesHelper().systemTimeOfPrevCallOnCurrencyLayerAPI = presentTime
                    systemTimeOfPrevCallOnCurrencyLayerAPI = presentTime

                    body =
                        Api.currencyLayerRetrofitService.getLiveExchangeRates(targetCurrencyList.joinToString())
                }
            } catch (e: Exception) {
                Timber.d("Cause: ${e.cause}, Message: ${e.message}")
                _exchangeRateApiCalled.value = true
                return@launch
            }

            if (body.success) {

                val exchangeMap = body.quotes
                for (pair in exchangeMap)
                    Timber.i("${pair.key}, ${pair.value}")

                _exchangeRateDataList.value = ConvertHelper.convertToExchangeRateDataList(body)

                // Convert API response to Json string and save it to SharedPreference
                SharedPreferencesHelper().jsonOfCurrencyLayerResponse =
                    LiveExchangeRateResponse.convertToJson(body)

                _timestamp.value = body.timestamp
            } else
                Timber.e(body.error?.toString())

            _exchangeRateApiCalled.value = true
        }

        job.invokeOnCompletion {

            it?.message?.let { msg ->

                if (it is CancellationException)
                    Timber.d("$job was cancelled. Cause: ${it.cause}, Message: $msg")
                else
                    Timber.e("$job throws an error. Cause: ${it.cause}, Message: $msg")

                _exchangeRateApiCalled.value = true
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