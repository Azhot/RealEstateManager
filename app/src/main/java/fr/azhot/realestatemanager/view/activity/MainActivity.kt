package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.LayoutInflater
import androidx.appcompat.app.AppCompatActivity
import fr.azhot.realestatemanager.databinding.ActivityMainBinding
import fr.azhot.realestatemanager.view.fragment.PropertyListFragment

class MainActivity : AppCompatActivity() {


    // companion
    companion object {
        private val TAG = MainActivity::class.simpleName
    }


    // variables
    lateinit var mBinding: ActivityMainBinding


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(layoutInflater)
        launchPropertyListFragment()
        setContentView(mBinding.root)
    }


    // functions
    private fun init(layoutInflater: LayoutInflater) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
    }

    private fun launchPropertyListFragment() {
        supportFragmentManager
            .beginTransaction()
            .replace(mBinding.fragmentContainerView.id, PropertyListFragment.newInstance())
            .commit()
    }
}