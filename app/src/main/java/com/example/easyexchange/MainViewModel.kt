package com.example.easyexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeout
import timber.log.Timber
import com.squareup.moshi.JsonAdapter

import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.EOFException
import java.lang.Exception

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
        val liveExchangeRateResponse = convertToObject(json)
        _exchangeRateDataList.value = convertToExchangeRateDataList(liveExchangeRateResponse)
    }

    private fun convertToExchangeRateDataList(data: LiveExchangeRateResponse?): List<ExchangeRateData>? {

        data ?: return null

        val output = data.quotes.map { entry ->
            ExchangeRateData(
                entry.value,
                entry.key.substring(3)
            )
        }
        return output
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

                _exchangeRateDataList.value = convertToExchangeRateDataList(body)

                // Convert API response to Json string and save it to SharedPreference
                SharedPreferencesHelper().jsonOfCurrencyLayerResponse = convertToJson(body)

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

    fun onSwipeRefreshed() {
        updatePropertiesByCallCurrencyLayerApi()
    }


    fun findExchangeRateOfUSD(selectedSourceCurrency: String): Double? {
        return _exchangeRateDataList.value?.find { v ->
            v.targetCurrency == selectedSourceCurrency
        }?.exchangeRateOfUSD
    }

    private fun calculateViewOfExchangeRateForAllCurrencies(selectedSourceCurrency: String) {

        val selectedSourceCurrencyRatio =
            _exchangeRateDataList.value?.find { v -> v.targetCurrency == selectedSourceCurrency }?.exchangeRateOfUSD

        if (selectedSourceCurrencyRatio != null)
            _exchangeRateDataList.value?.forEach { v -> v.exchangeRateOfUSD /= selectedSourceCurrencyRatio }
    }

}