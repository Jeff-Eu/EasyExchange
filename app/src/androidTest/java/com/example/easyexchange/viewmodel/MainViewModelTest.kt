package com.example.easyexchange.viewmodel

import android.content.Context
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry.getInstrumentation
import junit.framework.TestCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith


@RunWith(AndroidJUnit4::class)
class MainViewModelTest : TestCase() {

    private lateinit var viewModel: MainViewModel

    @Before
    public override fun setUp() {
        super.setUp()

        viewModel = MainViewModel()
    }

    @After
    public override fun tearDown() {
        super.tearDown()

        val sharedPreferences = getInstrumentation().targetContext
            .getSharedPreferences("EasyExchange", Context.MODE_PRIVATE)
        sharedPreferences.edit().clear().commit()
    }

    //region ViewModel test

    @Test
    fun callCurrencyLayerApiAndUpdateTimestamp_delay_enough_then_call_again() {

        runBlocking {

            val seconds = 3000L
            val oneSecond = 1000L
            viewModel.callCurrencyLayerApiAndUpdateTimestamp(seconds)
            delay(seconds + oneSecond)
            val response = viewModel.callCurrencyLayerApiAndUpdateTimestamp(seconds)
            assertNotNull(response)
        }
    }

    @Test
    fun callCurrencyLayerApiAndUpdateTimestamp_delay_not_enough_then_call_again() {

        runBlocking {

            val seconds = 3000L
            val oneSecond = 1000L
            viewModel.callCurrencyLayerApiAndUpdateTimestamp(seconds)
            delay(oneSecond)
            val response = viewModel.callCurrencyLayerApiAndUpdateTimestamp(seconds)
            assertNull(response)
        }
    }

    //endregion
}