package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.FragmentSearchModalBinding
import fr.azhot.realestatemanager.model.PropertySearch
import fr.azhot.realestatemanager.model.PropertyType
import fr.azhot.realestatemanager.utils.roundIntLower
import fr.azhot.realestatemanager.utils.roundIntUpper
import fr.azhot.realestatemanager.view.adapter.ExposedDropdownMenuAdapter
import fr.azhot.realestatemanager.viewmodel.SearchModalFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.SearchModalFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import java.text.NumberFormat
import java.util.*

class SearchModalFragment : BottomSheetDialogFragment() {

    // variables
    private lateinit var binding: FragmentSearchModalBinding
    private lateinit var navController: NavController
    private val viewModel: SearchModalFragmentViewModel by viewModels {
        SearchModalFragmentViewModelFactory(
            (activity?.application as RealEstateManagerApplication).propertyRepository
        )
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.TransparentBottomSheetDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchModalBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        navController = Navigation.findNavController(requireActivity(), R.id.main_container_view)
        sharedViewModel.livePropertySearch.value?.let { setUpSliders(it) }
        sharedViewModel.livePropertySearch.value?.let { setUpExposedDropdownMenus(it) }
        sharedViewModel.livePropertySearch.value?.let { setUpListeners(it) }
        setUpButtons()
    }

    //functions
    private fun setUpSliders(propertySearch: PropertySearch) {
        setUpSlidersBounds(propertySearch)

        // format label
        binding.priceRangeSlider.setLabelFormatter { value ->
            NumberFormat.getCurrencyInstance(Locale.US).run {
                maximumFractionDigits = 0
                format(value.toDouble())
            }
        }
    }

    private fun setUpSlidersBounds(propertySearch: PropertySearch) {
        propertySearch.run {
            viewModel.getPriceBounds().observe(viewLifecycleOwner) { minMax ->
                binding.priceRangeSlider.apply {
                    minMax.min?.let { valueFrom = roundIntLower(it, stepSize) }
                    minMax.max?.let { valueTo = roundIntUpper(it, stepSize) }
                    values = if (price != null) price!! else listOf(valueFrom, valueTo)
                }
            }

            viewModel.getSquareMetersBounds().observe(viewLifecycleOwner) { minMax ->
                binding.squareMetersRangeSlider.apply {
                    minMax.min?.let { valueFrom = roundIntLower(it, stepSize) }
                    minMax.max?.let { valueTo = roundIntUpper(it, stepSize) }
                    values = if (squareMeters != null) squareMeters!!
                    else listOf(valueFrom, valueTo)
                }
            }

            viewModel.getRoomsBounds().observe(viewLifecycleOwner) { minMax ->
                binding.roomsRangeSlider.apply {
                    minMax.min?.let { valueFrom = roundIntLower(it, stepSize) }
                    minMax.max?.let { valueTo = roundIntUpper(it, stepSize) }
                    values = if (rooms != null) rooms!! else listOf(valueFrom, valueTo)
                }
            }
        }
    }

    private fun setUpExposedDropdownMenus(propertySearch: PropertySearch) {
        binding.propertyTypeFilterAutoComplete.apply {
            val adapter = ExposedDropdownMenuAdapter(
                requireContext(),
                R.layout.exposed_dropdown_menu_item,
                PropertyType.values().toMutableList()
            )
            setAdapter(adapter)
            propertySearch.propertyType?.let { propertyType ->
                setText(propertyType.toString(), false)
            }
        }
    }

    private fun setUpButtons() {
        binding.resetButton.setOnClickListener {
            binding.apply {
                propertyTypeFilterAutoComplete.setText(null, false)
                propertyTypeFilterAutoComplete.clearFocus()
                priceRangeSlider.apply { values = listOf(valueFrom, valueTo) }
                squareMetersRangeSlider.apply { values = listOf(valueFrom, valueTo) }
                roomsRangeSlider.apply { values = listOf(valueFrom, valueTo) }
            }
            sharedViewModel.livePropertySearch.value = PropertySearch()
        }

        binding.okButton.setOnClickListener {
            navController.navigateUp()
        }
    }

    private fun setUpListeners(propertySearch: PropertySearch) {
        binding.propertyTypeFilterAutoComplete.doAfterTextChanged {
            propertySearch.apply {
                propertyType = if (it?.isNotEmpty() == true)
                    PropertyType.valueOf(it.toString().toUpperCase(Locale.ROOT)) else null
            }
            sharedViewModel.livePropertySearch.value = propertySearch
        }

        binding.priceRangeSlider.addOnChangeListener { slider, _, _ ->
            propertySearch.apply {
                price = if (slider.values.containsAll(listOf(slider.valueFrom, slider.valueTo)))
                    null else slider.values
            }
            sharedViewModel.livePropertySearch.value = propertySearch
        }

        binding.squareMetersRangeSlider.addOnChangeListener { slider, _, _ ->
            propertySearch.apply {
                squareMeters =
                    if (slider.values.containsAll(listOf(slider.valueFrom, slider.valueTo)))
                        null else slider.values
            }
            sharedViewModel.livePropertySearch.value = propertySearch
        }

        binding.roomsRangeSlider.addOnChangeListener { slider, _, _ ->
            propertySearch.apply {
                rooms = if (slider.values.containsAll(listOf(slider.valueFrom, slider.valueTo)))
                    null else slider.values
            }
            sharedViewModel.livePropertySearch.value = propertySearch
        }
    }
}