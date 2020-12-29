package fr.azhot.realestatemanager.view.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.ActivityOpenPhotoBinding
import fr.azhot.realestatemanager.model.Photo
import fr.azhot.realestatemanager.utils.PHOTO_EXTRA

class OpenPhotoActivity : AppCompatActivity() {


    // variables
    private lateinit var binding: ActivityOpenPhotoBinding


    // overridden functions
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOpenPhotoBinding.inflate(layoutInflater)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val photo = intent.getParcelableExtra<Photo>(PHOTO_EXTRA)
        if (photo != null) {
            loadPhoto(photo)
        }

        setContentView(binding.root)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> onBackPressed()
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }


    // functions
    private fun loadPhoto(photo: Photo) {
        Glide.with(this)
            .load(photo.uri)
            .into(binding.root)
        supportActionBar?.title = photo.title
    }
}