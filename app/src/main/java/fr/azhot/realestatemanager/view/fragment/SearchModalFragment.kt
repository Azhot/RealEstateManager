package fr.azhot.realestatemanager.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.LiveData
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.slider.RangeSlider
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.FragmentSearchModalBinding
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.utils.*
import fr.azhot.realestatemanager.view.adapter.CheckBoxDropdownAdapter
import fr.azhot.realestatemanager.view.adapter.ExposedDropdownMenuAdapter
import fr.azhot.realestatemanager.viewmodel.SearchModalFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.SearchModalFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import java.text.NumberFormat
import java.util.*


class SearchModalFragment : BottomSheetDialogFragment(), View.OnClickListener,
    CheckBoxDropdownAdapter.ItemCheckListener {

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
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        // open dialog fragment in expanded mode
        return (super.onCreateDialog(savedInstanceState) as BottomSheetDialog).run {
            setOnShowListener {
                findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)?.let { frameLayout ->
                    BottomSheetBehavior.from(frameLayout).state = BottomSheetBehavior.STATE_EXPANDED
                }
            }
            this // returned bottom sheet dialog loaded with OnShowListener
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // set dialog fragment's background to transparent
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
        sharedViewModel.livePropertySearch.value?.let { propertySearch ->
            setUpExposedDropdownMenus(propertySearch)
            setUpSliders(propertySearch)
            setUpDateRangePickerButton(binding.entryDateButton, propertySearch.entryDateRange)
            setUpDateRangePickerButton(binding.saleDateButton, propertySearch.saleDateRange)
            setUpListeners(propertySearch)
        }
        observeCityList()
        observeRealtorList()
        setUpButtons()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.entryDateButton.id ->
                buildMaterialDateRangePicker(
                    childFragmentManager,
                    sharedViewModel.livePropertySearch.value?.entryDateRange,
                ) { newDateRange ->
                    binding.entryDateButton.text = getString(
                        R.string.range_result,
                        formatTimeStamp(newDateRange.first!!),
                        formatTimeStamp(newDateRange.second!!)
                    )
                    sharedViewModel.livePropertySearch.apply {
                        value?.entryDateRange = newDateRange
                        forceRefresh()
                    }
                }

            binding.saleDateButton.id ->
                buildMaterialDateRangePicker(
                    childFragmentManager,
                    sharedViewModel.livePropertySearch.value?.saleDateRange,
                ) { newDateRange ->
                    binding.saleDateButton.text = getString(
                        R.string.range_result,
                        formatTimeStamp(newDateRange.first!!),
                        formatTimeStamp(newDateRange.second!!)
                    )
                    sharedViewModel.livePropertySearch.apply {
                        value?.saleDateRange = newDateRange
                        forceRefresh()
                    }
                }
        }
    }

    override fun <T> onItemCheckListener(isChecked: Boolean, item: T) {
        if (sharedViewModel.livePropertySearch.value?.poiTypeList == null)
            sharedViewModel.livePropertySearch.value?.poiTypeList = mutableListOf()
        sharedViewModel.livePropertySearch.value?.poiTypeList?.apply list@{
            item as PointOfInterestType
            if (isChecked && !this.contains(item)) this.add(item) else this.remove(item)
            if (this.isEmpty()) {
                sharedViewModel.livePropertySearch.value?.poiTypeList = null
                binding.poiTypeFilterAutoComplete.apply {
                    setText(null, false)
                    clearFocus()
                }
            } else {
                binding.poiTypeFilterAutoComplete.setText(listToText(this@list), false)
            }
        }
        sharedViewModel.livePropertySearch.forceRefresh()
    }


    //functions
    private fun setUpExposedDropdownMenus(propertySearch: PropertySearch) {
        binding.propertyTypeFilterAutoComplete.apply {
            setAdapter(
                ExposedDropdownMenuAdapter(
                    context,
                    R.layout.exposed_dropdown_menu_item,
                    PropertyType.values().toMutableList()
                )
            )
            propertySearch.propertyType?.let { setText(it.toString(), false) }
        }

        binding.cityFilterAutoComplete.apply {
            setAdapter(
                ExposedDropdownMenuAdapter(
                    context,
                    R.layout.exposed_dropdown_menu_item,
                    mutableListOf()
                )
            )
            setText(propertySearch.city, false)
        }

        binding.poiTypeFilterAutoComplete.apply {
            sharedViewModel.livePropertySearch.value?.poiTypeList?.let {
                setText(listToText(it), false)
            }
            setAdapter(
                CheckBoxDropdownAdapter(
                    requireContext(),
                    R.layout.cell_poi_type,
                    PointOfInterestType.values().toMutableList(),
                    sharedViewModel.livePropertySearch.value?.poiTypeList,
                    this@SearchModalFragment
                )
            )
        }

        binding.realtorFilterAutoComplete.apply {
            setAdapter(
                ExposedDropdownMenuAdapter(
                    context,
                    R.layout.exposed_dropdown_menu_item,
                    mutableListOf()
                )
            )
            propertySearch.realtor?.let { setText(it.toString(), false) }
        }
    }

    private fun listToText(list: MutableList<PointOfInterestType>?): String? {
        if (list == null) return null
        StringBuilder().apply {
            for (i in 0..list.lastIndex) {
                append("${list[i]}")
                if (i != list.lastIndex && list.isNotEmpty()) append(", ")
            }
            return toString()
        }
    }

    private fun setUpSliders(propertySearch: PropertySearch) {
        setUpSliderBounds(
            binding.priceRangeSlider,
            viewModel.getPriceBounds(),
            propertySearch.priceRange,
        )

        binding.priceRangeSlider.setLabelFormatter { value ->
            NumberFormat.getCurrencyInstance(Locale.US).run {
                maximumFractionDigits = 0
                format(value.toDouble())
            }
        }

        setUpSliderBounds(
            binding.roomsRangeSlider,
            viewModel.getRoomsBounds(),
            propertySearch.roomsRange,
        )

        setUpSliderBounds(
            binding.squareMetersRangeSlider,
            viewModel.getSquareMetersBounds(),
            propertySearch.squareMetersRange,
        )

        viewModel.getPhotoListMax().observe(viewLifecycleOwner) { max ->
            binding.photosSlider.apply {
                max?.let { valueTo = max.toFloat() }
                value = propertySearch.photoListSize ?: valueFrom
            }
        }
    }

    private fun setUpSliderBounds(
        slider: RangeSlider,
        bounds: LiveData<MinMax>,
        existingData: List<Float>?,
    ) {
        bounds.observe(viewLifecycleOwner) { minMax ->
            slider.apply {
                valueFrom = roundIntLower(minMax.min, stepSize)
                valueTo = roundIntUpper(minMax.max, stepSize)
                values = existingData ?: listOf(valueFrom, valueTo)
            }
        }
    }

    private fun setUpDateRangePickerButton(
        button: Button,
        dateRange: Pair<Long, Long>?
    ) {
        dateRange?.let { range ->
            button.text = getString(
                R.string.range_result,
                formatTimeStamp(range.first!!),
                formatTimeStamp(range.second!!)
            )
        }
    }

    private fun setUpButtons() {
        binding.apply {
            resetButton.setOnClickListener {
                binding.apply {
                    propertyTypeFilterAutoComplete.apply {
                        setText(null, false)
                        clearFocus()
                    }
                    cityFilterAutoComplete.apply {
                        setText(null, false)
                        clearFocus()
                    }
                    priceRangeSlider.apply { values = listOf(valueFrom, valueTo) }
                    squareMetersRangeSlider.apply { values = listOf(valueFrom, valueTo) }
                    roomsRangeSlider.apply { values = listOf(valueFrom, valueTo) }
                    photosSlider.apply { value = valueFrom }
                    entryDateButton.text = getString(R.string.entry_date_range)
                    saleDateButton.text = getString(R.string.sale_date_range)
                    poiTypeFilterAutoComplete.apply {
                        setText(null, false)
                        clearFocus()
                    }
                    realtorFilterAutoComplete.apply {
                        setText(null, false)
                        clearFocus()
                    }
                }
                sharedViewModel.livePropertySearch.apply {
                    value?.clear()
                    forceRefresh()
                }
            }
            okButton.setOnClickListener {
                navController.navigateUp()
            }
        }
    }

    private fun setUpListeners(propertySearch: PropertySearch) {
        binding.apply {
            propertyTypeFilterAutoComplete.setOnItemClickListener { _, _, position, _ ->
                propertySearch.propertyType =
                    propertyTypeFilterAutoComplete.adapter.getItem(position) as PropertyType
                sharedViewModel.livePropertySearch.forceRefresh()
            }
            cityFilterAutoComplete.setOnItemClickListener { _, _, position, _ ->
                propertySearch.city = cityFilterAutoComplete.adapter.getItem(position) as String
                sharedViewModel.livePropertySearch.forceRefresh()
            }
            priceRangeSlider.addOnChangeListener { slider, _, _ ->
                propertySearch.priceRange =
                    if (slider.values.containsAll(listOf(slider.valueFrom, slider.valueTo))) null
                    else slider.values
                sharedViewModel.livePropertySearch.forceRefresh()
            }
            squareMetersRangeSlider.addOnChangeListener { slider, _, _ ->
                propertySearch.squareMetersRange =
                    if (slider.values.containsAll(listOf(slider.valueFrom, slider.valueTo))) null
                    else slider.values
                sharedViewModel.livePropertySearch.forceRefresh()
            }
            roomsRangeSlider.addOnChangeListener { slider, _, _ ->
                propertySearch.roomsRange =
                    if (slider.values.containsAll(listOf(slider.valueFrom, slider.valueTo))) null
                    else slider.values
                sharedViewModel.livePropertySearch.forceRefresh()
            }
            photosSlider.addOnChangeListener { slider, value, _ ->
                propertySearch.photoListSize = if (value != slider.valueFrom) value else null
                sharedViewModel.livePropertySearch.forceRefresh()
            }
            entryDateButton.setOnClickListener(this@SearchModalFragment)
            saleDateButton.setOnClickListener(this@SearchModalFragment)
            realtorFilterAutoComplete.setOnItemClickListener { _, _, position, _ ->
                propertySearch.realtor =
                    realtorFilterAutoComplete.adapter.getItem(position) as Realtor
                sharedViewModel.livePropertySearch.forceRefresh()
            }
        }
    }

    private fun observeRealtorList() {
        viewModel.realtorList.observe(viewLifecycleOwner, { realtorList ->
            (binding.realtorFilterAutoComplete.adapter as ExposedDropdownMenuAdapter)
                .list = realtorList.toMutableList()
        })
    }

    private fun observeCityList() {
        viewModel.cityList.observe(viewLifecycleOwner, { cityList ->
            (binding.cityFilterAutoComplete.adapter as ExposedDropdownMenuAdapter)
                .list = cityList.toMutableList()
        })
    }
}