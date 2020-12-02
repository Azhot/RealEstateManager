package fr.azhot.realestatemanager

import fr.azhot.realestatemanager.utils.Utils
import fr.azhot.realestatemanager.utils.Utils.EUR_DOLLAR_EXCHANGE_RATE
import org.junit.Assert.*
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class UtilsUnitTest {
    @Test
    fun convertDollarToEuro_isCorrect() {
        val euros = 123
        val dollars = (euros / EUR_DOLLAR_EXCHANGE_RATE).roundToInt()

        assertEquals(dollars, Utils.convertEuroToDollar(euros))
    }

    @Test
    fun getDateFormatted_isWorking() {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val date = format.format(Date())

        assertEquals(date, Utils.getTodayDate())
    }
}