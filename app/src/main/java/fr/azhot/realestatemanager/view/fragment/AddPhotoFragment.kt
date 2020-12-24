package fr.azhot.realestatemanager.view.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
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
import fr.azhot.realestatemanager.utils.*
import fr.azhot.realestatemanager.view.adapter.AddPhotoListAdapter
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import java.io.File


class AddPhotoFragment : Fragment(), View.OnClickListener,
    AddPhotoListAdapter.OnDeletePhotoListener {

    // variables
    private lateinit var binding: FragmentAddPhotoBinding
    private lateinit var navController: NavController
    private lateinit var fromCameraFile: File
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPhotoBinding.inflate(inflater)
        binding.photoTitleEditText.doAfterTextChanged { checkEnableAddButton() }
        binding.selectPhotoButton.setOnClickListener(this)
        binding.addPhotoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = AddPhotoListAdapter(Glide.with(this), mutableMapOf(), this@AddPhotoFragment)
        }
        observePhotoList()
        binding.nextButton.setOnClickListener(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(view)
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
        if (checkPermission(
                RC_READ_EXTERNAL_STORAGE_PERMISSION,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                requestCode,
                permissions,
                grantResults
            )
        ) {
            selectPhoto()
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // todo : resultCode = 0
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                RC_SELECT_PHOTO -> {
                    val uri =
                        if (data != null && data.data != null) data.data!! else fromCameraFile.toUri()
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

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.selectPhotoButton.id -> {
                if (checkAndRequestPermission(
                        this,
                        RC_READ_EXTERNAL_STORAGE_PERMISSION,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    selectPhoto()
                }
            }
            binding.nextButton.id -> navigateNext()
        }
    }

    override fun onDeletePhoto(bitmap: Bitmap) {
        (binding.addPhotoRecyclerView.adapter as AddPhotoListAdapter).apply {
            bitmapStringMutableMap.remove(bitmap)
            notifyDataSetChanged()
        }
        checkEnableNextButton()
    }


    // private functions
    private fun observePhotoList() {
        sharedViewModel.livePhotoMap.observe(viewLifecycleOwner) {
            (binding.addPhotoRecyclerView.adapter as AddPhotoListAdapter).bitmapStringMutableMap =
                it
            checkEnableNextButton()
        }
    }

    private fun selectPhoto() {
        // todo : question to Virgile : camera will crash on a Kitkat running device...
        fromCameraFile =
            createImageFile(requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES))
        val fileProvider =
            FileProvider.getUriForFile(requireContext(), FILE_PROVIDER_AUTHORITY, fromCameraFile)
        val fromCamera = Intent(MediaStore.ACTION_IMAGE_CAPTURE).apply {
            putExtra(MediaStore.EXTRA_OUTPUT, fileProvider)
        }
        val fromGallery =
            Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI).apply {
                type = PICK_IMAGE_MIME
            }
        val chooser = Intent.createChooser(fromGallery, getString(R.string.select_photo)).apply {
            putExtra(Intent.EXTRA_INITIAL_INTENTS, arrayOf(fromCamera))
        }

        if (requireActivity().packageManager.hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            startActivityForResult(chooser, RC_SELECT_PHOTO)
        } else {
            startActivityForResult(fromGallery, RC_SELECT_PHOTO)
        }
    }

    private fun checkEnableAddButton() {
        binding.selectPhotoButton.isEnabled =
            binding.photoTitleEditText.text.toString().isNotEmpty()
        val color =
            if (binding.selectPhotoButton.isEnabled) R.color.primaryDayColor else R.color.gray
        binding.selectPhotoButton.setTextColor(ContextCompat.getColor(requireContext(), color))
        binding.selectPhotoButton.setIconTintResource(color)
    }

    private fun addPhoto(bitmap: Bitmap) {
        if (fromCameraFile.exists()) fromCameraFile.delete()
        sharedViewModel.livePhotoMap += (bitmap to binding.photoTitleEditText.text.toString())
        binding.photoTitleEditText.text?.clear()
        checkEnableNextButton()
        checkEnableAddButton()
    }

    private fun checkEnableNextButton() {
        binding.nextButton.isEnabled = binding.addPhotoRecyclerView.adapter?.itemCount != 0
        binding.nextButton.visibility = if (binding.nextButton.isEnabled) VISIBLE else INVISIBLE
    }

    private fun navigateNext() {
        val action = AddPhotoFragmentDirections.actionAddPhotoFragmentToAddAddressFragment()
        navController.navigate(action)
    }
}