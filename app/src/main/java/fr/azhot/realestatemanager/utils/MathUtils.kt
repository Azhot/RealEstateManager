package fr.azhot.realestatemanager.utils

import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor

// todo : write javadoc

fun roundIntUpper(valueToRound: Int, roundValue: Float): Float {
    return (roundValue * (ceil(abs(valueToRound.toDouble() / roundValue)))).toFloat()
}

fun roundIntLower(valueToRound: Int, roundValue: Float): Float {
    return (roundValue * (floor(abs(valueToRound.toDouble() / roundValue)))).toFloat()
}