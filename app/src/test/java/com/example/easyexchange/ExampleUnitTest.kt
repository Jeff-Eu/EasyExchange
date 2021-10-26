package com.example.easyexchange

import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
    @Test
    fun test_MutableSetToString() {
        val set = mutableSetOf("12", "34")
        assertEquals("12, 34", set.joinToString())
    }

    @Test
    fun test_convertToObjectOnLiveExchangeRateResponse() {
        var r = LiveExchangeRateResponse.convertToObject("")
        assertEquals(r, null)
        r = LiveExchangeRateResponse.convertToObject("{}")
        assertNotNull(r)
    }
}