package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragmentDirections
import fr.azhot.realestatemanager.view.fragment.PropertyListFragmentDirections


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener {


    // variables
    private lateinit var binding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainContainerView.id) as NavHostFragment
        navController = navHostFragment.findNavController()
        navController.addOnDestinationChangedListener(this)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.clear()
        menuInflater.inflate(
            when (navController.currentDestination?.id) {
                R.id.propertyListFragment -> R.menu.menu_property_list
                R.id.propertyDetailsFragment -> R.menu.menu_property_details
                else -> return false
            },
            menu
        )
        return true
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

    override fun onBackPressed() {
        invalidateOptionsMenu()
        super.onBackPressed()
    }


    // functions
    private fun startAddNewProperty() {
        when (navController.currentDestination?.id) {
            R.id.propertyListFragment -> PropertyListFragmentDirections.actionPropertyListFragmentToAddPhotoFragment()
            R.id.propertyDetailsFragment -> PropertyDetailsFragmentDirections.actionPropertyDetailsFragmentToAddPhotoFragment()
            else -> null
        }?.let { navController.navigate(it) }
    }
}