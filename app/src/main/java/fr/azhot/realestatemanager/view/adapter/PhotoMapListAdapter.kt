package fr.azhot.realestatemanager.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import fr.azhot.realestatemanager.databinding.CellPhotoMapBinding

class PhotoMapListAdapter(
    private val glide: RequestManager,
    photoMap: MutableMap<Bitmap, String>,
    private val onDeleteListener: OnDeleteListener
) : RecyclerView.Adapter<PhotoMapListAdapter.PhotoMapViewHolder>() {


    // listeners
    interface OnDeleteListener {
        fun OnDeletePhoto(bitmap: Bitmap)
    }


    // variables
    var photoMap: MutableMap<Bitmap, String> = photoMap
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoMapViewHolder {
        val view = CellPhotoMapBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoMapViewHolder(view, glide, onDeleteListener)
    }

    override fun onBindViewHolder(holder: PhotoMapViewHolder, position: Int) =
        holder.bind(photoMap.entries.elementAt(position))

    override fun getItemCount(): Int = photoMap.count()


    // inner class
    class PhotoMapViewHolder(
        private val binding: CellPhotoMapBinding,
        private val glide: RequestManager,
        private val onDeleteListener: OnDeleteListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: Map.Entry<Bitmap, String>) {
            glide.load(entry.key)
                .centerCrop()
                .into(binding.photoImageView)

            binding.photoTitleTextView.text = entry.value

            binding.deleteButton.setOnClickListener() {
                onDeleteListener.OnDeletePhoto(entry.key)
            }
        }
    }
}

