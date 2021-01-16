package fr.azhot.realestatemanager.view.fragment

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
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.model.PropertySearch
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
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
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPropertyListBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.main_container_view)
        setHasOptionsMenu(true)
        setUpPropertyListRecyclerView()
        observePropertySearch()
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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.search_property -> promptSearchFragment()
            R.id.edit_property -> navigateEditProperty()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onChanged(propertyList: List<Property>) {
        (binding.propertyListRecyclerView.adapter as PropertyListAdapter).setList(
            propertyList
        )
    }

    override fun onPropertyClickListener(property: Property) {
        if (activity?.resources?.getBoolean(R.bool.isLandscape) == true) {
            if (property == sharedViewModel.liveProperty.value) return
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(binding.detailContainerView!!.id, PropertyDetailFragment())
                .commit()
        } else {
            navController.navigate(
                PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailFragment()
            )
        }
        sharedViewModel.liveProperty.value = property
    }


    // functions
    private fun setUpPropertyListRecyclerView() {
        binding.propertyListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PropertyListAdapter(mutableListOf(), this@PropertyListFragment)
        }
    }

    private fun observePropertySearch() {
        sharedViewModel.livePropertySearch.observe(viewLifecycleOwner) {
            observePropertyFilterableList(it)
        }
    }

    private fun observePropertyFilterableList(propertySearch: PropertySearch) {
        viewModel.getPropertyFilterableList(propertySearch).removeObserver(this)
        viewModel.getPropertyFilterableList(propertySearch)
            .observe(viewLifecycleOwner, this)
    }

    private fun promptSearchFragment() {
        navController.navigate(
            PropertyListFragmentDirections.actionPropertyListFragmentToSearchModalFragment()
        )
    }

    private fun navigateEditProperty() {
        navController.navigate(
            PropertyListFragmentDirections.actionPropertyListFragmentToAddPhotoFragment(true)
        )
    }

// address (contains with autocomplete)
// entry date -> date range picker
// sale date -> date range picker
// realtor -> exposed dropdown menu
}