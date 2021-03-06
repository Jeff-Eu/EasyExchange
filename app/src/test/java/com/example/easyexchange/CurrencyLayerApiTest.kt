package com.example.easyexchange

import junit.framework.TestCase
import kotlinx.coroutines.runBlocking
import org.junit.Test
import org.junit.Assert.*

class CurrencyLayerApiTest {

    @Test
    fun `call API and verify the response Object`() {

        runBlocking {

            val targetCurrencyList = listOf("AUD", "USD", "JPY", "EUR", "GBP", "TWD")
            var body =
                Api.currencyLayerRetrofitService.getLiveExchangeRates(targetCurrencyList.joinToString())

            // Test: for successful case
            if (body.success) {
                assertEquals("USD", body.source)
                assertEquals(6, body.quotes.size)
            }

            // Test: Check the error return in case of continuous calls in a short time
            for (i in 1..3) {
                body =
                    Api.currencyLayerRetrofitService.getLiveExchangeRates(targetCurrencyList.joinToString())
                if (!body.success)
                    assertNotNull(body.error)
            }
        }
    }
}