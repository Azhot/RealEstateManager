package fr.azhot.realestatemanager

import fr.azhot.realestatemanager.Utils.EUR_DOLLAR_EXCHANGE_RATE
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.roundToInt

class UtilsUnitTest {
    @Test
    fun convertDollarToEuro_isCorrect() {
        val euros = 123
        val dollars = (euros / EUR_DOLLAR_EXCHANGE_RATE).roundToInt()

        assertEquals(dollars, Utils.convertEuroToDollar(euros))
    }
}