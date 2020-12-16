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
            PropertyType.values().toMutableList(),
            binding.propertyTypeAutoComplete
        )

        // building the rooms dropdown menu
        buildExposedMenuDropdown(
            createRoomsListString(),
            binding.roomsAutoComplete
        )

        // building the realtor dropdown menu
        buildExposedMenuDropdown(
            mutableListOf<Realtor>(),
            binding.realtorAutoComplete
        )

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
        val adapter = MediaListAdapter(
            glide = Glide.with(this),
            photoList = mutableListOf()
        )
        binding.mediaRecyclerView.adapter = adapter
    }

    private fun addPhoto() {
        if (!this::detail.isInitialized) {
            detail = Detail()
        }
        val photo = Photo(
            detailId = detail.detailId,
            uri = uri.toString(),
            description = binding.photoInfoEditText.text.toString(),
        )
        (binding.mediaRecyclerView.adapter as MediaListAdapter).addPhoto(photo)
    }

    private fun buildExposedMenuDropdown(
        list: MutableList<*>,
        autoCompleteTextView: AutoCompleteTextView,
    ) {
        val adapter = ExposedDropdownMenuAdapter(
            context = this,
            resource = R.layout.exposed_dropdown_menu_item,
            list = list
        )
        autoCompleteTextView.setAdapter(adapter)
        autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            adapter.position = position
        }
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
        // init dialog
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogAddressBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        // sets fields if address was already filled
        address.zipCode?.let { dialogBinding.zipCodeEditText.setText(it) }
        address.city?.let { dialogBinding.cityEditText.setText(it) }
        address.roadName?.let { dialogBinding.roadNameEditText.setText(it) }
        address.number?.let { dialogBinding.numberEditText.setText(it) }
        address.complement?.let { dialogBinding.complementEditText.setText(it) }

        val dialog = builder.create()

        // add listener for cancel button
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // add listener for set button
        dialogBinding.setButton.setOnClickListener {
            if (dialogBinding.zipCodeEditText.text.toString().isNotEmpty()) {
                address.zipCode = dialogBinding.zipCodeEditText.text.toString().trim()
            }
            if (dialogBinding.cityEditText.text.toString().isNotEmpty()) {
                address.city = dialogBinding.cityEditText.text.toString().trim()
            }
            if (dialogBinding.roadNameEditText.text.toString().isNotEmpty()) {
                address.roadName = dialogBinding.roadNameEditText.text.toString().trim()
            }
            if (dialogBinding.numberEditText.text.toString().isNotEmpty()) {
                address.number = dialogBinding.numberEditText.text.toString().trim()
            }
            if (dialogBinding.complementEditText.text.toString().isNotEmpty()) {
                address.complement = dialogBinding.complementEditText.text.toString().trim()
            }
            textView.text = address.toString()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun initRealtorListObserver() {
        viewModel.realtorList.observe(this, { list ->
            val adapter = binding.realtorAutoComplete.adapter as ExposedDropdownMenuAdapter
            adapter.clear()
            adapter.list = list
            for (realtor in list) {
                adapter.add(realtor.toString())
            }
        })
    }

    private fun buildAddRealtorDialog(functionOnLickAddButton: (firstName: String, lastName: String) -> (Unit)) {
        // init dialog
        val builder = AlertDialog.Builder(this)
        val dialogBinding = DialogAddRealtorBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        // add listener for cancel button
        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        // add listener for add button
        dialogBinding.addButton.setOnClickListener {
            val firstName = dialogBinding.firstNameEditText.text.toString().trim()
            val lastName = dialogBinding.lastNameEditText.text.toString().trim()
            functionOnLickAddButton(firstName, lastName)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun createRealtor(firstName: String?, lastName: String?) {
        val realtor = Realtor().apply {
            firstName?.let { this.firstName = it }
            lastName?.let { this.lastName = it }
        }
        viewModel.insertRealtor(realtor)
    }

    // todo : should only be called when detail is initialized, i.e not before a photo is added
    private fun createProperty() {
        // todo: kept for testing... to be deleted when user is forced to enter a photo to confirm creation
        if (!this::detail.isInitialized) {
            detail = Detail()
        }

        // set propertyType
        (binding.propertyTypeAutoComplete.adapter as ExposedDropdownMenuAdapter).apply {
            this.position?.let { detail.propertyType = this.list[it] as PropertyType }
        }

        // set price
        if (binding.priceEditText.text.toString().isNotEmpty()) {
            detail.price = Integer.valueOf(binding.priceEditText.text.toString())
        }
        // set squareMeters
        if (binding.squareMeterEditText.text.toString().isNotEmpty()) {
            detail.squareMeters = Integer.valueOf(binding.squareMeterEditText.text.toString())
        }

        // set rooms
        (binding.roomsAutoComplete.adapter as ExposedDropdownMenuAdapter).apply {
            this.position?.let { detail.rooms = it + 1 }
        }

        // set description
        if (binding.descriptionEditText.text.toString().isNotEmpty()) {
            detail.description = binding.descriptionEditText.text.toString().trim()
        }

        // set addressId
        if (binding.addressEditText.text.toString().isNotEmpty()) {
            detail.addressId = address.addressId
        }

        // set entryTimeStamp
        if (binding.entryDateEditText.text.toString().isNotEmpty()) {
            detail.entryTimeStamp = entryDate.timeInMillis
        }

        // set saleTimeStamp
        if (binding.saleDateEditText.text.toString().isNotEmpty()) {
            detail.saleTimeStamp = saleDate.timeInMillis
        }

        // set realtorId
        (binding.realtorAutoComplete.adapter as ExposedDropdownMenuAdapter).apply {
            this.position?.let { detail.realtorId = (this.list[it] as Realtor).realtorId }
        }

        // todo : for testing
        /*
        println(detail)
        println(address)
        Toast.makeText(this, "$detail", Toast.LENGTH_SHORT).show()
        Toast.makeText(this, "$address", Toast.LENGTH_SHORT).show()
        */

        // todo : to be implemented
        /*
        viewModel.insertAddress(address)

        viewModel.insertDetail(detail)

        for (photo in (binding.mediaRecyclerView.adapter as MediaListAdapter).photoList) {
            viewModel.insertPhoto(photo)
        }
         */

        /*
        for (pointOfInterest in pointsOfInterest) {
            viewModel.insertPointOfInterest(pointOfInterest)
        }
        */
    }
}
