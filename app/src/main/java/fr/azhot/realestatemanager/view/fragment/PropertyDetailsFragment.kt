package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.databinding.FragmentPropertyDetailsBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.MediaListAdapter
import fr.azhot.realestatemanager.viewmodel.PropertyDetailsFragmentViewModel


class PropertyDetailsFragment : Fragment() {


    // companions
    companion object {
        // private val TAG = PropertyDetailsFragment::class.simpleName

        @JvmStatic
        fun newInstance() = PropertyDetailsFragment()
    }


    // variables
    private lateinit var mBinding: FragmentPropertyDetailsBinding
    private lateinit var mViewModel: PropertyDetailsFragmentViewModel


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init(layoutInflater)
        return mBinding.root
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = FragmentPropertyDetailsBinding.inflate(layoutInflater)
        mViewModel = ViewModelProvider(this).get(PropertyDetailsFragmentViewModel::class.java)
        initPropertyObserver(mBinding.mediaRecyclerView)
    }

    private fun initPropertyObserver(recyclerView: RecyclerView) {
        mViewModel.getCurrentProperty().observe(viewLifecycleOwner, { property ->
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