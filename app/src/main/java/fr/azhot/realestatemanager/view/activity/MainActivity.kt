package fr.azhot.realestatemanager.view.activity

import android.Manifest
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.gms.common.ConnectionResult
import com.google.android.gms.common.GoogleApiAvailability
import com.google.android.material.navigation.NavigationView
import fr.azhot.realestatemanager.NavGraphDirections
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.utils.RC_GOOGLE_SERVICES_DIALOG
import fr.azhot.realestatemanager.utils.RC_LOCATION_PERMISSIONS
import fr.azhot.realestatemanager.utils.checkAndRequestPermissions
import fr.azhot.realestatemanager.utils.checkPermissionsGranted
import fr.azhot.realestatemanager.view.fragment.PropertyDetailFragmentDirections
import fr.azhot.realestatemanager.view.fragment.PropertyListFragmentDirections
import fr.azhot.realestatemanager.viewmodel.SharedViewModel


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    NavigationView.OnNavigationItemSelectedListener {


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
        setSupportActionBar(binding.toolbar)
        navController.addOnDestinationChangedListener(this)
        binding.navView.setNavigationItemSelectedListener(this)

        // todo : toggle should change to a back arrow on all fragments but ListViewFragment
        // todo : loan simulator
        // todo : add static map
        // todo : integration test for network verification
        // todo : content provider

        val toggle = ActionBarDrawerToggle( // todo : in a method
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
    }

    override fun onBackPressed() {
        if (binding.drawerLayout.isDrawerOpen(GravityCompat.START)) {
            binding.drawerLayout.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.let {
            it.clear()
            this.menu = it
            menuInflater.inflate(
                when (navController.currentDestination?.id) {
                    R.id.propertyListFragment -> R.menu.property_list_menu
                    R.id.propertyDetailFragment -> R.menu.property_detail_menu
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
        val toolbarTitle = binding.toolbar.getChildAt(0) as TextView
        when (destination.id) {
            R.id.propertyListFragment -> {
                toolbarTitle.typeface =
                    ResourcesCompat.getFont(this, R.font.robotocondensed_regular)
                binding.bottomNavigation?.visibility = VISIBLE
            }
            R.id.propertyDetailFragment, R.id.searchModalFragment -> {
                toolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.robotocondensed_light)
                binding.bottomNavigation?.visibility = VISIBLE
            }
            else -> {
                toolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.robotocondensed_light)
                binding.bottomNavigation?.visibility = GONE
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.map_view_fragment -> navigateToMap()
        }
        onBackPressed()
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (checkPermissionsGranted(
                RC_LOCATION_PERMISSIONS,
                requestCode,
                grantResults
            )
        ) navigateToMap()
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    // functions
    private fun initVariables() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainContainerView.id) as NavHostFragment
        navController = navHostFragment.findNavController()
        binding.bottomNavigation?.let { NavigationUI.setupWithNavController(it, navController) }
        binding.bottomNavigation?.menu?.findItem(R.id.propertyDetailFragment)?.isVisible = false
    }

    private fun observeLiveProperty() {
        sharedViewModel.liveProperty.observe(this) { property ->
            if (property != null) {
                binding.bottomNavigation?.menu?.findItem(R.id.propertyDetailFragment)?.isVisible =
                    true
                if (resources.getBoolean(R.bool.isLandscape) && this::menu.isInitialized) {
                    menu.findItem(R.id.edit_property)?.isVisible = true
                }
            }
        }
    }

    private fun startAddNewProperty() {
        when (navController.currentDestination?.id) {
            R.id.propertyListFragment -> PropertyListFragmentDirections.actionPropertyListFragmentToAddPhotoFragment()
            R.id.propertyDetailFragment -> PropertyDetailFragmentDirections.actionPropertyDetailFragmentToAddPhotoFragment()
            else -> null
        }?.let { navController.navigate(it) }
    }

    private fun navigateToMap() {
        if (!isGoogleServicesOK()) {
            return
        }
        // todo : check user connection available or else snackBar user and return
        if (checkAndRequestPermissions(
                this,
                RC_LOCATION_PERMISSIONS,
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                )
            )
        ) {
            navController.navigate(NavGraphDirections.navigateToMapFragment())
        }
    }

    private fun isGoogleServicesOK(): Boolean {
        GoogleApiAvailability.getInstance().isGooglePlayServicesAvailable(this).run {
            when {
                this == ConnectionResult.SUCCESS -> {
                    return true
                }
                GoogleApiAvailability.getInstance().isUserResolvableError(this) -> {
                    GoogleApiAvailability.getInstance()
                        .getErrorDialog(this@MainActivity, this, RC_GOOGLE_SERVICES_DIALOG)
                        .show()
                    return false
                }
                else -> {
                    Toast.makeText(
                        this@MainActivity,
                        getString(R.string.cannot_make_map_requests),
                        Toast.LENGTH_SHORT
                    ).show()
                    return false
                }
            }
        }
    }
}