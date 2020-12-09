package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.ActivityPropertyDetailsBinding
import fr.azhot.realestatemanager.view.fragment.PropertyDetailsFragment

class PropertyDetailsActivity : AppCompatActivity() {


    // variables
    private lateinit var binding: ActivityPropertyDetailsBinding


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = initActivityPropertyDetailsBinding(layoutInflater)
        launchPropertyDetailsFragment(binding.propertyDetailsContainerView.id)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()
        if (resources.getBoolean(R.bool.isLandscape)) {
            finish()
        }
    }


    // functions
    private fun initActivityPropertyDetailsBinding(layoutInflater: LayoutInflater) =
        ActivityPropertyDetailsBinding.inflate(layoutInflater)


    private fun launchPropertyDetailsFragment(containerId: Int) =
        supportFragmentManager
            .beginTransaction()
            .replace(containerId, PropertyDetailsFragment.newInstance())
            .commit()
}