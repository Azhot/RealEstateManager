package fr.azhot.realestatemanager.view.fragment

import android.content.Context
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
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.PropertyListFragmentViewModelFactory


class PropertyListFragment : Fragment() {


    // companions
    companion object {
        // private val TAG = PropertyListAdapter::class.simpleName

        @JvmStatic
        fun newInstance() = PropertyListFragment()
    }


    // variables
    private lateinit var binding: FragmentPropertyListBinding
    private val viewModel: PropertyListFragmentViewModel by viewModels {
        PropertyListFragmentViewModelFactory((activity?.application as RealEstateManagerApplication).detailRepository)
    }
    private val adapter by lazy {
        PropertyListAdapter(Glide.with(this), listOf(), context as PropertyClickListener)
    }


    // overridden functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context !is PropertyClickListener) {
            throw RuntimeException("$context must implement PropertyClickListener")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        binding = initFragmentPropertyListBinding(layoutInflater)
        initPropertyListObserver(adapter)
        configPropertyRecyclerView(binding.propertyListRecyclerView, adapter)
        return binding.root
    }


    // functions
    private fun initFragmentPropertyListBinding(layoutInflater: LayoutInflater) =
        FragmentPropertyListBinding.inflate(layoutInflater)

    private fun initPropertyListObserver(adapter: PropertyListAdapter) {
        viewModel.propertyList.observe(viewLifecycleOwner, { list ->
            adapter.setPropertyList(list)
        })
    }

    private fun configPropertyRecyclerView(
        recyclerView: RecyclerView,
        adapter: PropertyListAdapter
    ) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
    }
}