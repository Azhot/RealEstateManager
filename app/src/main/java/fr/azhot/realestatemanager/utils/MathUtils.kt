package fr.azhot.realestatemanager.utils

// todo : write javadoc

fun roundInt(valueToRound: Int, roundValue: Int): Int {
    return (((valueToRound + roundValue - 1) / roundValue) * roundValue)
}