package fr.azhot.realestatemanager.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding
import fr.azhot.realestatemanager.model.Address
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.model.PropertyType
import fr.azhot.realestatemanager.model.Realtor
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import java.util.*

class PropertyListFragment(private val mListener: PropertyClickListener) : Fragment() {


    // companion
    companion object {
        private val TAG = PropertyListAdapter::class.simpleName

        @JvmStatic
        fun newInstance(listener: PropertyClickListener) = PropertyListFragment(listener)
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
        configRecyclerView()
        return mBinding.root
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = FragmentPropertyListBinding.inflate(layoutInflater)
    }

    private fun configRecyclerView() {
        mBinding.propertyListRecyclerView.layoutManager = LinearLayoutManager(context)
        mAdapter = PropertyListAdapter(mContext, populateList(), mListener)
        mBinding.propertyListRecyclerView.adapter = mAdapter
    }

    private fun populateList(): List<Property> {
        val addresses = mutableListOf(
            Address(
                1L,
                "Apartment",
                "78480",
                "Verneuil-sur-seine",
                "rue du Hameau",
                "52",
                "TER",
            ),
            Address(
                2L,
                "House",
                "75116",
                "Paris",
                "rue du Bizon",
                "15",
                null,
            ),
        )

        val realtors = mutableListOf(
            Realtor(
                1L,
                "François",
                "Jouvelot",
            ),
            Realtor(
                2L,
                "Gisèle",
                "Sellier",
            ),
        )

        return listOf(
            Property(
                1L,
                PropertyType.FLAT,
                1500000,
                150,
                5,
                "This is a big flat",
                mutableListOf(),
                addresses[0],
                addresses,
                false,
                Date(),
                null,
                realtors[0],
            ),
            Property(
                2L,
                PropertyType.HOUSE,
                2500000,
                250,
                9,
                "This is a big house",
                mutableListOf(),
                addresses[1],
                addresses,
                false,
                Date(),
                null,
                realtors[1],
            )
        )
    }
}