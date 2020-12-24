package fr.azhot.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import fr.azhot.realestatemanager.databinding.CellPhotoBinding
import fr.azhot.realestatemanager.model.Photo

class MediaListAdapter(
    private val glide: RequestManager,
    photoList: MutableList<Photo>,
) : RecyclerView.Adapter<MediaListAdapter.PhotoViewHolder>() {


    // variables
    var photoList: MutableList<Photo> = photoList
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = CellPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoViewHolder(view, glide)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) =
        holder.bind(photoList[position])

    override fun getItemCount(): Int = photoList.count()


    // inner class
    class PhotoViewHolder(
        private val binding: CellPhotoBinding,
        private val glide: RequestManager
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(photo: Photo) {
            glide
                .load(photo.uri)
                .centerCrop()
                .into(binding.photoImageView)

            binding.photoTitleTextView.text = photo.title
        }
    }
}

