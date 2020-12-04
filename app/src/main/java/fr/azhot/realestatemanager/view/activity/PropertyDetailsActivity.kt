package fr.azhot.realestatemanager.view.activity

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import fr.azhot.realestatemanager.databinding.ActivityPropertyDetailsBinding
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragment

class PropertyDetailsActivity : AppCompatActivity() {


    // companion
    companion object {
        // private val TAG = PropertyDetailsActivity::class.simpleName
    }


    // variables
    private lateinit var mBinding: ActivityPropertyDetailsBinding


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(layoutInflater)
        launchPropertyDetailsFragment(mBinding.propertyDetailsContainerView.id)
        setContentView(mBinding.root)
    }

    override fun onResume() {
        super.onResume()
        if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            finish()
        }
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = ActivityPropertyDetailsBinding.inflate(layoutInflater)
    }

    private fun launchPropertyDetailsFragment(containerId: Int) {
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, PropertyDetailsFragment.newInstance())
            .commit()
    }
}