package fr.azhot.realestatemanager.view.fragment

import android.app.AlertDialog
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
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
import fr.azhot.realestatemanager.utils.roundInt
import fr.azhot.realestatemanager.view.adapter.ExposedDropdownMenuAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import java.text.NumberFormat
import java.util.*

class PropertyListFragment : Fragment(), PropertyClickListener, Observer<List<Property>> {


    // variables
    private lateinit var binding: FragmentPropertyListBinding
    private lateinit var navController: NavController
    private val viewModel: PropertyListFragmentViewModel by viewModels {
        PropertyListFragmentViewModelFactory(
            (activity?.application as RealEstateManagerApplication).propertyRepository
        )
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = FragmentPropertyListBinding.inflate(layoutInflater)
        setHasOptionsMenu(true)
        setUpPropertyListRecyclerView()
        observePropertyFilterableList()
        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_property -> buildSearchDialog()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.main_container_view)
    }

    override fun onResume() {
        super.onResume()
        if (activity?.resources?.getBoolean(R.bool.isLandscape) == true
            && sharedViewModel.liveProperty.value != null
        ) {
            childFragmentManager.beginTransaction()
                .replace(binding.detailContainerView!!.id, PropertyDetailFragment())
                .commit()
        }
    }

    override fun onChanged(propertyList: List<Property>) {
        (binding.propertyListRecyclerView.adapter as PropertyListAdapter).propertyList =
            propertyList
    }

    override fun onPropertyClickListener(property: Property) {
        if (activity?.resources?.getBoolean(R.bool.isLandscape) == true) {
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
    private fun setUpPropertyListRecyclerView() {
        binding.propertyListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PropertyListAdapter(emptyList(), this@PropertyListFragment)
        }
    }

    private fun observePropertyFilterableList() {
        viewModel.getPropertyFilterableList(sharedViewModel.livePropertySearch).removeObserver(this)
        viewModel.getPropertyFilterableList(sharedViewModel.livePropertySearch)
            .observe(viewLifecycleOwner, this)
    }

    private fun buildSearchDialog() {
        val dialogBinding = DialogSearchBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context).run {
            setView(dialogBinding.root)
            create()
        }

        setUpSliders(dialogBinding)
        setUpExposedDropdownMenus(dialogBinding) { item ->
            sharedViewModel.livePropertySearch.propertyType = item as PropertyType
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

            observePropertyFilterableList()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setUpSliders(dialogBinding: DialogSearchBinding) {
        setUpSlidersBounds(dialogBinding)
        resetSearchWidgets(dialogBinding)

        // format label
        dialogBinding.priceRangeSlider.setLabelFormatter { value ->
            NumberFormat.getCurrencyInstance(Locale.US).run {
                maximumFractionDigits = 0
                format(value.toDouble())
            }
        }
    }

    private fun setUpSlidersBounds(dialogBinding: DialogSearchBinding) {
        viewModel.getPriceBounds().observe(viewLifecycleOwner) { minMax ->
            dialogBinding.priceRangeSlider.apply {
                minMax.min?.let { valueFrom = roundInt(it, stepSize.toInt()).toFloat() }
                minMax.max?.let { valueTo = roundInt(it, stepSize.toInt()).toFloat() }
                values = listOf(valueFrom, valueTo)
                sharedViewModel.livePropertySearch.price?.let { values = it }
            }
        }

        viewModel.getSquareMetersBounds().observe(viewLifecycleOwner) { minMax ->
            dialogBinding.squareMetersRangeSlider.apply {
                minMax.min?.let { valueFrom = roundInt(it, stepSize.toInt()).toFloat() }
                minMax.max?.let { valueTo = roundInt(it, stepSize.toInt()).toFloat() }
                values = listOf(valueFrom, valueTo)
                sharedViewModel.livePropertySearch.squareMeters?.let { values = it }
            }
        }

        viewModel.getRoomsBounds().observe(viewLifecycleOwner) { minMax ->
            dialogBinding.roomsRangeSlider.apply {
                minMax.min?.let { valueFrom = roundInt(it, stepSize.toInt()).toFloat() }
                minMax.max?.let { valueTo = roundInt(it, stepSize.toInt()).toFloat() }
                values = listOf(valueFrom, valueTo)
                sharedViewModel.livePropertySearch.rooms?.let { values = it }
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
            sharedViewModel.livePropertySearch.propertyType?.let { propertyType ->
                setText(propertyType.toString(), false)
            }
            setOnItemClickListener { _, _, position, _ ->
                functionToCall(adapter.getItem(position))
            }
        }
    }

// address (contains with autocomplete)
// entry date -> date range picker
// sale date -> date range picker
// realtor -> exposed dropdown menu
}