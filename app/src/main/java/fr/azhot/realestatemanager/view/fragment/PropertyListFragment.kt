package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import fr.azhot.realestatemanager.databinding.FragmentPropertyListBinding


class PropertyListFragment : Fragment() {

    lateinit var mBinding: FragmentPropertyListBinding

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
            PropertyListFragment().apply {
                arguments = Bundle()
            }
    }

    private fun init(layoutInflater: LayoutInflater) {
        mBinding = FragmentPropertyListBinding.inflate(layoutInflater)
    }
}