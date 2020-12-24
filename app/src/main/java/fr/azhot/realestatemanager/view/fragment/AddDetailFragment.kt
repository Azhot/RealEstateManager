package fr.azhot.realestatemanager.view.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
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
import fr.azhot.realestatemanager.utils.buildMaterialDatePicker
import fr.azhot.realestatemanager.utils.storeBitmap
import fr.azhot.realestatemanager.view.adapter.ExposedDropdownMenuAdapter
import fr.azhot.realestatemanager.view.adapter.PointOfInterestListAdapter
import fr.azhot.realestatemanager.viewmodel.AddDetailFragmentViewModel
import fr.azhot.realestatemanager.viewmodel.AddDetailFragmentViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.*

class AddDetailFragment : Fragment(), View.OnClickListener,
    PointOfInterestListAdapter.OnDeletePointOfInterestListener {

    // variables
    private lateinit var binding: FragmentAddDetailBinding
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by activityViewModels()
    private val viewModel: AddDetailFragmentViewModel by viewModels {
        val application = (activity?.application as RealEstateManagerApplication)
        AddDetailFragmentViewModelFactory(
            application.detailRepository,
            application.addressRepository,
            application.photoRepository,
            application.pointOfInterestRepository,
            application.realtorRepository,
        )
    }
    private lateinit var currentPropertyType: PropertyType
    private lateinit var currentRealtor: Realtor
    private val entryDate by lazy { Calendar.getInstance() }
    private val saleDate by lazy { Calendar.getInstance() }


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddDetailBinding.inflate(inflater)
        setUpWidgets()
        observeRealtorList()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.createRealtorImageButton.id -> buildAddRealtorDialog(::insertRealtor)

            binding.addPointOfInterestButton.id -> buildAddPointOfInterestDialog(::addPointOfInterest)

            binding.entryDateEditText.id -> buildMaterialDatePicker(
                childFragmentManager,
                entryDate,
                binding.entryDateEditText
            )

            binding.saleDateEditText.id -> buildMaterialDatePicker(
                childFragmentManager,
                saleDate,
                binding.saleDateEditText
            )

            binding.createPropertyButton.id -> {
                showProgressBar()
                CoroutineScope(Default).launch {
                    val job: Job = launch(Default) {
                        insertProperty()
                    }
                    job.join()
                    withContext(Main) {
                        val action =
                            AddDetailFragmentDirections.actionAddDetailFragmentToPropertyListFragment()
                        navController.navigate(action)
                    }
                }
            }
        }
    }

    override fun onDeletePointOfInterest(pointOfInterest: PointOfInterest) {
        (binding.pointOfInterestRecyclerView.adapter as PointOfInterestListAdapter).apply {
            pointOfInterestList.remove(pointOfInterest)
            notifyDataSetChanged()
        }
    }


    // private functions
    // todo : découper
    private fun setUpWidgets() {
        // property dropdown menu
        binding.propertyTypeAutoComplete.apply {
            val adapter = ExposedDropdownMenuAdapter(
                requireContext(),
                R.layout.exposed_dropdown_menu_item,
                mutableListOf()
            )
            adapter.addAll(PropertyType.toMutableList())
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                currentPropertyType = adapter.getItem(position) as PropertyType
            }
        }
        binding.addPointOfInterestButton.setOnClickListener(this)
        binding.pointOfInterestRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            adapter = PointOfInterestListAdapter(mutableListOf(), this@AddDetailFragment)
        }
        binding.entryDateEditText.setOnClickListener(this)
        binding.saleDateEditText.setOnClickListener(this)
        // realtor dropdown menu
        binding.realtorAutoComplete.apply {
            val adapter = ExposedDropdownMenuAdapter(
                requireContext(),
                R.layout.exposed_dropdown_menu_item,
                mutableListOf()
            )
            setAdapter(adapter)
            setOnItemClickListener { _, _, position, _ ->
                currentRealtor = adapter.getItem(position) as Realtor
            }
        }
        binding.createRealtorImageButton.setOnClickListener(this)
        binding.createPropertyButton.setOnClickListener(this)
    }

    private fun observeRealtorList() {
        viewModel.realtorList.observe(viewLifecycleOwner, { realtorList ->
            (binding.realtorAutoComplete.adapter as ExposedDropdownMenuAdapter).apply {
                this.list = realtorList.toMutableList()
            }
        })
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
        val builder = AlertDialog.Builder(context)
        val dialogBinding = DialogAddPointOfInterestBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        val dialog = builder.create()

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
            val zipCode = if (dialogBinding.zipCodeEditText.text.toString().isNotEmpty())
                dialogBinding.zipCodeEditText.text.toString().trim() else null
            val city = if (dialogBinding.cityEditText.text.toString().isNotEmpty())
                dialogBinding.cityEditText.text.toString().trim() else null
            val roadName = if (dialogBinding.roadNameEditText.text.toString().isNotEmpty())
                dialogBinding.roadNameEditText.text.toString().trim() else null
            val number = if (dialogBinding.numberEditText.text.toString().isNotEmpty())
                dialogBinding.numberEditText.text.toString().trim() else null
            val complement = if (dialogBinding.complementEditText.text.toString().isNotEmpty())
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
        val address = Address(
            zipCode = zipCode,
            city = city,
            roadName = roadName,
            number = number,
            complement = complement
        )
        val pointOfInterest = PointOfInterest(
            name = name,
            address = address
        )
        (binding.pointOfInterestRecyclerView.adapter as PointOfInterestListAdapter).apply {
            pointOfInterestList.add(pointOfInterest)
            notifyDataSetChanged()
        }
    }

    private fun buildAddRealtorDialog(functionOnClickAddButton: (firstName: String, lastName: String) -> (Unit)) {
        val builder = AlertDialog.Builder(context)
        val dialogBinding = DialogAddRealtorBinding.inflate(layoutInflater)
        builder.setView(dialogBinding.root)

        val dialog = builder.create()

        dialogBinding.firstNameEditText.doAfterTextChanged {
            dialogBinding.addButton.isEnabled =
                dialogBinding.firstNameEditText.text.toString().isNotEmpty()
                        && dialogBinding.lastNameEditText.text.toString().isNotEmpty()
            updateButtonColor(dialogBinding.addButton)
        }

        dialogBinding.lastNameEditText.doAfterTextChanged {
            dialogBinding.addButton.isEnabled =
                dialogBinding.firstNameEditText.text.toString().isNotEmpty()
                        && dialogBinding.lastNameEditText.text.toString().isNotEmpty()
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
        val realtor = Realtor(firstName = firstName, lastName = lastName)
        binding.realtorAutoComplete.apply {
            (adapter as ExposedDropdownMenuAdapter).apply {
                for (item in list) {
                    if (item.toString() == realtor.toString()) {
                        Snackbar
                            .make(
                                binding.root,
                                "${item.toString()} already exists.",
                                Snackbar.LENGTH_SHORT
                            ).setBackgroundTint(
                                ContextCompat.getColor(
                                    context,
                                    R.color.secondaryDayColor
                                )
                            )
                            .show()
                        return
                    }
                }
                setText(realtor.toString(), false)
            }
            viewModel.insertRealtor(realtor)
        }
    }

    private fun updateButtonColor(button: Button) {
        if (button.isEnabled) {
            button.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.primaryDayColor)
            )
        } else {
            button.setTextColor(
                ContextCompat.getColor(requireContext(), R.color.gray)
            )
        }
    }

    private fun showProgressBar() {
        binding.createPropertyButton.apply {
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
        // insert address to the database if exists
        val address = sharedViewModel.liveAddress.value
        address?.let { viewModel.insertAddress(it) }

        // construct detail and insert it to the database
        val detail = Detail().apply {
            // set propertyType
            if (this@AddDetailFragment::currentPropertyType.isInitialized)
                propertyType = currentPropertyType
            // set price
            if (binding.priceEditText.text.toString().isNotEmpty())
                price = Integer.valueOf(binding.priceEditText.text.toString())
            // set squareMeters
            if (binding.squareMeterEditText.text.toString().isNotEmpty())
                squareMeters = Integer.valueOf(binding.squareMeterEditText.text.toString())
            // set rooms
            if (binding.roomsEditText.text.toString().isNotEmpty())
                rooms = Integer.valueOf(binding.roomsEditText.text.toString())
            // set description
            if (binding.descriptionEditText.text.toString().isNotEmpty())
                description = binding.descriptionEditText.text.toString()
            // set addressId
            addressId = address?.addressId
            // set entryTimeStamp
            if (binding.entryDateEditText.text.toString().isNotEmpty())
                entryTimeStamp = entryDate.timeInMillis
            // set saleTimeStamp
            if (binding.saleDateEditText.text.toString().isNotEmpty())
                saleTimeStamp = saleDate.timeInMillis
            // set realtorId
            if (this@AddDetailFragment::currentRealtor.isInitialized)
                realtorId = currentRealtor.realtorId
        }
        viewModel.insertDetail(detail)

        // store and insert photos to the database
        sharedViewModel.livePhotoMap.value?.let { photoMap ->
            storePhotoMap(photoMap, detail.detailId).apply {
                for (photo in this) {
                    viewModel.insertPhoto(photo)
                }
            }
        }

        // insert points of interest to the database
        (binding.pointOfInterestRecyclerView.adapter as PointOfInterestListAdapter)
            .pointOfInterestList.apply {
                for (pointOfInterest in this) {
                    viewModel.insertPointOfInterest(pointOfInterest)
                }
            }
    }

    private fun storePhotoMap(photoMap: Map<Bitmap, String>, detailId: String): List<Photo> {
        return mutableListOf<Photo>().apply {
            for (entry in photoMap.entries) {
                val imageFile = storeBitmap(
                    entry.key,
                    context?.getDir(Environment.DIRECTORY_PICTURES, Context.MODE_PRIVATE)
                )
                this.add(
                    Photo(
                        detailId = detailId,
                        uri = imageFile.toUri().toString(),
                        title = entry.value
                    )
                )
            }
        }
    }
}