package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
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
    private val viewModel: PropertyDetailsFragmentViewModel by viewModels {
        PropertyDetailsFragmentViewModelFactory((activity?.application as RealEstateManagerApplication).detailRepository)
    }


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = initFragmentPropertyDetailsBinding(layoutInflater)
        initPropertyObserver(binding.mediaRecyclerView)
        return binding.root
    }


    // functions
    private fun initFragmentPropertyDetailsBinding(layoutInflater: LayoutInflater) =
        FragmentPropertyDetailsBinding.inflate(layoutInflater)

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
        val adapter = MediaListAdapter(Glide.with(this), property.photos)
        recyclerView.adapter = adapter
    }
}