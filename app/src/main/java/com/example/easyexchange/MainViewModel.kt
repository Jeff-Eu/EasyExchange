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

    private val _exchangeRateDataList: MutableLiveData<List<ExchangeRateData>> = MutableLiveData()
    val exchangeRateDataList: LiveData<List<ExchangeRateData>>
        get() = _exchangeRateDataList

    private val _exchangeRateRetrieved = MutableLiveData(false)
    val exchangeRateRetrieved: LiveData<Boolean>
        get() = _exchangeRateRetrieved

    init {
        getExchangeRates()
    }

    fun getExchangeRates() {
        val selectedCurrencies = SharedPreferencesHelper().selectedTargetCurrencies

        val job = viewModelScope.launch {

            val body: LiveExchangeRateResponse
            withTimeout(5000) {
                body =
                    Api.currencyLayerRetrofitService.getLiveExchangeRate(selectedCurrencies.joinToString())
            }

            val exchangeMap = body.quotes
            for (pair in exchangeMap)
                Timber.i("${pair.key}, ${pair.value}")

            _exchangeRateDataList.value =
                body.quotes.map { entry -> ExchangeRateData(entry.value, entry.key.substring(3)) }

            val timestamp = body.timestamp
            Timber.i(timestamp.toString())

            _exchangeRateRetrieved.value = true
        }

        job.invokeOnCompletion {

            it?.message?.let { msg ->

                if (it is CancellationException)
                    Timber.d("$job was cancelled. Reason: $msg")
                else
                    Timber.e("$job throws an error. Reason: $msg")
            }
        }
    }

    fun onSwipeRefreshed() {
        getExchangeRates()
    }

    fun calculateExchangeRate() {
//        _exchangeRateDataList.value.forEach {  }
    }

}