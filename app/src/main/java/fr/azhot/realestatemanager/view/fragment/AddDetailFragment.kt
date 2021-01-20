package fr.azhot.realestatemanager.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.DialogAddPointOfInterestBinding
import fr.azhot.realestatemanager.databinding.DialogAddRealtorBinding
import fr.azhot.realestatemanager.databinding.FragmentAddDetailBinding
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.utils.*
import fr.azhot.realestatemanager.view.adapter.AddPointOfInterestListAdapter
import fr.azhot.realestatemanager.view.adapter.ExposedDropdownMenuAdapter
import fr.azhot.realestatemanager.viewmodel.AddDetailFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.AddDetailFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.text.NumberFormat
import java.util.*

class AddDetailFragment : Fragment(), View.OnClickListener,
    AddPointOfInterestListAdapter.OnDeletePointOfInterestListener {

    // variables
    private lateinit var binding: FragmentAddDetailBinding
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: AddDetailFragmentViewModel by viewModels {
        AddDetailFragmentViewModelFactory(
            (activity?.application as RealEstateManagerApplication).propertyRepository
        )
    }


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddDetailBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setUpWidgets()
        setUpListeners()
        observeRealtorList()
        loadDataIfExisting()
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.addPointOfInterestButton.id -> buildAddPointOfInterestDialog(::addPointOfInterest)
            binding.createRealtorImageButton.id -> buildAddRealtorDialog(::insertRealtor)
            binding.entryDateEditText.id -> buildEntryDatePicker()
            binding.saleDateEditText.id -> buildSaleDatePicker()
            binding.createOrUpdatePropertyButton.id -> createOrUpdateProperty()
            binding.previousButton.id -> activity?.onBackPressed()
        }
    }

    override fun onDeletePointOfInterest(pointOfInterest: PointOfInterest) {
        (binding.pointOfInterestRecyclerView.adapter as AddPointOfInterestListAdapter).apply {
            val index = pointOfInterestList.indexOf(pointOfInterest)
            pointOfInterestList.remove(pointOfInterest)
            notifyItemRemoved(index)
        }
    }


    // private functions
    private fun setUpWidgets() {
        buildExposedDropdownMenu(
            binding.propertyTypeAutoComplete,
            PropertyType.values().toMutableList()
        ) { item ->
            sharedViewModel.sharedDetail.propertyType = item as PropertyType
        }
        setUpNumberFormattingEditText(binding.priceEditText) { price ->
            sharedViewModel.sharedDetail.price = price
        }
        setUpNumberFormattingEditText(binding.squareMetersEditText) { squareMeters ->
            sharedViewModel.sharedDetail.squareMeters = squareMeters
        }
        setUpNumberFormattingEditText(binding.roomsEditText) { rooms ->
            sharedViewModel.sharedDetail.rooms = rooms
        }
        buildPointOfInterestRecyclerView()
        buildExposedDropdownMenu(binding.realtorAutoComplete, mutableListOf()) { item ->
            sharedViewModel.sharedDetail.realtorId = (item as Realtor).realtorId
        }
        if (arguments?.let { AddDetailFragmentArgs.fromBundle(it).editMode } == true) {
            binding.createOrUpdatePropertyButton.text = getString(R.string.update)
        }
    }

    private fun setUpListeners() {
        binding.addPointOfInterestButton.setOnClickListener(this)
        binding.entryDateEditText.setOnClickListener(this)
        binding.entryDateEditText.doAfterTextChanged { editable ->
            if (editable?.isEmpty() == true) sharedViewModel.sharedDetail.entryTimeStamp = null
        }
        binding.saleDateEditText.setOnClickListener(this)
        binding.saleDateEditText.doAfterTextChanged { editable ->
            if (editable?.isEmpty() == true) sharedViewModel.sharedDetail.saleTimeStamp = null
        }
        binding.createRealtorImageButton.setOnClickListener(this)
        binding.descriptionEditText.doAfterTextChanged { editable ->
            sharedViewModel.sharedDetail.description =
                if (editable?.isNotEmpty() == true) editable.toString() else null
        }
        binding.previousButton.setOnClickListener(this)
        binding.createOrUpdatePropertyButton.setOnClickListener(this)
    }

    private fun observeRealtorList() {
        viewModel.realtorList.observe(viewLifecycleOwner, { realtorList ->
            (binding.realtorAutoComplete.adapter as ExposedDropdownMenuAdapter)
                .list = realtorList.toMutableList()
        })
    }

    private fun loadDataIfExisting() {
        sharedViewModel.sharedDetail.run {
            propertyType?.let { binding.propertyTypeAutoComplete.setText(it.toString(), false) }
            price?.let { binding.priceEditText.setText(it.toString()) }
            squareMeters?.let { binding.squareMetersEditText.setText(it.toString()) }
            rooms?.let { binding.roomsEditText.setText(it.toString()) }
            description?.let { binding.descriptionEditText.setText(it) }
            entryTimeStamp?.let { binding.entryDateEditText.setText(formatTimeStamp(it)) }
            saleTimeStamp?.let { binding.saleDateEditText.setText(formatTimeStamp(it)) }
            realtorId?.let { realtorId ->
                viewModel.getRealtorById(realtorId).observe(viewLifecycleOwner) { realtor ->
                    binding.realtorAutoComplete.setText(realtor.toString(), false)
                }
            }
        }
    }

    private fun buildExposedDropdownMenu(
        autoCompleteTextView: AutoCompleteTextView,
        mutableList: MutableList<Any>,
        functionToCall: (any: Any?) -> (Unit)
    ) {
        autoCompleteTextView.apply {
            val adapter = ExposedDropdownMenuAdapter(
                requireContext(),
                R.layout.exposed_dropdown_menu_item,
                mutableList
            )
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                functionToCall(adapter.getItem(position))
            }
        }
    }

    private fun setUpNumberFormattingEditText(
        editText: EditText,
        functionToCall: (number: Int?) -> (Unit)
    ) {
        editText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                var number: Int? = null
                if (s?.isNotEmpty() == true) {
                    editText.removeTextChangedListener(this)
                    number = Integer.valueOf(s.toString().replace(",", ""))
                    editText.setText(
                        NumberFormat.getInstance(Locale.US).run {
                            maximumFractionDigits = 0
                            format(number)
                        }
                    )
                    editText.text?.length?.let { length ->
                        editText.setSelection(length)
                    }
                    editText.addTextChangedListener(this)
                }
                functionToCall(number)
            }

        })
    }

    private fun buildPointOfInterestRecyclerView() {
        binding.pointOfInterestRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = AddPointOfInterestListAdapter(
                sharedViewModel.sharedPointOfInterestList,
                this@AddDetailFragment
            )
        }
    }

    private fun buildAddPointOfInterestDialog(
        functionOnClickAddButton: (
            name: String,
            zipCode: String?,
            city: String?,
            roadName: String?,
            number: String?,
            complement: String?,
        ) -> (Unit)
    ) {
        val dialogBinding = DialogAddPointOfInterestBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context).run {
            setView(dialogBinding.root)
            create()
        }

        dialogBinding.nameEditText.doAfterTextChanged {
            dialogBinding.addButton.isEnabled =
                dialogBinding.nameEditText.text.toString().isNotEmpty()
            updateButtonColor(dialogBinding.addButton)
        }

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.addButton.setOnClickListener {
            val name = dialogBinding.nameEditText.text.toString().trim()
            val zipCode = if (dialogBinding.zipCodeEditText.text?.isNotEmpty() == true)
                dialogBinding.zipCodeEditText.text.toString().trim() else null
            val city = if (dialogBinding.cityEditText.text?.isNotEmpty() == true)
                dialogBinding.cityEditText.text.toString().trim() else null
            val roadName = if (dialogBinding.roadNameEditText.text?.isNotEmpty() == true)
                dialogBinding.roadNameEditText.text.toString().trim() else null
            val number = if (dialogBinding.numberEditText.text?.isNotEmpty() == true)
                dialogBinding.numberEditText.text.toString().trim() else null
            val complement = if (dialogBinding.complementEditText.text?.isNotEmpty() == true)
                dialogBinding.complementEditText.text.toString().trim() else null

            functionOnClickAddButton(name, zipCode, city, roadName, number, complement)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun addPointOfInterest(
        name: String,
        zipCode: String?,
        city: String?,
        roadName: String?,
        number: String?,
        complement: String?,
    ) {
        val address = if (zipCode != null || city != null || roadName != null || number != null
            || complement != null
        ) {
            Address(
                zipCode = zipCode,
                city = city,
                roadName = roadName,
                number = number,
                complement = complement
            )
        } else null

        sharedViewModel.sharedPointOfInterestList.add(
            PointOfInterest(
                detailId = sharedViewModel.sharedDetail.detailId,
                name = name,
                address = address
            )
        )

        (binding.pointOfInterestRecyclerView.adapter as AddPointOfInterestListAdapter).notifyItemInserted(
            sharedViewModel.sharedPointOfInterestList.size
        )
    }

    private fun updateButtonColor(button: Button) {
        if (button.isEnabled) {
            button.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.primaryDarkColor)
            )
        } else {
            button.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.gray)
            )
        }
    }

    private fun buildAddRealtorDialog(functionOnClickAddButton: (firstName: String, lastName: String) -> (Unit)) {
        val dialogBinding = DialogAddRealtorBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(context).run {
            setView(dialogBinding.root)
            create()
        }

        dialogBinding.firstNameEditText.doAfterTextChanged {
            dialogBinding.addButton.isEnabled =
                dialogBinding.firstNameEditText.text?.isNotEmpty() == true
                        && dialogBinding.lastNameEditText.text?.isNotEmpty() == true
            updateButtonColor(dialogBinding.addButton)
        }

        dialogBinding.lastNameEditText.doAfterTextChanged {
            dialogBinding.addButton.isEnabled =
                dialogBinding.firstNameEditText.text?.isNotEmpty() == true
                        && dialogBinding.lastNameEditText.text?.isNotEmpty() == true
            updateButtonColor(dialogBinding.addButton)
        }

        dialogBinding.cancelButton.setOnClickListener {
            dialog.dismiss()
        }

        dialogBinding.addButton.setOnClickListener {
            val firstName = dialogBinding.firstNameEditText.text.toString().trim()
            val lastName = dialogBinding.lastNameEditText.text.toString().trim()
            functionOnClickAddButton(firstName, lastName)
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun insertRealtor(firstName: String, lastName: String) {
        Realtor(firstName = firstName, lastName = lastName).also { realtor ->
            binding.realtorAutoComplete.apply {
                with(adapter as ExposedDropdownMenuAdapter) {
                    for (item in list) {
                        if (item.toString() == realtor.toString()) {
                            makeSnackBar(
                                binding.root,
                                getString(R.string.realtor_already_exists, item),
                                requireContext()
                            )
                            return
                        }
                    }
                }
                sharedViewModel.sharedDetail.realtorId = realtor.realtorId
                setText(realtor.toString(), false)
            }
            viewModel.insertRealtor(realtor)
        }
    }

    private fun makeSnackBar(view: View, string: String, context: Context) {
        Snackbar.make(view, string, Snackbar.LENGTH_SHORT)
            .setBackgroundTint(ContextCompat.getColor(context, R.color.primaryColor))
            .show()
    }

    private fun buildEntryDatePicker() {
        sharedViewModel.sharedDetail.entryTimeStamp.let { timeInMillis ->
            buildMaterialDatePicker(
                childFragmentManager,
                timeInMillis ?: Calendar.getInstance().run {
                    set(Calendar.HOUR_OF_DAY, 12)
                    this.timeInMillis
                },
            ) { selectedTimeInMillis ->
                binding.entryDateEditText.setText(formatTimeStamp(selectedTimeInMillis))
                sharedViewModel.sharedDetail.entryTimeStamp = selectedTimeInMillis
            }
        }
    }

    private fun buildSaleDatePicker() {
        sharedViewModel.sharedDetail.saleTimeStamp.let { timeInMillis ->
            buildMaterialDatePicker(
                childFragmentManager,
                timeInMillis ?: Calendar.getInstance().run {
                    set(Calendar.HOUR_OF_DAY, 12)
                    this.timeInMillis
                },
            ) { selectedTimeInMillis ->
                binding.saleDateEditText.setText(formatTimeStamp(selectedTimeInMillis))
                sharedViewModel.sharedDetail.saleTimeStamp = selectedTimeInMillis
            }
        }
    }

    private fun createOrUpdateProperty() {
        showProgressBar()
        CoroutineScope(IO).launch {
            val editMode = arguments?.let { AddDetailFragmentArgs.fromBundle(it).editMode } == true
            launch {
                if (editMode) updateProperty() else insertProperty()
            }.run { join() }
            withContext(Main) {
                makeSnackBar(
                    binding.root,
                    if (editMode) getString(R.string.property_updated) else
                        getString(R.string.new_property_created),
                    requireContext()
                )
                sharedViewModel.resetNewPropertyData()
                navigateNext()
            }
        }
    }

    private fun showProgressBar() {
        binding.previousButton.isEnabled = false
        binding.createOrUpdatePropertyButton.apply {
            alpha = 1f
            isEnabled = false
            animate()
                .alpha(0f)
                .setDuration(100)
                .setListener(null)
        }
        binding.progressBar.apply {
            alpha = 0f
            visibility = View.VISIBLE
            animate()
                .alpha(1f)
                .setDuration(100)
                .setListener(null)
        }
    }

    private fun insertProperty() {
        viewModel.insertAddress(sharedViewModel.sharedAddress)
        viewModel.insertDetail(sharedViewModel.sharedDetail)
        storePhotoList(sharedViewModel.sharedPhotoList, sharedViewModel.sharedDetail.detailId).run {
            for (photo in this) {
                viewModel.insertPhoto(photo)
            }
        }
        for (pointOfInterest in sharedViewModel.sharedPointOfInterestList) {
            viewModel.insertPointOfInterest(pointOfInterest)
        }
    }

    private fun updateProperty() {
        viewModel.updateAddress(sharedViewModel.sharedAddress)
        viewModel.updateDetail(sharedViewModel.sharedDetail)
        sharedViewModel.liveProperty.value?.photoList?.let { photoList ->
            for (photo in photoList) {
                Uri.parse(photo.uri).path?.let { path ->
                    File(path).run { if (this.exists()) this.delete() }
                }
                viewModel.deletePhoto(photo)
            }
        }
        storePhotoList(sharedViewModel.sharedPhotoList, sharedViewModel.sharedDetail.detailId)
            .run photoList@{
                for (photo in this@photoList) {
                    viewModel.insertPhoto(photo)
                }
            }
        sharedViewModel.liveProperty.value?.pointOfInterestList?.let { pointOfInterestList ->
            for (pointOfInterest in pointOfInterestList) {
                viewModel.deletePointOfInterest(pointOfInterest)
            }
        }
        for (pointOfInterest in sharedViewModel.sharedPointOfInterestList) {
            viewModel.insertPointOfInterest(pointOfInterest)
        }
    }

    private fun storePhotoList(
        photoList: List<Pair<Bitmap, String>>,
        detailId: String
    ): List<Photo> {
        return mutableListOf<Photo>().apply {
            for (pair in photoList) {
                val imageFile = storeBitmap(
                    pair.first,
                    context?.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE)
                )
                this.add(
                    Photo(
                        detailId = detailId,
                        uri = imageFile.toUri().toString(),
                        title = pair.second
                    )
                )
            }
        }
    }

    private fun navigateNext() {
        navController.navigate(
            AddDetailFragmentDirections.actionAddDetailFragmentToPropertyListFragment()
        )
    }
}