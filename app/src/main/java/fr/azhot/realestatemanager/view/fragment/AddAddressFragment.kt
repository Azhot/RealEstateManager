package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.azhot.realestatemanager.databinding.FragmentAddAddressBinding
import fr.azhot.realestatemanager.model.Address
import fr.azhot.realestatemanager.viewmodel.SharedViewModel

class AddAddressFragment : Fragment(), View.OnClickListener {

    // variables
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by activityViewModels()


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
        binding = FragmentAddAddressBinding.inflate(inflater)
        binding.nextButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.nextButton.id -> alertEmptyFields()
        }
    }

    // private functions
    private fun goNext() {
        Log.d("AddAddressFragment", "goNext")
        val zipCode = if (binding.zipCodeEditText.text.toString().isNotEmpty()) {
            binding.zipCodeEditText.text.toString()
        } else null
        val city = if (binding.cityEditText.text.toString().isNotEmpty()) {
            binding.cityEditText.text.toString()
        } else null

        val roadName = if (binding.roadNameEditText.text.toString().isNotEmpty()) {
            binding.roadNameEditText.text.toString()
        } else null

        val number = if (binding.numberEditText.text.toString().isNotEmpty()) {
            binding.numberEditText.text.toString()
        } else null

        val complement = if (binding.complementEditText.text.toString().isNotEmpty()) {
            binding.complementEditText.text.toString()
        } else null

        val address = Address(
            zipCode = zipCode,
            city = city,
            roadName = roadName,
            number = number,
            complement = complement,
        )

        sharedViewModel.mutableAddress.value = address

        val action = AddAddressFragmentDirections.actionAddAddressFragmentToAddDetailFragment()
        navController.navigate(action)
    }

    private fun alertEmptyFields() {
        Log.d("AddAddressFragment", "alertEmptyFields: ")
        if (binding.zipCodeEditText.text.toString().isNotEmpty()
            && binding.cityEditText.text.toString().isNotEmpty()
            && binding.roadNameEditText.text.toString().isNotEmpty()
            && binding.numberEditText.text.toString().isNotEmpty()
        ) {
            goNext()
            return
        }

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle("Information missing")
            setMessage("One or more fields are not completed, continue anyway ?")
            setPositiveButton("Continue") { dialog, _ ->
                dialog.dismiss()
                goNext()
            }
            setNegativeButton("Edit") { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }
}