package fr.azhot.realestatemanager.view.fragment

import android.Manifest
import android.content.Intent
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.Intent.FLAG_GRANT_WRITE_URI_PERMISSION
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.net.toUri
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.FragmentAddPhotoBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.utils.*
import fr.azhot.realestatemanager.view.adapter.AddPhotoListAdapter
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import java.io.File


class AddPhotoFragment : Fragment(), View.OnClickListener,
    AddPhotoListAdapter.OnDeletePhotoListener {

    // variables
    private lateinit var binding: FragmentAddPhotoBinding
    private lateinit var navController: NavController
    private lateinit var cameraFile: File
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPhotoBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
        setUpAddPhotoRecyclerView()
        setUpListeners()
        if (arguments?.let { AddPhotoFragmentArgs.fromBundle(it).editMode } == true) {
            sharedViewModel.liveProperty.value?.let { loadDataIfEditMode(it) }
        }
        setUpOnBackPressed()
    }

    override fun onResume() {
        super.onResume()
        checkEnableAddButton()
        checkEnableNextButton()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        selectPhoto()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RC_SELECT_PHOTO -> {
                    (if (data?.data != null) data.data else cameraFile.toUri())?.let { uri ->
                        createBitmapWithGlide(
                            Glide.with(requireContext()),
                            PHOTO_WIDTH,
                            PHOTO_HEIGHT,
                            uri,
                            ::addPhoto
                        )
                    }
                }
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.selectPhotoButton.id -> selectPhoto()
            binding.nextButton.id -> navigateNext()
            binding.previousButton.id -> activity?.onBackPressed()
        }
    }

    override fun onDeletePhoto(pair: Pair<Bitmap, String>) {
        sharedViewModel.sharedPhotoList.indexOf(pair).run {
            try {
                sharedViewModel.sharedPhotoList.removeAt(this)
                (binding.addPhotoRecyclerView.adapter as AddPhotoListAdapter).notifyItemRemoved(this)
            } catch (e: IndexOutOfBoundsException) {
            }
        }
        checkEnableNextButton()
    }


    // private functions
    private fun setUpAddPhotoRecyclerView() {
        binding.addPhotoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = AddPhotoListAdapter(sharedViewModel.sharedPhotoList, this@AddPhotoFragment)
        }
    }

    private fun setUpListeners() {
        binding.photoTitleEditText.doAfterTextChanged { checkEnableAddButton() }
        binding.selectPhotoButton.setOnClickListener(this)
        binding.previousButton.setOnClickListener(this)
        binding.nextButton.setOnClickListener(this)
    }

    private fun loadDataIfEditMode(property: Property) {
        if (sharedViewModel.sharedDetail.detailId == property.detail.detailId) return
        sharedViewModel.sharedAddress = property.address.copy()
        sharedViewModel.sharedDetail = property.detail.copy()
        sharedViewModel.sharedPhotoList.clear()
        for (photo in property.photoList) {
            createBitmapWithGlide(
                Glide.with(this),
                PHOTO_WIDTH,
                PHOTO_HEIGHT,
                Uri.parse(photo.uri)
            ) { bitmap ->
                sharedViewModel.sharedPhotoList.add(bitmap to photo.title)
                (binding.addPhotoRecyclerView.adapter as AddPhotoListAdapter).notifyItemInserted(
                    sharedViewModel.sharedPhotoList.size
                )
                checkEnableNextButton()
            }
        }
        sharedViewModel.sharedPointOfInterestList.clear()
        sharedViewModel.sharedPointOfInterestList.addAll(property.pointOfInterestList)
    }

    private fun checkEnableAddButton() {
        binding.selectPhotoButton.apply {
            isEnabled = binding.photoTitleEditText.text?.isNotEmpty() == true
            val color = if (isEnabled) R.color.primaryDayColor else R.color.gray
            setTextColor(ContextCompat.getColor(requireContext(), color))
            setIconTintResource(color)
        }
    }

    private fun selectPhoto() {
        if (!checkAndRequestPermission(
                this,
                RC_READ_EXTERNAL_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            return
        }

        val fromGallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = PICK_IMAGE_MIME
            }

        if (requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            cameraFile =
                createImageFile(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))

            val uri =
                if (Build.VERSION_CODES.N <= Build.VERSION.SDK_INT) FileProvider.getUriForFile(
                    requireContext(),
                    FILE_PROVIDER_AUTHORITY,
                    cameraFile
                ) else Uri.fromFile(cameraFile)

            val fromCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
                putExtra(MediaStore.EXTRA_OUTPUT, uri)
                addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(FLAG_GRANT_WRITE_URI_PERMISSION)
            }

            val chooser = Intent.createChooser(fromGallery, getString(R.string.select_photo))
                .apply { putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(fromCamera)) }

            startActivityForResult(chooser, RC_SELECT_PHOTO)

        } else {
            startActivityForResult(fromGallery, RC_SELECT_PHOTO)
        }
    }

    private fun addPhoto(bitmap: Bitmap) {
        cameraFile.delete()
        cameraFile = createImageFile(context?.getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        sharedViewModel.sharedPhotoList.add(bitmap to binding.photoTitleEditText.text.toString())
        (binding.addPhotoRecyclerView.adapter as AddPhotoListAdapter).notifyItemInserted(
            sharedViewModel.sharedPhotoList.size
        )
        binding.photoTitleEditText.text?.clear()
        checkEnableAddButton()
        checkEnableNextButton()
    }

    private fun checkEnableNextButton() {
        binding.nextButton.apply {
            isEnabled = binding.addPhotoRecyclerView.adapter?.itemCount != 0
            visibility = if (binding.nextButton.isEnabled) VISIBLE else INVISIBLE
        }
    }

    private fun navigateNext() {
        navController.navigate(
            AddPhotoFragmentDirections.actionAddPhotoFragmentToAddAddressFragment(
                arguments?.let { AddPhotoFragmentArgs.fromBundle(it).editMode } == true
            )
        )
    }

    private fun setUpOnBackPressed() {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (arguments?.let { AddPhotoFragmentArgs.fromBundle(it).editMode } == true) {
                    sharedViewModel.resetNewPropertyData()
                }
                navController.navigateUp()
            }
        }.run {
            requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, this)
        }
    }
}