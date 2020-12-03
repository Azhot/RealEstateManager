package fr.azhot.realestatemanager.view.activity

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.model.Property
import fr.azhot.realestatemanager.view.adapter.PropertyListAdapter.PropertyClickListener
import fr.azhot.realestatemanager.view.fragment.PropertyListFragment


class MainActivity : AppCompatActivity(), PropertyClickListener {


    // companion
    companion object {
        //private val TAG = MainActivity::class.simpleName
        const val PROPERTY_EXTRA = "property_extra"
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

    override fun onPropertyClickListener(property: Property) {
        val gson = Gson()
        val intent = Intent(this, PropertyDetailsActivity::class.java)
        intent.putExtra(PROPERTY_EXTRA, gson.toJson(property))
        startActivity(intent)
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
                mBinding.propertyListContainerView.id,
                PropertyListFragment.newInstance(this, propertyList)
            )
            .commit()
    }
}