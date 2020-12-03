package fr.azhot.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import fr.azhot.realestatemanager.databinding.CellPhotoBinding
import fr.azhot.realestatemanager.model.Photo

class MediaListAdapter(
    private val mGlide: RequestManager,
    private var mPhotos: List<Photo>,
) : RecyclerView.Adapter<MediaListAdapter.PhotoViewHolder>() {


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = CellPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(view, mGlide)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val data = mPhotos[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int {
        return mPhotos.count()
    }


    // inner class
    class PhotoViewHolder(
        private val mBinding: CellPhotoBinding,
        private val mGlide: RequestManager
    ) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(photo: Photo) {
            mGlide
                .load(photo.bitmap)
                .centerCrop()
                .into(mBinding.photoImageView)

            mBinding.photoDescriptionTextView.text = photo.description
        }
    }
}

