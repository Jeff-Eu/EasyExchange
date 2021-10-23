package com.example.easyexchange

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class MainViewModel : ViewModel() {

    private val _convertedCurrencyList: MutableLiveData<List<ConvertedCurrency>> = MutableLiveData()
    val convertedCurrencyList: LiveData<List<ConvertedCurrency>>
        get() = _convertedCurrencyList

    fun onSwipeRefreshed() {

    }

}