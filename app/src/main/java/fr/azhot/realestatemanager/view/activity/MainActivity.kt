package fr.azhot.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import fr.azhot.realestatemanager.RealEstateManagerApplication
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragment
import fr.azhot.realestatemanager.view.fragment.PropertyListFragment
import fr.azhot.realestatemanager.viewmodel.MainActivityViewModel
import fr.azhot.realestatemanager.viewmodel.MainActivityViewModelFactory


class MainActivity : AppCompatActivity(), PropertyClickListener {


    // variables
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainActivityViewModel by viewModels {
        MainActivityViewModelFactory((application as RealEstateManagerApplication).detailRepository)
    }
    private lateinit var currentProperty: Property


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initActivityMainBinding(layoutInflater)

        initPropertyObserver()
        launchPropertyListFragment(binding.mainContainerView.id)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (binding.secondaryContainerView != null && this::currentProperty.isInitialized) {
            launchPropertyDetailsFragment(binding.secondaryContainerView!!.id)
        }
    }

    override fun onPropertyClickListener(property: Property) {
        viewModel.setLiveProperty(property)
        if (binding.secondaryContainerView != null) {
            launchPropertyDetailsFragment(binding.secondaryContainerView!!.id)
        } else {
            intent = Intent(this, PropertyDetailsActivity::class.java)
            startActivity(intent)
        }
    }


    // functions
    private fun initActivityMainBinding(layoutInflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)


    private fun initPropertyObserver() {
        viewModel.liveProperty.observe(this, { property ->
            currentProperty = property
        })
    }

    private fun launchPropertyListFragment(containerId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, PropertyListFragment.newInstance())
            .commit()
    }

    private fun launchPropertyDetailsFragment(containerId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, PropertyDetailsFragment.newInstance())
            .commit()
    }
}