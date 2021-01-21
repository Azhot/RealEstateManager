package fr.azhot.realestatemanager.view.fragment

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.os.Bundle
import android.view.*
import android.widget.TextView
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
import fr.azhot.realestatemanager.utils.forceRefresh
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
        setUpFilterOnTextView()
        observePropertySearch()
    }

    override fun onResume() {
        super.onResume()
        // if landscape, load detail fragment to the respective container
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

    // androidx.lifecycle.Observer interface
    override fun onChanged(propertyList: List<Property>) {
        binding.propertyListRecyclerView.apply {
            (adapter as PropertyListAdapter).setList(propertyList)
            scrollToPosition(0)
        }
    }

    // PropertyListAdapter callback
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

    // sets the action on click either to the right drawable or the text of filter text view
    private fun setUpFilterOnTextView() {
        binding.filerOnTextView.apply {
            setOnTouchListener { v, event ->
                v.performClick()
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        if (event.rawX >= (v.right - (v as TextView).compoundDrawables[2].bounds.width())) {
                            sharedViewModel.livePropertySearch.apply {
                                value?.clear()
                                forceRefresh()
                            }
                            return@setOnTouchListener true
                        }
                        promptSearchFragment()
                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener false
            }
        }
    }

    private fun observePropertySearch() {
        sharedViewModel.livePropertySearch.observe(viewLifecycleOwner) { propertySearch ->
            // get property data from room
            observePropertyFilterableList(propertySearch)
            // animate filter text view
            binding.filerOnTextView.apply {
                if (propertySearch.isOn()) {
                    alpha = 0f
                    text = propertySearch.toString(context)
                    visibility = View.VISIBLE
                } else {
                    if (visibility != View.GONE) {
                        binding.propertyListRecyclerView
                            .animate()
                            .translationY(-height.toFloat())
                            .duration = 250
                    }
                }
                animate()
                    .alpha(if (propertySearch.isOn()) 1f else 0f)
                    .setDuration(250)
                    .setListener(if (propertySearch.isOn()) null else object :
                        AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator?) {
                            super.onAnimationEnd(animation)
                            text = null
                            binding.propertyListRecyclerView.translationY = 0f
                            visibility = View.GONE
                        }
                    })
            }
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
}