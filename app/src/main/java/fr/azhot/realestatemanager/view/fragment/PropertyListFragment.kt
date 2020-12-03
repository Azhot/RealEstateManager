package fr.azhot.realestatemanager.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener

class PropertyListFragment(
    private val mListener: PropertyClickListener,
    private val mPropertyList: List<Property>,
) : Fragment() {


    // companion
    companion object {
        // private val TAG = PropertyListAdapter::class.simpleName

        @JvmStatic
        fun newInstance(
            listener: PropertyClickListener,
            list: List<Property>
        ) =
            PropertyListFragment(listener, list)
    }


    // variables
    private lateinit var mContext: Context
    private lateinit var mBinding: FragmentPropertyListBinding
    private lateinit var mAdapter: PropertyListAdapter


    // overridden functions
    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        init(layoutInflater)
        configPropertyRecyclerView()
        return mBinding.root
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = FragmentPropertyListBinding.inflate(layoutInflater)
    }

    private fun configPropertyRecyclerView() {
        mBinding.propertyListRecyclerView.layoutManager = LinearLayoutManager(mContext)
        mAdapter = PropertyListAdapter(Glide.with(this), mPropertyList, mListener)
        mBinding.propertyListRecyclerView.adapter = mAdapter
    }
}