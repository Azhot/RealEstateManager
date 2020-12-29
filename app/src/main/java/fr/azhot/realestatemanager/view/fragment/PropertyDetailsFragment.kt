package fr.azhot.realestatemanager.view.fragment

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.FragmentPropertyDetailsBinding
import fr.azhot.realestatemanager.model.Photo
import fr.azhot.realestatemanager.utils.PHOTO_EXTRA
import fr.azhot.realestatemanager.view.activity.OpenPhotoActivity
import fr.azhot.realestatemanager.view.adapter.MediaListAdapter
import fr.azhot.realestatemanager.viewmodel.SharedViewModel
import kotlin.properties.Delegates


class PropertyDetailsFragment : Fragment(), MediaListAdapter.OnPhotoClickListener {


    // variables
    private lateinit var binding: FragmentPropertyDetailsBinding
    private lateinit var navController: NavController
    private var isLandscapeMode by Delegates.notNull<Boolean>()
    private val sharedViewModel: SharedViewModel by activityViewModels()


    // overridden functions
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        (activity as AppCompatActivity).supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = getString(R.string.property_detail)
        }
        setHasOptionsMenu(true)
        binding = FragmentPropertyDetailsBinding.inflate(layoutInflater)
        binding.mediaRecyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = MediaListAdapter(mutableListOf(), this@PropertyDetailsFragment)
        }
        sharedViewModel.liveProperty.observe(viewLifecycleOwner) { property ->
            (binding.mediaRecyclerView.adapter as MediaListAdapter).photoList = property.photoList
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        navController = Navigation.findNavController(requireActivity(), R.id.main_container_view)
    }

    override fun onResume() {
        super.onResume()
        isLandscapeMode = activity?.resources?.getBoolean(R.bool.isLandscape) == true
        if (isLandscapeMode) {
            navController.navigateUp()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.add_property -> {
                val action =
                    PropertyDetailsFragmentDirections.actionPropertyDetailsFragmentToAddPhotoFragment()
                navController.navigate(action)
            }
        }
        return true
    }

    override fun onPhotoClick(photo: Photo) {
        startActivity(Intent(context, OpenPhotoActivity::class.java).apply {
            putExtra(PHOTO_EXTRA, photo)
        })
        activity?.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }
}