package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragmentDirections
import fr.azhot.realestatemanager.view.fragment.PropertyListFragmentDirections
import fr.azhot.realestatemanager.viewmodel.SharedViewModel


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {


    // variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var menu: Menu
    private lateinit var navController: NavController
    private val sharedViewModel: SharedViewModel by viewModels()


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariables()
        observeLiveProperty()
        setContentView(binding.root)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            it.clear()
            this.menu = it
            menuInflater.inflate(
                when (navController.currentDestination?.id) {
                    R.id.propertyListFragment -> R.menu.menu_property_list
                    R.id.propertyDetailsFragment -> R.menu.menu_property_details
                    else -> return false
                },
                this.menu
            )
            if (resources.getBoolean(R.bool.isLandscape) && sharedViewModel.liveProperty.value == null) {
                this.menu.findItem(R.id.edit_property)?.isVisible = false
            }
            return true
        }
        return false
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
            R.id.add_property -> startAddNewProperty()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        invalidateOptionsMenu()
        supportActionBar?.title = destination.label
        when (destination.id) {
            R.id.propertyListFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(false)
            R.id.propertyDetailsFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
            R.id.addPhotoFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
            R.id.addAddressFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
            R.id.addDetailFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }


    // functions
    private fun initVariables() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainContainerView.id) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener(this)
    }

    private fun observeLiveProperty() {
        sharedViewModel.liveProperty.observe(this) { property ->
            if (resources.getBoolean(R.bool.isLandscape) && property != null && this::menu.isInitialized) {
                menu.findItem(R.id.edit_property)?.isVisible = true
            }
        }
    }

    private fun startAddNewProperty() {
        when (navController.currentDestination?.id) {
            R.id.propertyListFragment -> PropertyListFragmentDirections.actionPropertyListFragmentToAddPhotoFragment()
            R.id.propertyDetailsFragment -> PropertyDetailsFragmentDirections.actionPropertyDetailsFragmentToAddPhotoFragment()
            else -> null
        }?.let { navController.navigate(it) }
    }
}