package fr.azhot.realestatemanager.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import fr.azhot.realestatemanager.databinding.CellAddPhotoBinding

class AddPhotoListAdapter(
    private val glide: RequestManager,
    bitmapStringMutableMap: MutableMap<Bitmap, String>,
    private val onDeletePhotoListener: OnDeletePhotoListener
) : RecyclerView.Adapter<AddPhotoListAdapter.PhotoMapViewHolder>() {


    // listeners
    interface OnDeletePhotoListener {
        fun onDeletePhoto(bitmap: Bitmap)
    }


    // variables
    var bitmapStringMutableMap: MutableMap<Bitmap, String> = bitmapStringMutableMap
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoMapViewHolder {
        val view = CellAddPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoMapViewHolder(view, glide, onDeletePhotoListener)
    }

    override fun onBindViewHolder(holder: PhotoMapViewHolder, position: Int) =
        holder.bind(bitmapStringMutableMap.entries.elementAt(position))

    override fun getItemCount(): Int = bitmapStringMutableMap.count()


    // inner class
    class PhotoMapViewHolder(
        private val binding: CellAddPhotoBinding,
        private val glide: RequestManager,
        private val onDeletePhotoListener: OnDeletePhotoListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(entry: Map.Entry<Bitmap, String>) {
            glide.load(entry.key)
                .centerCrop()
                .into(binding.photoImageView)

            binding.photoTitleTextView.text = entry.value

            binding.deleteButton.setOnClickListener() {
                onDeletePhotoListener.onDeletePhoto(entry.key)
            }
        }
    }
}

