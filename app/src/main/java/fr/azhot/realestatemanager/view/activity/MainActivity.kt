package fr.azhot.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragment
import fr.azhot.realestatemanager.view.fragment.PropertyListFragment
import fr.azhot.realestatemanager.viewmodel.MainActivityViewModel


class MainActivity : AppCompatActivity(), PropertyClickListener {


    // variables
    private lateinit var mBinding: ActivityMainBinding
    private lateinit var mViewModel: MainActivityViewModel
    private lateinit var mProperty: Property


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = initActivityMainBinding(layoutInflater)
        mViewModel = initMainActivityViewModel(this)
        initPropertyObserver()
        launchPropertyListFragment(mBinding.mainContainerView.id)
        setContentView(mBinding.root)
    }

    override fun onResume() {
        super.onResume()
        if (mBinding.secondaryContainerView != null && this::mProperty.isInitialized) {
            launchPropertyDetailsFragment(mBinding.secondaryContainerView!!.id)
        }
    }

    override fun onPropertyClickListener(property: Property) {
        mViewModel.setCurrentProperty(property)
        if (mBinding.secondaryContainerView != null) {
            launchPropertyDetailsFragment(mBinding.secondaryContainerView!!.id)
        } else {
            intent = Intent(this, PropertyDetailsActivity::class.java)
            startActivity(intent)
        }
    }


    // functions
    private fun initActivityMainBinding(layoutInflater: LayoutInflater) =
        ActivityMainBinding.inflate(layoutInflater)

    private fun initMainActivityViewModel(owner: ViewModelStoreOwner) =
        ViewModelProvider(owner).get(MainActivityViewModel::class.java)

    private fun initPropertyObserver() =
        mViewModel.getCurrentProperty().observe(this, { mProperty = it })


    private fun launchPropertyListFragment(containerId: Int) =
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, PropertyListFragment.newInstance())
            .commit()


    private fun launchPropertyDetailsFragment(containerId: Int) =
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, PropertyDetailsFragment.newInstance())
            .commit()
}