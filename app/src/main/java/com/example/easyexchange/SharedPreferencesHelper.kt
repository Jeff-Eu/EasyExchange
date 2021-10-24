package com.example.easyexchange

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import kotlin.properties.ReadWriteProperty
import kotlin.reflect.KProperty

open class SharedPreferencesHelper {

    private val context = EasyExchangeApplication.instance

    private val sharedPreferences: SharedPreferences by lazy {
        context.getSharedPreferences(
            "EasyExchange",
            Context.MODE_PRIVATE
        )
    }

    // Last Json response from calling CurrencyLayer API
    var lastJsonFromCurrencyLayer by SharedPreferenceDelegates.stringSetProperty()

    // Source Currency
    var sourceCurrency by SharedPreferenceDelegates.stringProperty("USD")

    // Selected target  currencies by user's preference
    var selectedTargetCurrencies by SharedPreferenceDelegates.stringSetProperty(
        mutableSetOf(
            "AUD",
            "JPY",
            "EUR",
            "GBP",
            "TWD"
        )
    )

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

        fun stringSetProperty(defaultValue: MutableSet<String> = mutableSetOf()) =
            object : ReadWriteProperty<SharedPreferencesHelper, MutableSet<String>> {
                override fun getValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>
                ): MutableSet<String> {
                    return thisRef.sharedPreferences.getStringSet(property.name, defaultValue)
                        ?: mutableSetOf()
                }

                override fun setValue(
                    thisRef: SharedPreferencesHelper,
                    property: KProperty<*>,
                    value: MutableSet<String>
                ) {
                    thisRef.sharedPreferences.edit()
                        .putStringSet(property.name, value)
                        .apply()
                }
            }
    }
}