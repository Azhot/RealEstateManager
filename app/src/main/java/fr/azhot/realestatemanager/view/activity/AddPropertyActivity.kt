package fr.azhot.realestatemanager.view.activity

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.MenuItem
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.ActivityAddPropertyBinding
import fr.azhot.realestatemanager.databinding.DialogAddRealtorBinding
import fr.azhot.realestatemanager.databinding.DialogAddressBinding
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.utils.RC_READ_EXTERNAL_STORAGE_PERMISSION
import fr.azhot.realestatemanager.utils.RC_SELECTED_PHOTO
import fr.azhot.realestatemanager.utils.buildMaterialDatePicker
import fr.azhot.realestatemanager.utils.checkPermission
import fr.azhot.realestatemanager.view.adapter.ExposedDropdownMenuAdapter
import fr.azhot.realestatemanager.view.adapter.MediaListAdapter
import fr.azhot.realestatemanager.viewmodel.AddPropertyActivityViewModel
import fr.azhot.realestatemanager.viewmodel.AddPropertyActivityViewModelFactory
import java.util.*


class AddPropertyActivity : AppCompatActivity() {

    companion object {
        //private val TAG = AddPropertyActivity::class.java.simpleName
    }

    // variables
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
    private val binding by lazy { ActivityAddPropertyBinding.inflate(layoutInflater) }
    private val address by lazy { Address() }
    private val pointOfInterestList by lazy { mutableListOf<PointOfInterest>() }
    private val entryDate by lazy { Calendar.getInstance() }
    private val saleDate by lazy { Calendar.getInstance() }
    private lateinit var detail: Detail
    private lateinit var uri: Uri
    private lateinit var realtorList: List<Realtor>


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        // setting the toolbar
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.new_property)

        // todo : change button color when disabled
        binding.addPhotoButton.isEnabled = false
        binding.photoInfoEditText.addTextChangedListener {
            if (this::uri.isInitialized) {
                binding.addPhotoButton.isEnabled =
                    checkIfEnableAddPhotoButton(uri, binding.photoInfoEditText)
            }
        }

        // init photo list recyclerview
        configPhotoListRecyclerView()

        // building the property type dropdown menu
        buildExposedMenuDropdown(
            PropertyType.getValuesAsMutableListString(),
            binding.propertyTypeAutoComplete
        )

        // building the rooms dropdown menu
        buildExposedMenuDropdown(
            createRoomsListString(),
            binding.roomsAutoComplete
        )

        // building the realtor dropdown menu
        buildExposedMenuDropdown(
            mutableListOf(),
            binding.realtorAutoComplete
        )

        // testing purpose

        binding.propertyTypeAutoComplete.setOnItemClickListener { _, _, _, _ ->
            val propertyType = PropertyType.values()[getAdapterPosition(binding.propertyTypeAutoComplete)]
            Toast.makeText(this, "$propertyType", Toast.LENGTH_SHORT).show()
        }

        binding.roomsAutoComplete.setOnItemClickListener { _, _, _, _ ->
            val rooms = getAdapterPosition(binding.roomsAutoComplete) + 1
            Toast.makeText(this, "$rooms", Toast.LENGTH_SHORT).show()
        }

        binding.realtorAutoComplete.setOnItemClickListener { _, _, _, _ ->
            val realtor = realtorList[getAdapterPosition(binding.realtorAutoComplete)]
            Toast.makeText(this, "$realtor", Toast.LENGTH_SHORT).show()
        }

        // init the realtor observer
        initRealtorListObserver()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> finish()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECTED_PHOTO && resultCode == RESULT_OK) {
            if (data != null && data.data != null) {
                uri = data.data!!
                binding.addPhotoButton.isEnabled =
                    checkIfEnableAddPhotoButton(uri, binding.photoInfoEditText)
            }
        }
    }


    // public functions
    fun onClick(v: View?) {
        when (v?.id) {
            binding.selectPhotoButton.id -> configSelectPhotoButton()
            binding.addPhotoButton.id -> addPhoto()
            binding.realtorAddImageButton.id -> buildAddRealtorDialog(::createRealtor)
            binding.addressEditText.id -> buildAddressDialog(binding.addressEditText)
            binding.entryDateEditText.id -> buildMaterialDatePicker(
                supportFragmentManager,
                entryDate,
                binding.entryDateEditText
            )
            binding.saleDateEditText.id -> buildMaterialDatePicker(
                supportFragmentManager,
                saleDate,
                binding.saleDateEditText
            )
            binding.createPropertyButton.id -> {
                createProperty()
            }
        }
    }


    // private functions
    private fun checkIfEnableAddPhotoButton(uri: Uri?, textView: TextView): Boolean {
        return uri != null && textView.text.isNotEmpty()
    }

    private fun configSelectPhotoButton() {
        if (checkPermission(
                this,
                RC_READ_EXTERNAL_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(
                intent,
                RC_SELECTED_PHOTO
            )
        }
    }

    private fun configPhotoListRecyclerView() {
        binding.mediaRecyclerView.layoutManager = LinearLayoutManager(
            this,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        // todo : make a specific adapter with a delete button attached to each photo
        val adapter = MediaListAdapter(Glide.with(this), mutableListOf())
        binding.mediaRecyclerView.adapter = adapter
    }

    private fun addPhoto() {
        if (!this::detail.isInitialized) {
            detail = Detail()
        }
        val photo =
            Photo(detail.detailId, uri.toString(), binding.photoInfoEditText.text.toString())
        (binding.mediaRecyclerView.adapter as MediaListAdapter).addPhoto(photo)
    }

    private fun buildExposedMenuDropdown(
        listString: MutableList<String>,
        autoCompleteTextView: AutoCompleteTextView,
    ) {
        val adapter = ExposedDropdownMenuAdapter(
            this,
            R.layout.exposed_dropdown_menu_item,
            listString
        )
        autoCompleteTextView.setAdapter(adapter)
    }

    private fun createRoomsListString(): MutableList<String> {
        val items = mutableListOf<String>()
        // todo : string resource
        items.add("1 room")
        for (i in 2..19) {
            items.add("$i rooms")
        }
        items.add("20 or more rooms")
        return items
    }

    private fun buildAddressDialog(textView: TextView) {
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogAddressBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        if (address.zipCode.isNotEmpty()) dialogBinding.zipCodeEditText.setText(address.zipCode)
        if (address.city.isNotEmpty()) dialogBinding.cityEditText.setText(address.city)
        if (address.roadName.isNotEmpty()) dialogBinding.roadNameEditText.setText(address.roadName)
        if (address.number.isNotEmpty()) dialogBinding.numberEditText.setText(address.number)
        if (address.complement.isNotEmpty()) dialogBinding.complementEditText.setText(address.complement)
        val dialog = builder.create()
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.setButton.setOnClickListener {
            address.zipCode = dialogBinding.zipCodeEditText.text.toString().trim()
            address.city = dialogBinding.cityEditText.text.toString().trim()
            address.roadName = dialogBinding.roadNameEditText.text.toString().trim()
            address.number = dialogBinding.numberEditText.text.toString().trim()
            address.complement = dialogBinding.complementEditText.text.toString().trim()
            textView.text = address.toString()
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun initRealtorListObserver() {
        viewModel.realtorList.observe(this, { list ->
            realtorList = list
            val adapter = binding.realtorAutoComplete.adapter as ExposedDropdownMenuAdapter
            adapter.clear()
            for (realtor in list) {
                adapter.add(realtor.toString())
            }
        })
    }

    private fun buildAddRealtorDialog(functionOnLickAddButton: (firstName: String, lastName: String) -> (Unit)) {
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogAddRealtorBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)
        val dialog = builder.create()
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }
        dialogBinding.addButton.setOnClickListener {
            val firstName = dialogBinding.firstNameEditText.text.toString().trim()
            val lastName = dialogBinding.lastNameEditText.text.toString().trim()
            functionOnLickAddButton(firstName, lastName)
            dialog.dismiss()
        }
        dialog.show()
    }


    private fun createRealtor(firstName: String, lastName: String) {
        viewModel.insertRealtor(
            Realtor(
                firstName,
                lastName,
            )
        )
    }

    private fun createProperty() {
        // todo : should only be called when detail is initialized, i.e not before a photo is added
        detail.propertyType =
            PropertyType.values()[getAdapterPosition(binding.propertyTypeAutoComplete)]
        detail.price = Integer.valueOf(binding.priceEditText.text.toString())
        detail.squareMeters = Integer.valueOf(binding.squareMeterEditText.text.toString())
        detail.rooms = getAdapterPosition(binding.roomsAutoComplete) + 1
        detail.description = binding.descriptionEditText.text.toString().trim()
        detail.addressId = address.addressId
        detail.entryTimeStamp = entryDate.timeInMillis
        detail.saleTimeStamp = saleDate.timeInMillis
        detail.realtorId = realtorList[getAdapterPosition(binding.realtorAutoComplete)].realtorId

        viewModel.insertAddress(address)

        viewModel.insertDetail(detail)

        for (photo in (binding.mediaRecyclerView.adapter as MediaListAdapter).photoList) {
            viewModel.insertPhoto(photo)
        }

        /*
        for (pointOfInterest in pointsOfInterest) {
            viewModel.insertPointOfInterest(pointOfInterest)
        }
        */
    }

    private fun getAdapterPosition(autoCompleteTextView: AutoCompleteTextView): Int {
        return (autoCompleteTextView.adapter as ExposedDropdownMenuAdapter)
            .getPosition(autoCompleteTextView.text.toString())
    }
}
