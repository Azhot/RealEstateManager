package fr.azhot.realestatemanager.view.fragment

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity.RESULT_OK
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.FragmentAddPhotoBinding
import fr.azhot.realestatemanager.model.Photo
import fr.azhot.realestatemanager.utils.*
import fr.azhot.realestatemanager.view.adapter.MediaListAdapter
import fr.azhot.realestatemanager.viewmodel.SharedViewModel


class AddPhotoFragment : Fragment(), View.OnClickListener {

    // variables
    private lateinit var binding: FragmentAddPhotoBinding
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // todo : prompt also camera intent when clicking select photo

    // todo : optional : make a specific adapter with a delete button attached to each photo
    // todo : remember to [checkEnableNextButton] is user deletes all photos (if (photoList.size == 0))

    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddPhotoBinding.inflate(inflater)
        binding.photoTitleEditText.doAfterTextChanged { checkEnableAddButton() }
        binding.selectPhotoEditText.setOnClickListener(this)
        binding.addPhotoButton.setOnClickListener(this)
        configPhotoListRecyclerView()
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == RC_SELECTED_PHOTO
            && resultCode == RESULT_OK
            && data != null
            && data.data != null
        ) {
            binding.selectPhotoEditText.setText(data.data.toString())
            checkEnableAddButton()
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.selectPhotoEditText.id -> {
                if (checkAndRequestPermission(
                        this,
                        RC_READ_EXTERNAL_STORAGE_PERMISSION,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                ) {
                    selectPhoto()
                }
            }
            binding.addPhotoButton.id -> addPhoto()
            binding.nextButton.id -> navigateNext()
        }
    }


    // private functions
    private fun configPhotoListRecyclerView() {
        binding.mediaRecyclerView.layoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.HORIZONTAL,
            false
        )
        val adapter = MediaListAdapter(Glide.with(this), mutableListOf())
        binding.mediaRecyclerView.adapter = adapter
    }

    private fun selectPhoto() {
        val intent = Intent().apply {
            type = PICK_IMAGE_MIME
            action = Intent.ACTION_GET_CONTENT
        }
        startActivityForResult(
            Intent.createChooser(intent, getString(R.string.select_photo)), RC_SELECTED_PHOTO
        )
    }

    private fun checkEnableAddButton() {
        binding.addPhotoButton.isEnabled = binding.selectPhotoEditText.text.toString().isNotEmpty()
                && binding.photoTitleEditText.text.toString().isNotEmpty()
        if (binding.addPhotoButton.isEnabled) {
            binding.addPhotoButton
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.primaryDayColor))
            binding.addPhotoButton.setIconTintResource(R.color.primaryDayColor)

        } else {
            binding.addPhotoButton
                .setTextColor(ContextCompat.getColor(requireContext(), R.color.gray))
            binding.addPhotoButton.setIconTintResource(R.color.gray)
        }
    }

    private fun addPhoto() {
        val photo = Photo(
            uri = binding.selectPhotoEditText.text.toString(),
            title = binding.photoTitleEditText.text.toString()
        )
        (binding.mediaRecyclerView.adapter as MediaListAdapter).addPhoto(photo)
        binding.selectPhotoEditText.text?.clear()
        binding.photoTitleEditText.text?.clear()
        checkEnableAddButton()
        checkEnableNextButton()
    }

    private fun checkEnableNextButton() {
        binding.nextButton.isEnabled = binding.mediaRecyclerView.adapter?.itemCount != 0
        if (binding.nextButton.isEnabled) {
            binding.nextButton.visibility = VISIBLE
        } else {
            binding.nextButton.visibility = INVISIBLE
        }
    }

    private fun navigateNext() {
        sharedViewModel.mutablePhotoList.value =
            (binding.mediaRecyclerView.adapter as MediaListAdapter).photoList
        val action = AddPhotoFragmentDirections.actionAddPhotoFragmentToAddAddressFragment()
        navController.navigate(action)
    }
}