package fr.azhot.realestatemanager.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.repository.PropertyRepository
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener


class PropertyListFragment : Fragment() {


    // companions
    companion object {
        // private val TAG = PropertyListAdapter::class.simpleName

        @JvmStatic
        fun newInstance() = PropertyListFragment()
    }


    // variables
    private lateinit var mBinding: FragmentPropertyListBinding


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
        init(layoutInflater)
        configPropertyRecyclerView(
            mBinding.propertyListRecyclerView,
            PropertyRepository.populatePropertyList(requireContext())
        )
        return mBinding.root
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = FragmentPropertyListBinding.inflate(layoutInflater)
    }

    private fun configPropertyRecyclerView(
        recyclerView: RecyclerView,
        propertyList: List<Property>
    ) {
        recyclerView.layoutManager = LinearLayoutManager(context)
        val adapter = PropertyListAdapter(
            Glide.with(this),
            propertyList,
            (context as PropertyClickListener)
        )
        recyclerView.adapter = adapter
    }
}