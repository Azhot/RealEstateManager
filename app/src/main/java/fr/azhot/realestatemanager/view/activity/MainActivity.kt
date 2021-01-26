package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.TextView
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.navigation.NavigationView
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.viewmodel.SharedViewModel


class MainActivity : AppCompatActivity(), NavController.OnDestinationChangedListener,
    NavigationView.OnNavigationItemSelectedListener,
    BottomNavigationView.OnNavigationItemSelectedListener {


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
        setUpNavigationUI()
        observeLiveProperty()
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)
        setUpDrawerToggle()
        navController.addOnDestinationChangedListener(this)
        binding.navView.setNavigationItemSelectedListener(this)

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
                binding.bottomNavigation.visibility = VISIBLE
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
                binding.bottomNavigation.visibility = when (destination.id) {
                    R.id.propertyDetailFragment, R.id.mapFragment, R.id.searchModalFragment -> VISIBLE
                    else -> GONE
                }
            }
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
        }
        // todo : onBackPressed() to be applied to drawer nav items
        return true
    }


    // functions
    private fun initVariables() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        val navHostFragment =
            supportFragmentManager.findFragmentById(binding.mainContainerView.id) as NavHostFragment
        navController = navHostFragment.findNavController()
    }

    private fun setUpNavigationUI() {
        NavigationUI.setupWithNavController(binding.bottomNavigation, navController)
        binding.bottomNavigation.menu.findItem(R.id.propertyDetailFragment).isVisible =
            false
    }

    private fun observeLiveProperty() {
        sharedViewModel.liveProperty.observe(this) { property ->
            if (property != null) {
                binding.bottomNavigation.menu.findItem(R.id.propertyDetailFragment)?.isVisible =
                    !resources.getBoolean(R.bool.isLandscape)
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
}