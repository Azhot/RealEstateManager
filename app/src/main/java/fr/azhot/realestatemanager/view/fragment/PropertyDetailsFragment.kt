package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.azhot.realestatemanager.databinding.FragmentPropertyDetailsBinding
import fr.azhot.realestatemanager.model.Property


class PropertyDetailsFragment(private val property: Property) : Fragment() {


    // companion
    companion object {
        private val TAG = PropertyDetailsFragment::class.simpleName

        @JvmStatic
        fun newInstance(property: Property) = PropertyDetailsFragment(property)
    }


    // variables
    lateinit var mBinding: FragmentPropertyDetailsBinding


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        init(layoutInflater)
        mBinding.name.text = property.address.name
        return mBinding.root
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = FragmentPropertyDetailsBinding.inflate(layoutInflater)
    }
}