package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.ActivityMainBinding


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

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> navController.navigateUp()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestinationChanged(
        controller: NavController,
        destination: NavDestination,
        arguments: Bundle?
    ) {
        supportActionBar?.title = destination.label
        when (destination.id) {
            R.id.propertyListFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(false)
            R.id.propertyDetailsFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
            R.id.addPhotoFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
            R.id.addAddressFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
            R.id.addDetailFragment -> supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
    }
}