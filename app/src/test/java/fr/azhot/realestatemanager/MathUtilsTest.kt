package fr.azhot.realestatemanager


import fr.azhot.realestatemanager.utils.roundIntLower
import fr.azhot.realestatemanager.utils.roundIntUpper
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.properties.Delegates

class MathUtilsTest {

    private var valueToRound by Delegates.notNull<Int>()
    private var roundValue by Delegates.notNull<Float>()

    @Before
    fun init() {
        valueToRound = 123
        roundValue = 10f
    }

    @Test
    fun roundIntUpper_isCorrect() {
        assertEquals(130f, roundIntUpper(valueToRound, roundValue))
    }

    @Test
    fun roundIntLower_isCorrect() {
        assertEquals(120f, roundIntLower(valueToRound, roundValue))
    }
}