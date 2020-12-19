package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import fr.azhot.realestatemanager.databinding.FragmentAddAddressBinding

class AddAddressFragment : Fragment(), View.OnClickListener {

    // variables
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var navController: NavController


    // overridden functions
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // todo : add homeasup
        binding = FragmentAddAddressBinding.inflate(inflater)
        binding.nextButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.nextButton.id -> goNext()
        }
    }

    // private functions
    private fun goNext() {
        val action = AddAddressFragmentDirections.actionAddAddressFragmentToAddDetailFragment()
        navController.navigate(action)
    }

}