package fr.azhot.realestatemanager.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.DialogSearchBinding
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.model.PropertySearch
import fr.azhot.realestatemanager.model.PropertyType
import fr.azhot.realestatemanager.model.Realtor
import fr.azhot.realestatemanager.utils.roundInt
import fr.azhot.realestatemanager.view.adapter.ExposedDropdownMenuAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import java.text.NumberFormat
import java.util.*
import kotlin.properties.Delegates

class PropertyListFragment : Fragment(), PropertyClickListener {


    // variables
    private lateinit var binding: FragmentPropertyListBinding
    private lateinit var navController: NavController
    private var isLandscapeMode by Delegates.notNull<Boolean>()
    private val viewModel: PropertyListFragmentViewModel by viewModels {
        PropertyListFragmentViewModelFactory((activity?.application as RealEstateManagerApplication).detailRepository)
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        setUpWidgets()
        observePropertyList()
        binding.propertyListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PropertyListAdapter(emptyList(), emptyList(), this@PropertyListFragment)
        }
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_property -> buildSearchDialog(
                binding.propertyListRecyclerView.adapter as PropertyListAdapter,
                ::filterPropertyList
            )
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.main_container_view)
    }

    override fun onResume() {
        super.onResume()
        isLandscapeMode = activity?.resources?.getBoolean(R.bool.isLandscape) == true
        if (isLandscapeMode && sharedViewModel.liveProperty.value != null) {
            childFragmentManager.beginTransaction()
                .replace(binding.detailContainerView!!.id, PropertyDetailFragment())
                .commit()
        }
    }

    override fun onPropertyClickListener(property: Property) {
        if (isLandscapeMode) {
            if (property == sharedViewModel.liveProperty.value) return
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(binding.detailContainerView!!.id, PropertyDetailFragment())
                .commit()
        } else {
            val action =
                PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment()
            navController.navigate(action)
        }
        sharedViewModel.liveProperty.value = property
    }


    // functions
    private fun setUpWidgets() {
        binding = FragmentPropertyListBinding.inflate(layoutInflater)
    }

    private fun observePropertyList() {
        viewModel.propertyList.observe(viewLifecycleOwner) { propertyList ->
            (binding.propertyListRecyclerView.adapter as PropertyListAdapter).apply {
                unFilteredPropertyList = propertyList
                filteredPropertyList = if (sharedViewModel.livePropertySearch.isActivated()) {
                    filterPropertyList(
                        unFilteredPropertyList,
                        sharedViewModel.livePropertySearch
                    )
                } else {
                    unFilteredPropertyList
                }
            }
        }
    }

    private fun buildSearchDialog(
        propertyListAdapter: PropertyListAdapter,
        functionOnClickApplyButton: (List<Property>, PropertySearch) -> (List<Property>)
    ) {
        val dialogBinding = DialogSearchBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context).run {
            setView(dialogBinding.root)
            create()
        }

        setUpSliders(propertyListAdapter.unFilteredPropertyList, dialogBinding)
        setUpExposedDropdownMenus(dialogBinding) { item ->
            sharedViewModel.livePropertySearch.propertyType = item as PropertyType
        }

        if (sharedViewModel.livePropertySearch.propertyType != null) {
            dialogBinding.propertyTypeFilterAutoComplete.setText(
                sharedViewModel.livePropertySearch.propertyType.toString(),
                false
            )
        }

        sharedViewModel.livePropertySearch.price?.let { values ->
            dialogBinding.priceRangeSlider.values = values
        }

        sharedViewModel.livePropertySearch.squareMeters?.let { values ->
            dialogBinding.squareMetersRangeSlider.values = values
        }

        sharedViewModel.livePropertySearch.rooms?.let { values ->
            dialogBinding.roomsRangeSlider.values = values
        }

        dialogBinding.cancelButton.setOnClickListener { dialog.dismiss() }

        dialogBinding.resetButton.setOnClickListener {
            sharedViewModel.livePropertySearch = PropertySearch()
            resetSearchWidgets(dialogBinding)
        }

        dialogBinding.applyButton.setOnClickListener {
            sharedViewModel.livePropertySearch.apply {
                price = dialogBinding.priceRangeSlider.values
                squareMeters = dialogBinding.squareMetersRangeSlider.values
                rooms = dialogBinding.roomsRangeSlider.values
                photoListSize = dialogBinding.photosSlider.value
            }

            propertyListAdapter.filteredPropertyList =
                functionOnClickApplyButton(
                    propertyListAdapter.unFilteredPropertyList,
                    sharedViewModel.livePropertySearch,
                )
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setUpSliders(
        propertyList: List<Property>,
        dialogBinding: DialogSearchBinding
    ) {
        setUpSlidersBounds(propertyList, dialogBinding)
        resetSearchWidgets(dialogBinding)

        // format label
        dialogBinding.priceRangeSlider.setLabelFormatter { value ->
            NumberFormat.getCurrencyInstance(Locale.US).run {
                maximumFractionDigits = 0
                format(value.toDouble())
            }
        }
    }

    private fun setUpSlidersBounds(
        propertyList: List<Property>,
        dialogBinding: DialogSearchBinding
    ) {
        for (property in propertyList) {
            property.detail.price?.let { price ->
                dialogBinding.priceRangeSlider.apply {
                    if (valueFrom > price) valueFrom = roundInt(price, stepSize.toInt()).toFloat()
                    if (valueTo < price) valueTo = roundInt(price, stepSize.toInt()).toFloat()
                }
            }
            property.detail.squareMeters?.let { squareMeters ->
                dialogBinding.squareMetersRangeSlider.apply {
                    if (valueFrom > squareMeters) valueFrom = squareMeters.toFloat()
                    if (valueTo < squareMeters) valueTo = squareMeters.toFloat()
                }
            }
            property.detail.rooms?.let { rooms ->
                dialogBinding.roomsRangeSlider.apply {
                    if (valueFrom > rooms) valueFrom = rooms.toFloat()
                    if (valueTo < rooms) valueTo = rooms.toFloat()
                }
            }
            property.photoList.let { photoList ->
                dialogBinding.photosSlider.apply {
                    if (valueTo < photoList.size) valueTo = photoList.size.toFloat()
                }
            }
        }
    }

    private fun resetSearchWidgets(dialogBinding: DialogSearchBinding) {
        dialogBinding.propertyTypeFilterAutoComplete.apply {
            setText(null, false)
            clearFocus()
        }
        dialogBinding.priceRangeSlider.apply { values = listOf(valueFrom, valueTo) }
        dialogBinding.squareMetersRangeSlider.apply { values = listOf(valueFrom, valueTo) }
        dialogBinding.roomsRangeSlider.apply { values = listOf(valueFrom, valueTo) }
        dialogBinding.photosSlider.apply { value = valueFrom }
    }

    private fun setUpExposedDropdownMenus(
        dialogBinding: DialogSearchBinding,
        functionToCall: (any: Any?) -> (Unit)
    ) {
        dialogBinding.propertyTypeFilterAutoComplete.apply {
            val adapter = ExposedDropdownMenuAdapter(
                requireContext(),
                R.layout.exposed_dropdown_menu_item,
                PropertyType.values().toMutableList()
            )
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                functionToCall(adapter.getItem(position))
            }
        }
    }

    private fun filterPropertyList(
        propertyList: List<Property>,
        propertySearch: PropertySearch
    ): List<Property> {
        return mutableListOf<Property>().run {
            for (property in propertyList) {
                if (!checkPropertyType(property, propertySearch.propertyType)) continue
                if (!checkPrice(property, propertySearch.price)) continue
                if (!checkSquareMeters(property, propertySearch.squareMeters)) continue
                if (!checkRooms(property, propertySearch.rooms)) continue
                if (!checkPhotoListSize(property, propertySearch.photoListSize)) continue
                this += property
            }
            this
        }
    }

    private fun checkPropertyType(property: Property, value: PropertyType?): Boolean {
        value ?: return true
        property.detail.propertyType ?: return false
        return property.detail.propertyType == value
    }

    // todo : factorize
    private fun checkPrice(property: Property, values: List<Float>?): Boolean {
        values ?: return true
        val price = property.detail.price ?: return false
        return price >= values[0] && price <= values[1]
    }

    private fun checkSquareMeters(property: Property, values: List<Float>?): Boolean {
        values ?: return true
        val squareMeters = property.detail.squareMeters ?: return false
        return squareMeters >= values[0] && squareMeters <= values[1]
    }

    private fun checkRooms(property: Property, values: List<Float>?): Boolean {
        values ?: return true
        val rooms = property.detail.rooms ?: return false
        return rooms >= values[0] && rooms <= values[1]
    }

    private fun checkPhotoListSize(property: Property, value: Float?): Boolean {
        value ?: return true
        return property.photoList.size >= value
    }

    private fun checkRealtor(property: Property, value: Realtor?): Boolean {
        value ?: return true
        property.realtor ?: return false
        return property.realtor == value
    }

// address (contains with autocomplete)
// entry date -> date range picker
// sale date -> date range picker
// realtor -> exposed dropdown menu
}