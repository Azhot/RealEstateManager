package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.FragmentPropertyDetailsBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.MediaListAdapter
import fr.azhot.realestatemanager.viewmodel.PropertyDetailsFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.PropertyDetailsFragmentViewModelFactory


class PropertyDetailsFragment : Fragment() {


    // companions
    companion object {
        // private val TAG = PropertyDetailsFragment::class.simpleName

        @JvmStatic
        fun newInstance() = PropertyDetailsFragment()
    }


    // variables
    private lateinit var binding: FragmentPropertyDetailsBinding
    private lateinit var navController: NavController
    private val viewModel: PropertyDetailsFragmentViewModel by viewModels {
        PropertyDetailsFragmentViewModelFactory((activity?.application as RealEstateManagerApplication).detailRepository)
    }


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setHasOptionsMenu(true)
        binding = FragmentPropertyDetailsBinding.inflate(layoutInflater)
        initPropertyObserver(binding.mediaRecyclerView)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_property -> {
                val action =
                    PropertyDetailsFragmentDirections.actionPropertyDetailsFragmentToAddPhotoFragment()
                navController.navigate(action)
            }
        }
        return true
    }


    // functions
    private fun initPropertyObserver(recyclerView: RecyclerView) {
        viewModel.liveProperty.observe(viewLifecycleOwner, { property ->
            configMediaRecyclerView(recyclerView, property)
        })
    }

    private fun configMediaRecyclerView(recyclerView: RecyclerView, property: Property) {
        recyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val adapter = MediaListAdapter(Glide.with(this), property.photoList)
        recyclerView.adapter = adapter
    }
}