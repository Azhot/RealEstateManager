package fr.azhot.realestatemanager.view.activity

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragment
import fr.azhot.realestatemanager.view.fragment.PropertyListFragment
import fr.azhot.realestatemanager.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity(), PropertyClickListener {


    // companion
    companion object {
        //private val TAG = MainActivity::class.simpleName
    }


    // variables
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var mProperty: Property


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(layoutInflater)
        initPropertyObserver()
        launchPropertyListFragment(mBinding.mainContainerView.id)
        setContentView(mBinding.root)
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE
            && mBinding.secondaryContainerView != null
            && this::mProperty.isInitialized
        ) {
            launchPropertyDetailsFragment(mBinding.secondaryContainerView!!.id)
        }
    }

    override fun onPropertyClickListener(property: Property) {
        mViewModel.setCurrentProperty(property)
        if (mBinding.secondaryContainerView == null) {
            intent = Intent(this, PropertyDetailsActivity::class.java)
            startActivity(intent)
        } else {
            launchPropertyDetailsFragment(mBinding.secondaryContainerView!!.id)
        }
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
        mViewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
    }

    private fun initPropertyObserver() {
        mViewModel.getCurrentProperty().observe(this, { mProperty = it })
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