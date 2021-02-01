package fr.azhot.realestatemanager.utils

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

/**
 * Rounds a Integer to the next upper [roundValue].
 *
 * @param valueToRound the Integer to be rounded
 * @param roundValue the rounding scale
 */

fun roundIntUpper(valueToRound: Int, roundValue: Float): Float {
    return (roundValue * (ceil(abs(valueToRound.toDouble() / roundValue)))).toFloat()
}

/**
 * Rounds a Integer to the next lower [roundValue].
 *
 * @param valueToRound the Integer to be rounded
 * @param roundValue the rounding scale
 */

fun roundIntLower(valueToRound: Int, roundValue: Float): Float {
    return (roundValue * (floor(abs(valueToRound.toDouble() / roundValue)))).toFloat()
}