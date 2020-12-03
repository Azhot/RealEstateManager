package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import fr.azhot.realestatemanager.databinding.ActivityPropertyDetailsBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.activity.MainActivity.Companion.PROPERTY_EXTRA
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragment

class PropertyDetailsActivity() : AppCompatActivity() {


    // companion
    companion object {
        private val TAG = PropertyDetailsActivity::class.simpleName
    }


    // variables
    private lateinit var mBinding: ActivityPropertyDetailsBinding


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(layoutInflater)
        launchPropertyDetailsFragment()
        setContentView(mBinding.root)
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = ActivityPropertyDetailsBinding.inflate(layoutInflater)
    }

    private fun getPropertyFromIntent(): Property {
        val gson = Gson()
        val stringProperty = intent.getStringExtra(PROPERTY_EXTRA)
        return gson.fromJson(stringProperty, Property::class.java)
    }

    private fun launchPropertyDetailsFragment() {
        val property = getPropertyFromIntent()
        supportFragmentManager
            .beginTransaction()
            .replace(
                mBinding.propertyDetailsContainerView.id,
                PropertyDetailsFragment.newInstance(property)
            )
            .commit()
    }
}