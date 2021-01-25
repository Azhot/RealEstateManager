package fr.azhot.realestatemanager.view.fragment

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.FrameLayout
import androidx.core.util.Pair
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.FragmentSearchModalBinding
import fr.azhot.realestatemanager.model.PointOfInterestType
import fr.azhot.realestatemanager.model.PropertySearch
import fr.azhot.realestatemanager.model.PropertyType
import fr.azhot.realestatemanager.model.Realtor
import fr.azhot.realestatemanager.utils.*
import fr.azhot.realestatemanager.view.adapter.ExposedDropdownMenuAdapter
import fr.azhot.realestatemanager.view.adapter.PoiTypeListAdapter
import fr.azhot.realestatemanager.viewmodel.SearchModalFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.SearchModalFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import java.text.NumberFormat
import java.util.*


class SearchModalFragment : BottomSheetDialogFragment(), View.OnClickListener,
    PoiTypeListAdapter.PoiCheckboxListener {

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

        binding.poiTypeFilterRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PoiTypeListAdapter(
                PointOfInterestType.values(),
                sharedViewModel.livePropertySearch.value?.poiTypeList,
                this@SearchModalFragment
            )
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

    override fun onPoiCheckboxListener(poiType: PointOfInterestType, isChecked: Boolean) {
        sharedViewModel.livePropertySearch.value?.apply {
            if (poiTypeList == null) poiTypeList = mutableListOf()
            if (isChecked) poiTypeList?.add(poiType)
            else poiTypeList?.remove(poiType)
            if (poiTypeList?.isEmpty() == true) poiTypeList = null
        }
        sharedViewModel.livePropertySearch.forceRefresh()
    }

    //functions
    private fun setUpExposedDropdownMenus(propertySearch: PropertySearch) {
        setUpExposedDropdownMenu(
            binding.propertyTypeFilterAutoComplete,
            PropertyType.values().toMutableList(),
            propertySearch.propertyType?.toString(),
        ) { item ->
            sharedViewModel.livePropertySearch.value?.propertyType = item as PropertyType
        }
        setUpExposedDropdownMenu(
            binding.cityFilterAutoComplete,
            mutableListOf(),
            propertySearch.city,
        ) { item ->
            sharedViewModel.livePropertySearch.value?.city = item as String
        }
        setUpExposedDropdownMenu(
            binding.realtorFilterAutoComplete,
            mutableListOf(),
            propertySearch.realtor?.toString()
        ) { item ->
            sharedViewModel.livePropertySearch.value?.realtor = item as Realtor
        }
    }

    private fun setUpExposedDropdownMenu(
        autoCompleteTextView: AutoCompleteTextView,
        mutableList: MutableList<Any>,
        existingData: String?,
        functionToCall: (any: Any?) -> (Unit)
    ) {
        autoCompleteTextView.apply {
            val adapter = ExposedDropdownMenuAdapter(
                requireContext(),
                R.layout.exposed_dropdown_menu_item,
                mutableList
            )
            setAdapter(adapter)
            existingData?.let { setText(it, false) }
            setOnItemClickListener { _, _, position, _ ->
                functionToCall(adapter.getItem(position))
                sharedViewModel.livePropertySearch.forceRefresh()
            }
        }
    }

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
            viewModel.apply {
                getPriceBounds().observe(viewLifecycleOwner) { minMax ->
                    binding.priceRangeSlider.apply {
                        valueFrom = roundIntLower(minMax.min, stepSize)
                        valueTo = roundIntUpper(minMax.max, stepSize)
                        values = priceRange ?: listOf(valueFrom, valueTo)
                    }
                }
                getSquareMetersBounds().observe(viewLifecycleOwner) { minMax ->
                    binding.squareMetersRangeSlider.apply {
                        valueFrom = roundIntLower(minMax.min, stepSize)
                        valueTo = roundIntUpper(minMax.max, stepSize)
                        values = squareMetersRange ?: listOf(valueFrom, valueTo)
                    }
                }
                getRoomsBounds().observe(viewLifecycleOwner) { minMax ->
                    binding.roomsRangeSlider.apply {
                        valueFrom = roundIntLower(minMax.min, stepSize)
                        valueTo = roundIntUpper(minMax.max, stepSize)
                        values = roomsRange ?: listOf(valueFrom, valueTo)
                    }
                }
                getPhotoListMax().observe(viewLifecycleOwner) { max ->
                    binding.photosSlider.apply {
                        max?.let { valueTo = max.toFloat() }
                        value = photoListSize ?: valueFrom
                    }
                }
            }
        }
    }

    private fun setUpDateRangePickerButton(button: Button, dateRange: Pair<Long, Long>?) {
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