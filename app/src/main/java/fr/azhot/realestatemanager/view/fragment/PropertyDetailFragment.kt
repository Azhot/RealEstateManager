package fr.azhot.realestatemanager.view.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.FragmentPropertyDetailBinding
import fr.azhot.realestatemanager.model.Photo
import fr.azhot.realestatemanager.model.PointOfInterest
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.utils.*
import fr.azhot.realestatemanager.view.activity.OpenPhotoActivity
import fr.azhot.realestatemanager.view.adapter.PhotoListAdapter
import fr.azhot.realestatemanager.view.adapter.PointOfInterestListAdapter
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import java.text.NumberFormat
import java.util.*


class PropertyDetailFragment : Fragment(), PhotoListAdapter.OnPhotoClickListener {


    // variables
    private lateinit var binding: FragmentPropertyDetailBinding
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPropertyDetailBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.main_container_view)
        setHasOptionsMenu(true)
        buildPhotoRecyclerView()
        buildPointOfInterestRecyclerView()
        observeLiveProperty()
    }

    override fun onResume() {
        super.onResume()
        if (activity?.resources?.getBoolean(R.bool.isLandscape) == true) {
            navController.navigateUp()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.edit_property -> navigateEditProperty()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onPhotoClick(photo: Photo) {
        startOpenPhotoActivity(photo)
    }


    // functions
    private fun buildPhotoRecyclerView() {
        binding.photoRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = PhotoListAdapter(mutableListOf(), this@PropertyDetailFragment)
        }
    }

    private fun buildPointOfInterestRecyclerView() {
        binding.pointOfInterestRecyclerView.apply {
            val layoutManager = object : LinearLayoutManager(context, VERTICAL, false) {
                override fun canScrollVertically(): Boolean = false
            }
            this.layoutManager = layoutManager
            adapter = PointOfInterestListAdapter(mutableListOf())
        }
    }

    private fun observeLiveProperty() {
        sharedViewModel.liveProperty.observe(viewLifecycleOwner) { property ->
            setUpWidgets(property)
        }
    }

    private fun setUpWidgets(property: Property) {
        if (property.detail.saleTimeStamp != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.mainConstraintLayout.foreground = ContextCompat.getDrawable(
                    requireContext(),
                    R.drawable.ic_transparent_foreground
                )
            }
            binding.soldImageView.visibility = VISIBLE
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                binding.mainConstraintLayout.foreground = null
            }
            binding.soldImageView.visibility = GONE
        }

        (binding.photoRecyclerView.adapter as PhotoListAdapter).photoList = property.photoList
        binding.descriptionTextView.text = property.detail.description
            ?: getString(R.string.not_provided)
        loadNumberTextView(property.detail.price, binding.priceTextView, null)
        binding.propertyTypeTextView.text = property.detail.propertyType?.toString()
            ?: getString(R.string.not_provided)
        loadNumberTextView(property.detail.squareMeters, binding.squareMeterTextView, "m²")
        loadNumberTextView(property.detail.rooms, binding.roomsTextView, null)
        binding.addressTextView.text = if (property.address.toString().isNotEmpty())
            property.address.toString() else getString(R.string.not_provided)
        loadTimeStampTextView(property.detail.entryTimeStamp, binding.entryDateTextView)
        loadTimeStampTextView(property.detail.saleTimeStamp, binding.saleDateTextView)
        binding.realtorTextView.text = property.realtor?.toString()
            ?: getString(R.string.not_provided)
        loadPointOfInterestList(property)
    }

    private fun loadNumberTextView(number: Int?, textView: TextView, suffix: String?) {
        textView.text = if (number == null) {
            getString(R.string.not_provided)
        } else {
            NumberFormat.getInstance(Locale.US).run {
                maximumFractionDigits = 0
                format(number)
            } + if (suffix != null) " $suffix" else ""
        }
    }

    private fun loadTimeStampTextView(timeStamp: Long?, textView: TextView) {
        textView.text = if (timeStamp == null) {
            getString(R.string.not_provided)
        } else {
            formatTimeStamp(timeStamp)
        }
    }

    private fun loadPointOfInterestList(property: Property) {
        (binding.pointOfInterestRecyclerView.adapter as PointOfInterestListAdapter).pointOfInterestList =
            if (property.pointOfInterestList.isNotEmpty()) {
                property.pointOfInterestList
            } else {
                mutableListOf(
                    PointOfInterest(
                        pointOfInterestId = "",
                        detailId = "",
                    )
                )
            }
    }

    private fun startOpenPhotoActivity(photo: Photo) {
        startActivity(Intent(context, OpenPhotoActivity::class.java).apply {
            putExtra(PHOTO_EXTRA, photo)
        })
        activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun navigateEditProperty() {
        if (!resources.getBoolean(R.bool.isLandscape)) {
            navController.navigate(
                PropertyDetailFragmentDirections.actionPropertyDetailFragmentToAddPhotoFragment(true)
            )
        }
    }
}