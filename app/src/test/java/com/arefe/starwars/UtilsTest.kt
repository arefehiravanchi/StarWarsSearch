package com.arefe.starwars

import com.arefe.starwars.utilities.Utils
import org.junit.Assert
import org.junit.Test

class UtilsTest {

    @Test
    fun test_getValidFormattedHeight(){
        val formattedHeight = Utils.getFormattedHeight("164")
        val expectedValue = "164 Cm (5 Feet 4.6 Inch)"

        Assert.assertNotNull(formattedHeight)
        Assert.assertEquals(expectedValue,formattedHeight)
    }

    @Test
    fun test_getAnotherValidFormattedHeight(){
        val formattedHeight = Utils.getFormattedHeight("66")
        val expectedValue = "66 Cm (2 Feet 2 Inch)"

        Assert.assertNotNull(formattedHeight)
        Assert.assertEquals(expectedValue,formattedHeight)
    }

}