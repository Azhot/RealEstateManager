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
import androidx.drawerlayout.widget.DrawerLayout
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
    private lateinit var toggle: ActionBarDrawerToggle
    private val sharedViewModel: SharedViewModel by viewModels()


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initVariables()
        observeLiveProperty()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpDrawerToggle()
        navController.addOnDestinationChangedListener(this)
        binding.navView.setNavigationItemSelectedListener(this)
        // todo : bottom nav should be list view and map view
        // todo : remove map from nav drawer
        // todo : content provider
        // todo : loan simulator
        // todo : add static map
        // todo : integration test for network verification
        // todo : content provider
    }

    override fun onBackPressed() {
        if (binding.root.isDrawerOpen(GravityCompat.START)) {
            binding.root.closeDrawer(GravityCompat.START)
            return
        }
        super.onBackPressed()
    }

    override fun onCreateOptionsMenu(m: Menu?): Boolean {
        m?.let { menu ->
            menu.clear()
            this.menu = menu
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
                binding.root.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED)
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                toggle.apply {
                    isDrawerIndicatorEnabled = true
                    toolbarNavigationClickListener = null
                }
                binding.bottomNavigation?.visibility = VISIBLE
            }
            else -> {
                toolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.robotocondensed_light)
                binding.root.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED)
                toggle.apply {
                    isDrawerIndicatorEnabled = false
                    setToolbarNavigationClickListener { onBackPressed() }
                }
                supportActionBar?.apply {
                    setDisplayHomeAsUpEnabled(true)
                    setHomeButtonEnabled(true)
                }
                binding.bottomNavigation?.visibility = when (destination.id) {
                    R.id.propertyDetailFragment, R.id.searchModalFragment -> VISIBLE
                    else -> GONE
                }
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
        binding.bottomNavigation?.let { botNavView ->
            NavigationUI.setupWithNavController(botNavView, navController)
            botNavView.menu.findItem(R.id.propertyDetailFragment).isVisible =
                false // todo : to be removed
        }
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

    private fun setUpDrawerToggle() {
        toggle = ActionBarDrawerToggle(
            this,
            binding.root,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        )
        binding.root.addDrawerListener(toggle)
        toggle.syncState()
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