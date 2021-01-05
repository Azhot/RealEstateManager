package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.util.TypedValue
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
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.NavigationUI
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.view.fragment.PropertyDetailFragmentDirections
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
        setSupportActionBar(binding.toolbar)
        navController.addOnDestinationChangedListener(this)

        // todo : various looks depending on whether the property is sold
        // todo : implement edit property
        // todo : search
        // todo : implement nav drawer
        // todo : add static map
        // todo : integration test for network verification
        // todo : content provider
        // todo : bottom add detail check nestedscrollview

        val toggle = ActionBarDrawerToggle(
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
        toolbarTitle.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17f)
        when (destination.id) {
            R.id.propertyListFragment -> {
                toolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.merienda_bold)
                binding.bottomNavigation?.visibility = VISIBLE
            }
            R.id.propertyDetailFragment -> {
                toolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.merienda_regular)
                binding.bottomNavigation?.visibility = VISIBLE
            }
            else -> {
                toolbarTitle.typeface = ResourcesCompat.getFont(this, R.font.merienda_regular)
                binding.bottomNavigation?.visibility = GONE
            }
        }
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
}