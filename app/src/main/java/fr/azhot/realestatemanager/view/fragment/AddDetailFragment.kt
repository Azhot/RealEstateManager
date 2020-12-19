package fr.azhot.realestatemanager.view.fragment

import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.FragmentAddDetailBinding
import fr.azhot.realestatemanager.model.Photo
import fr.azhot.realestatemanager.utils.PHOTO_DIRECTORY
import fr.azhot.realestatemanager.utils.PHOTO_HEIGHT
import fr.azhot.realestatemanager.utils.PHOTO_WIDTH
import fr.azhot.realestatemanager.utils.storeBitmap
import fr.azhot.realestatemanager.viewmodel.AddPropertyActivityViewModel
import fr.azhot.realestatemanager.viewmodel.AddPropertyActivityViewModelFactory
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.launch
import java.util.*

class AddDetailFragment : Fragment(), View.OnClickListener {

    // variables
    private lateinit var binding: FragmentAddDetailBinding
    private lateinit var navController: NavController
    private lateinit var photoList: List<Photo>

    // todo : make a specific viewmodel
    private val viewModel: AddPropertyActivityViewModel by viewModels {
        val application = (activity?.application as RealEstateManagerApplication)
        AddPropertyActivityViewModelFactory(
            application.detailRepository,
            application.addressRepository,
            application.photoRepository,
            application.pointOfInterestRepository,
            application.realtorRepository,
        )
    }

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
        // todo : add homeasup
        binding = FragmentAddDetailBinding.inflate(inflater)
        binding.createButton.setOnClickListener(this)
        return binding.root
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            binding.createButton.id -> {
                createProperty()
                // todo : navigate back to main
            }
        }
    }

    // private functions
    private fun createProperty() {

        CoroutineScope(Default).launch {
            storePhotoList(photoList)
        }

    }

    private fun observePhotoList() {
        sharedViewModel.mutableListPhoto.observe(viewLifecycleOwner) {
            photoList = it
        }
    }

    private fun storePhotoList(photoList: List<Photo>) {

        val requestOptions = RequestOptions()
            .fitCenter()
            .override(PHOTO_WIDTH, PHOTO_HEIGHT)

        for (photo in photoList) {
            Glide.with(requireContext())
                .asBitmap()
                .apply(requestOptions)
                .load(photo.uri)
                .into(object : CustomTarget<Bitmap>() {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        photo.uri = storeBitmap(
                            resource,
                            UUID.randomUUID().toString(),
                            PHOTO_DIRECTORY,
                            requireContext()
                        )
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {}
                })
        }
    }

}