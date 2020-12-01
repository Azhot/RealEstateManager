package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.azhot.realestatemanager.databinding.FragmentPropertyDetailsBinding


class PropertyDetailsFragment : Fragment() {

    lateinit var mBinding: FragmentPropertyDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(layoutInflater)
        return mBinding.root
    }

    companion object {
        @JvmStatic
        fun newInstance() =
            PropertyDetailsFragment().apply {
                arguments = Bundle()
            }
    }

    private fun init(layoutInflater: LayoutInflater) {
        mBinding = FragmentPropertyDetailsBinding.inflate(layoutInflater)
    }
}