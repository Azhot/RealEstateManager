package fr.azhot.realestatemanager.view.activity

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.ActivityAddPropertyBinding
import fr.azhot.realestatemanager.databinding.DialogAddRealtorBinding
import fr.azhot.realestatemanager.databinding.DialogNumberPickerBinding
import fr.azhot.realestatemanager.model.PropertyType
import fr.azhot.realestatemanager.model.Realtor
import fr.azhot.realestatemanager.view.adapter.SpinnerAdapter
import fr.azhot.realestatemanager.viewmodel.AddPropertyActivityViewModel
import fr.azhot.realestatemanager.viewmodel.AddPropertyActivityViewModelFactory
import java.util.*


class AddPropertyActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    companion object {
        //private val TAG = AddPropertyActivity::class.java.simpleName
    }

    // variables
    private lateinit var binding: ActivityAddPropertyBinding
    private val viewModel: AddPropertyActivityViewModel by viewModels {
        val application = (application as RealEstateManagerApplication)
        AddPropertyActivityViewModelFactory(
            application.detailRepository,
            application.addressRepository,
            application.photoRepository,
            application.pointOfInterestRepository,
            application.realtorRepository,
        )
    }
    private lateinit var propertyType: PropertyType
    private var rooms: Int = 0
    private lateinit var realtor: Realtor
    private var realtorList = mutableListOf<Realtor>()


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initActivityAddPropertyBinding(layoutInflater)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "New property"
        setContentView(binding.root)
        initPropertyTypeSpinner(binding.propertyTypeSpinner)
        initRealtorSpinner(binding.realtorSpinner)
        initRealtorListObserver()
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {

    }

    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        if (position > 0) {
            when (parent?.id) {
                binding.propertyTypeSpinner.id -> {
                    propertyType = PropertyType.values()[position - 1]
                    (view as TextView).setTextColor(ContextCompat.getColor(this, R.color.black))
                    Toast.makeText(
                        this@AddPropertyActivity,
                        "$propertyType is selected.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                binding.realtorSpinner.id -> {
                    realtor = realtorList[position - 1]
                    (view as TextView).setTextColor(ContextCompat.getColor(this, R.color.black))
                    Toast.makeText(
                        this@AddPropertyActivity,
                        "${realtor.firstName} ${realtor.lastName} is selected.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }


    // public functions
    fun onClick(v: View?) {
        when (v?.id) {
            binding.realtorAddImageButton.id -> buildAddRealtorDialog()
            binding.roomsButton.id -> buildNumberPickerDialog(binding.roomsTextView)
        }
    }


    // private functions
    private fun initActivityAddPropertyBinding(layoutInflater: LayoutInflater) =
        ActivityAddPropertyBinding.inflate(layoutInflater)

    private fun initPropertyTypeSpinner(spinner: Spinner) {
        spinner.onItemSelectedListener = this
        val spinnerList = mutableListOf<String>()
        spinnerList.add(resources.getString(R.string.hint_property_type_spinner))
        for (type in PropertyType.values()) {
            spinnerList.add(
                type.toString()
                    .toLowerCase(Locale.ROOT)
                    .capitalize(Locale.ROOT)
            )
        }
        val arrayAdapter = SpinnerAdapter(this, R.layout.spinner_item, spinnerList)
        spinner.adapter = arrayAdapter
    }

    private fun buildNumberPickerDialog(textView: TextView) {
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogNumberPickerBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        dialogBinding.numberPicker.maxValue = 100
        dialogBinding.numberPicker.minValue = 0
        dialogBinding.numberPicker.wrapSelectorWheel = false
        val dialog = builder.create()
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.setButton.setOnClickListener {
            rooms = dialogBinding.numberPicker.value
            textView.text = "$rooms room(s)"
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initRealtorListObserver() {
        viewModel.realtorList.observe(this, { list ->
            realtorList.clear()
            realtorList.addAll(list)
            val spinnerList = mutableListOf<String>()
            spinnerList.add(resources.getString(R.string.hint_realtor_spinner))
            for (realtor in list) {
                spinnerList.add("${realtor.firstName} ${realtor.lastName}")
            }
            (binding.realtorSpinner.adapter as SpinnerAdapter).updateList(spinnerList)
        })
    }

    private fun initRealtorSpinner(spinner: Spinner) {
        spinner.onItemSelectedListener = this
        val arrayAdapter = SpinnerAdapter(this, R.layout.spinner_item, mutableListOf())
        spinner.adapter = arrayAdapter
    }

    private fun buildAddRealtorDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogAddRealtorBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.addButton.setOnClickListener {
            val newRealtor = Realtor(
                dialogBinding.firstNameEditText.text.toString().trim(),
                dialogBinding.lastNameEditText.text.toString().trim(),
            )
            viewModel.insertRealtor(newRealtor)
            dialog.dismiss()
        }
        dialog.show()
    }
}
