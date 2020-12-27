package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel

class PropertyListFragment : Fragment(), PropertyClickListener {


    // variables
    private lateinit var binding: FragmentPropertyListBinding
    private lateinit var navController: NavController
    private val viewModel: PropertyListFragmentViewModel by viewModels {
        PropertyListFragmentViewModelFactory((activity?.application as RealEstateManagerApplication).detailRepository)
    }
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentPropertyListBinding.inflate(layoutInflater)
        resetSharedData()
        observePropertyList()
        binding.propertyListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PropertyListAdapter(Glide.with(this), emptyList(), this@PropertyListFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.main_container_view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onResume() {
        super.onResume()
        if (activity?.resources?.getBoolean(R.bool.isLandscape) == true && sharedViewModel.liveProperty.value != null) {
            childFragmentManager.beginTransaction()
                .replace(binding.detailsContainerView!!.id, PropertyDetailsFragment())
                .commit()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_property -> {
                val action =
                    PropertyListFragmentDirections.actionPropertyListFragmentToAddPhotoFragment()
                navController.navigate(action)
            }
        }
        return true
    }

    override fun onPropertyClickListener(property: Property) {
        if (activity?.resources?.getBoolean(R.bool.isLandscape) == false) {
            val action =
                PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailsFragment()
            navController.navigate(action)
        } else {
            if (property == sharedViewModel.liveProperty.value) return
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(binding.detailsContainerView!!.id, PropertyDetailsFragment())
                .commit()
        }
        sharedViewModel.liveProperty.value = property
    }


    // functions
    private fun resetSharedData() {
        sharedViewModel.livePhotoMap.value?.clear()
    }

    private fun observePropertyList() {
        viewModel.propertyList.observe(viewLifecycleOwner, { list ->
            (binding.propertyListRecyclerView.adapter as PropertyListAdapter).propertyList = list
        })
    }
}