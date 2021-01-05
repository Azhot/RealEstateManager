package fr.azhot.realestatemanager.view.fragment

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
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
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
        setUpWidgets()
        observePropertyList()
        binding.propertyListRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = PropertyListAdapter(emptyList(), this@PropertyListFragment)
        }
        return binding.root
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
                .replace(binding.detailsContainerView!!.id, PropertyDetailsFragment())
                .commit()
        }
    }

    override fun onPropertyClickListener(property: Property) {
        if (isLandscapeMode) {
            if (property == sharedViewModel.liveProperty.value) return
            childFragmentManager.beginTransaction()
                .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
                .replace(binding.detailsContainerView!!.id, PropertyDetailsFragment())
                .commit()
        } else {
            val action =
                PropertyListFragmentDirections.actionPropertyListFragmentToPropertyDetailsFragment()
            navController.navigate(action)
        }
        sharedViewModel.liveProperty.value = property
    }


    // functions
    private fun setUpWidgets() {
        binding = FragmentPropertyListBinding.inflate(layoutInflater)
    }

    private fun observePropertyList() {
        viewModel.propertyList.observe(viewLifecycleOwner, { list ->
            (binding.propertyListRecyclerView.adapter as PropertyListAdapter).propertyList = list
        })
    }
}