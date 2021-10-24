package com.example.easyexchange

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
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

    private object SharedPreferenceDelegates {

        fun intProperty(defaultValue: Int = 1) =
            object : ReadWriteProperty<SharedPreferencesHelper, Int> {
                override fun getValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>
                ): Int {
                    return thisRef.sharedPreferences.getInt(property.name, defaultValue)
                }

                override fun setValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>,
                    value: Int
                ) {
                    thisRef.sharedPreferences.edit()
                        .putInt(property.name, value)
                        .apply()
                }
            }

        fun booleanProperty(defaultValue: Boolean = true) =
            object : ReadWriteProperty<SharedPreferencesHelper, Boolean> {
                override fun getValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>
                ): Boolean {
                    return thisRef.sharedPreferences.getBoolean(property.name, defaultValue)
                }

                override fun setValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>,
                    value: Boolean
                ) {
                    thisRef.sharedPreferences.edit()
                        .putBoolean(property.name, value)
                        .apply()
                }
            }

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
    }
}