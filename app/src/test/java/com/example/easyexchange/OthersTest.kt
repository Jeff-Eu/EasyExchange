package com.example.easyexchange

import com.example.easyexchange.model.LiveExchangeRateResponse
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class OthersTest {
    @Test
    fun `mutableSet to String`() {
        val set = mutableSetOf("12", "34")
        assertEquals("12, 34", set.joinToString())
    }

    @Test
    fun `convertToObject() on LiveExchangeRateResponse`() {
        var r = LiveExchangeRateResponse.convertToObject("")
        assertEquals(r, null)
        r = LiveExchangeRateResponse.convertToObject("{}")
        assertNotNull(r)
    }
}