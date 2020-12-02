package fr.azhot.realestatemanager.view.fragment

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.databinding.FragmentPropertyDetailsBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.MediaListAdapter


class PropertyDetailsFragment(private val mProperty: Property) : Fragment() {


    // companion
    companion object {
        private val TAG = PropertyDetailsFragment::class.simpleName

        @JvmStatic
        fun newInstance(property: Property) = PropertyDetailsFragment(property)
    }


    // variables
    private lateinit var mContext: Context
    private lateinit var mBinding: FragmentPropertyDetailsBinding
    private lateinit var mAdapter: MediaListAdapter


    override fun onAttach(context: Context) {
        super.onAttach(context)
        mContext = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init(layoutInflater)
        configMediaRecyclerView()
        return mBinding.root
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = FragmentPropertyDetailsBinding.inflate(layoutInflater)
    }

    private fun configMediaRecyclerView() {
        mBinding.mediaRecyclerView.layoutManager = LinearLayoutManager(
            mContext,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        mAdapter = MediaListAdapter(Glide.with(this), mProperty.photos)
        mBinding.mediaRecyclerView.adapter = mAdapter
    }
}