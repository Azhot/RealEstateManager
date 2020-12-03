package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragment
import fr.azhot.realestatemanager.view.fragment.PropertyListFragment


class MainActivity : AppCompatActivity(), PropertyClickListener {


    // companion
    companion object {
        private val TAG = MainActivity::class.simpleName
    }


    // variables
    private lateinit var mBinding: ActivityMainBinding


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(layoutInflater)
        launchPropertyListFragment()
        setContentView(mBinding.root)
    }

    override fun onBackPressed() {
        if (supportFragmentManager.backStackEntryCount > 0) {
            supportFragmentManager.popBackStack()
        } else {
            super.onBackPressed()
        }
    }

    override fun onPropertyClickListener(property: Property) {
        supportFragmentManager
            .beginTransaction()
            .replace(
                mBinding.fragmentContainerView.id,
                PropertyDetailsFragment.newInstance(property)
            )
            .addToBackStack(null)
            .commit()
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
    }

    private fun launchPropertyListFragment() {
        val propertyList = Property.populatePropertyList(this)
        supportFragmentManager
            .beginTransaction()
            .replace(
                mBinding.fragmentContainerView.id, PropertyListFragment.newInstance(
                    this,
                    propertyList
                )
            )
            .commit()
    }
}