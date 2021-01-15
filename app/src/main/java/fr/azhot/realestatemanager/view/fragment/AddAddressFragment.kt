package fr.azhot.realestatemanager.view.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.FragmentAddAddressBinding
import fr.azhot.realestatemanager.viewmodel.SharedViewModel

class AddAddressFragment : Fragment(), View.OnClickListener {

    // variables
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAddressBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        loadDataIfExisting()
        setUpListeners()
        setUpAddressTextWatchers()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.nextButton.id -> alertEmptyFields()
            binding.previousButton.id -> activity?.onBackPressed()
        }
    }


    // private functions
    private fun loadDataIfExisting() {
        sharedViewModel.sharedAddress.run {
            zipCode?.let { binding.zipCodeEditText.setText(it) }
            city?.let { binding.cityEditText.setText(it) }
            roadName?.let { binding.roadNameEditText.setText(it) }
            number?.let { binding.numberEditText.setText(it) }
            complement?.let { binding.complementEditText.setText(it) }
        }
    }

    private fun setUpListeners() {
        binding.previousButton.setOnClickListener(this)
        binding.nextButton.setOnClickListener(this)
    }

    private fun setUpAddressTextWatchers() {
        binding.zipCodeEditText.doAfterTextChanged { editable ->
            sharedViewModel.sharedAddress.zipCode =
                if (editable?.isNotEmpty() == true) editable.toString() else null
        }
        binding.cityEditText.doAfterTextChanged { editable ->
            sharedViewModel.sharedAddress.city =
                if (editable?.isNotEmpty() == true) editable.toString() else null
        }
        binding.roadNameEditText.doAfterTextChanged { editable ->
            sharedViewModel.sharedAddress.roadName =
                if (editable?.isNotEmpty() == true) editable.toString() else null
        }
        binding.numberEditText.doAfterTextChanged { editable ->
            sharedViewModel.sharedAddress.number =
                if (editable?.isNotEmpty() == true) editable.toString() else null
        }
        binding.complementEditText.doAfterTextChanged { editable ->
            sharedViewModel.sharedAddress.complement =
                if (editable?.isNotEmpty() == true) editable.toString() else null
        }
    }

    private fun alertEmptyFields() {
        if (binding.zipCodeEditText.text?.isNotEmpty() == true
            && binding.cityEditText.text?.isNotEmpty() == true
            && binding.roadNameEditText.text?.isNotEmpty() == true
            && binding.numberEditText.text?.isNotEmpty() == true
        ) {
            navigateNext()
            return
        }

        MaterialAlertDialogBuilder(requireContext()).apply {
            setTitle(getString(R.string.alert_title))
            setMessage(getString(R.string.alert_message))
            setPositiveButton(getString(R.string.alert_positive)) { dialog, _ ->
                dialog.dismiss()
                navigateNext()
            }
            setNegativeButton(getString(R.string.alert_negative)) { dialog, _ ->
                dialog.dismiss()
            }
            create()
            show()
        }
    }

    private fun navigateNext() {
        val action = AddAddressFragmentDirections.actionAddAddressFragmentToAddDetailFragment()
        navController.navigate(action)
    }
}