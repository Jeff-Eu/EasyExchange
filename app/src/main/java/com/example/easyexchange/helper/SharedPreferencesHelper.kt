package com.example.easyexchange.helper

import android.content.Context
import android.content.SharedPreferences
import com.example.easyexchange.EasyExchangeApplication
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class SharedPreferencesHelper {

    companion object {

        const val defaultJsonOfCurrencyLayerResponse =
            "{\"success\":true,\"timestamp\":1635235804,\"source\":\"USD\",\"quotes\":{\"USDAUD\":1.333801,\"USDUSD\":1.0,\"USDJPY\":113.997502,\"USDEUR\":0.861809,\"USDGBP\":0.726185,\"USDTWD\":27.824498}}"
    }

    private val context = EasyExchangeApplication.instance

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            "EasyExchange",
            Context.MODE_PRIVATE
        )
    }

    // jsonOfCurrencyLayerResponse saves the the most recent Json response from CurrencyLayer API
    var jsonOfCurrencyLayerResponse by SharedPreferenceDelegates.stringProperty(
        defaultJsonOfCurrencyLayerResponse
    )

    // Selected source Currency
    var sourceCurrency by SharedPreferenceDelegates.stringProperty("USD")

    // Stores the system time of previous call on CurrencyLayer API
    var systemTimeOfPrevCallOnCurrencyLayerAPI by SharedPreferenceDelegates.longProperty()

    private object SharedPreferenceDelegates {

        fun stringProperty(defaultValue: String = "") =
            object : ReadWriteProperty<SharedPreferencesHelper, String> {
                override fun getValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>
                ): String {
                    return thisRef.sharedPreferences.getString(property.name, defaultValue) ?: ""
                }

                override fun setValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>,
                    value: String
                ) {
                    thisRef.sharedPreferences.edit()
                        .putString(property.name, value)
                        .apply()
                }
            }

        fun longProperty(defaultValue: Long = 1) =
            object : ReadWriteProperty<SharedPreferencesHelper, Long> {
                override fun getValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>
                ): Long {
                    return thisRef.sharedPreferences.getLong(property.name, defaultValue)
                }

                override fun setValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>,
                    value: Long
                ) {
                    thisRef.sharedPreferences.edit()
                        .putLong(property.name, value)
                        .apply()
                }
            }
    }
}