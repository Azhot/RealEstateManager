package fr.azhot.realestatemanager

import fr.azhot.realestatemanager.viewmodel.LoanCalculatorFragmentViewModel
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import kotlin.math.pow

class LoanCalculatorFragmentViewModelTest {

    private lateinit var viewModel: LoanCalculatorFragmentViewModel

    @Before
    fun init() {
        viewModel = LoanCalculatorFragmentViewModel()
    }

    @Test
    fun calculateMonthlyPayment_isCorrect() {

        val amountToFind = (100f * ((1f + 0.1f) / 100 / 12)) /
                (1 - (1 + ((1f + 0.1f) / 100 / 12)).pow(-120f))

        assertEquals(amountToFind, viewModel.calculateMonthlyPayment(100f, 1f, 0.1f, 120f))
    }
}