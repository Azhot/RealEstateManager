package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.google.android.material.slider.Slider
import fr.azhot.realestatemanager.databinding.FragmentLoanCalculatorBinding
import fr.azhot.realestatemanager.viewmodel.LoanCalculatorFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.LoanCalculatorFragmentViewModelFactory
import java.text.NumberFormat
import java.util.*


class LoanCalculatorFragment : Fragment(), Slider.OnChangeListener {


    // variables
    private lateinit var binding: FragmentLoanCalculatorBinding
    private val viewModel: LoanCalculatorFragmentViewModel by viewModels {
        LoanCalculatorFragmentViewModelFactory()
    }


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoanCalculatorBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpLoanAmountSlider()
        setUpInterestRateSlider()
        setUpInsuranceRateSlider()
        setUpLoanDurationSlider()
    }

    override fun onValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        updateUI()
    }


    // functions
    private fun formatDollars(float: Float): String {
        return NumberFormat.getCurrencyInstance(Locale.US).run {
            maximumFractionDigits = 0
            format(float)
        }
    }

    private fun formatPercent(float: Float): String {
        return NumberFormat.getPercentInstance().run {
            maximumFractionDigits = 2
            format(float / 100)
        }
    }

    private fun formatNoDigits(float: Float): String {
        return NumberFormat.getInstance().run {
            maximumFractionDigits = 0
            format(float)
        }
    }

    private fun setUpLoanAmountSlider() {
        binding.loanAmountSlider.apply {
            setLabelFormatter { value -> formatDollars(value) }
            addOnChangeListener(this@LoanCalculatorFragment)
            value = 300000f
        }
    }

    private fun setUpInterestRateSlider() {
        binding.interestRateSlider.apply {
            setLabelFormatter { value -> formatPercent(value) }
            addOnChangeListener(this@LoanCalculatorFragment)
            value = 1f
        }
    }

    private fun setUpInsuranceRateSlider() {
        binding.insuranceRateSlider.apply {
            setLabelFormatter { value -> formatPercent(value) }
            addOnChangeListener(this@LoanCalculatorFragment)
            value = 0.3f
        }
    }

    private fun setUpLoanDurationSlider() {
        binding.loanDurationSlider.apply {
            setLabelFormatter { value -> formatNoDigits(value) }
            addOnChangeListener(this@LoanCalculatorFragment)
            value = 15f
        }
    }

    private fun updateUI() {
        binding.loanAmountDisplayTextView.text = formatDollars(binding.loanAmountSlider.value)
        binding.interestRateDisplayTextView.text = formatPercent(binding.interestRateSlider.value)
        binding.insuranceRateDisplayTextView.text = formatPercent(binding.insuranceRateSlider.value)
        binding.loanDurationDisplayTextView.text = formatNoDigits(binding.loanDurationSlider.value)

        val result = viewModel.calculateMonthlyPayment(
            binding.loanAmountSlider.value,
            binding.interestRateSlider.value,
            binding.insuranceRateSlider.value,
            binding.loanDurationSlider.value * 12,
        )

        binding.monthlyPaymentResultTextView.text =
            NumberFormat.getCurrencyInstance(Locale.US).run {
                maximumFractionDigits = 2
                format(result)
            }
    }
}