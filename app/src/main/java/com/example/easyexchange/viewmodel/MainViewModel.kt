package com.example.easyexchange.viewmodel

import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.easyexchange.Api
import com.example.easyexchange.EasyExchangeApplication
import com.example.easyexchange.helper.ConvertHelper
import com.example.easyexchange.helper.SharedPreferencesHelper
import com.example.easyexchange.model.ExchangeRateData
import com.example.easyexchange.model.LiveExchangeRateResponse
import kotlinx.coroutines.*
import timber.log.Timber

class MainViewModel : ViewModel() {

    companion object {

        const val MILLISECONDS_OF_HALF_HOUR = 30 * 60 * 1000L
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
        _timestamp.postValue(liveExchangeRateResponse?.timestamp ?: 0)
        _exchangeRateDataList.postValue(
            ConvertHelper.convertToExchangeRateDataList(
                liveExchangeRateResponse
            )
        )
    }

    /**
     * Call the CurrencyLayer API at most once per time interval, [limitedTimeInterval], which is measured
     * in milliseconds. And update the saved timestamp info.
     */
    suspend fun callCurrencyLayerApiAndUpdateTimestamp(limitedTimeInterval: Long): LiveExchangeRateResponse? {
        val presentTime = System.currentTimeMillis()
        if (presentTime - systemTimeOfPrevCallOnCurrencyLayerAPI < limitedTimeInterval) {
            withContext(Dispatchers.Main) {
                Toast.makeText(
                    EasyExchangeApplication.instance,
                    "API wasn't called. We restrict the CurrencyLayer API can only be called once per ${limitedTimeInterval / 1000 / 60} minutes.",
                    Toast.LENGTH_LONG
                ).show()
            }
            return null
        }

        /// update timestamp info
        SharedPreferencesHelper().systemTimeOfPrevCallOnCurrencyLayerAPI = presentTime
        systemTimeOfPrevCallOnCurrencyLayerAPI = presentTime

        return Api.currencyLayerRetrofitService.getLiveExchangeRates(targetCurrencyList.joinToString())
    }

    private fun updatePropertiesByCallCurrencyLayerApi() {
        val job = viewModelScope.launch {

            val body: LiveExchangeRateResponse?
            withTimeout(3000) {
                body = callCurrencyLayerApiAndUpdateTimestamp(MILLISECONDS_OF_HALF_HOUR)
            }
            if (body == null) {
                _exchangeRateApiCalled.postValue(true)
                return@launch
            }

            if (body.success) {

                val exchangeMap = body.quotes
                for (pair in exchangeMap)
                    Timber.i("${pair.key}, ${pair.value}")

                _exchangeRateDataList.postValue(ConvertHelper.convertToExchangeRateDataList(body))

                // Convert API response to Json string and save it to SharedPreference
                SharedPreferencesHelper().jsonOfCurrencyLayerResponse =
                    LiveExchangeRateResponse.convertToJson(body)

                _timestamp.postValue(body.timestamp)
            } else
                Timber.e(body.error?.toString())

            _exchangeRateApiCalled.postValue(true)
        }

        job.invokeOnCompletion {

            it?.message?.let { msg ->

                if (it is CancellationException)
                    Timber.d("$job was cancelled. Cause: ${it.cause}, Message: $msg")
                else
                    Timber.e("$job throws an error. Cause: ${it.cause}, Message: $msg")

                _exchangeRateApiCalled.postValue(true)
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