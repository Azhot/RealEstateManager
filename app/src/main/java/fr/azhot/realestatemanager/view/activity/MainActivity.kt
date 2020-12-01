package fr.azhot.realestatemanager.view.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import fr.azhot.realestatemanager.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    lateinit var mBinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init(layoutInflater)
        setContentView(mBinding.root)
    }

    private fun init(layoutInflater: LayoutInflater) {
        mBinding = ActivityMainBinding.inflate(layoutInflater)
    }
}