package fr.azhot.realestatemanager.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import kotlin.math.pow

class LoanCalculatorFragmentViewModel : ViewModel() {

    fun calculateMonthlyPayment(
        amount: Float,
        interestRate: Float,
        insuranceRate: Float,
        durationInMonth: Float
    ): Float {
        return (amount * ((interestRate + insuranceRate) / 100 / 12)) /
                (1 - (1 + ((interestRate + insuranceRate) / 100 / 12)).pow(-durationInMonth))
    }
}

class LoanCalculatorFragmentViewModelFactory : ViewModelProvider.Factory {

    // overridden functions
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LoanCalculatorFragmentViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LoanCalculatorFragmentViewModel() as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}