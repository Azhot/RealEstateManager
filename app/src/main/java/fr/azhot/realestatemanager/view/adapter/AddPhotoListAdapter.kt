package fr.azhot.realestatemanager.view.adapter

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.GranularRoundedCorners
import com.bumptech.glide.request.RequestOptions
import fr.azhot.realestatemanager.databinding.CellAddPhotoBinding

class AddPhotoListAdapter(
    private var bitmapList: MutableList<Pair<Bitmap, String>>,
    private val onDeletePhotoListener: OnDeletePhotoListener
) : RecyclerView.Adapter<AddPhotoListAdapter.PhotoMapViewHolder>() {


    // listeners
    interface OnDeletePhotoListener {
        fun onDeletePhoto(pair: Pair<Bitmap, String>)
    }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoMapViewHolder {
        val view = CellAddPhotoBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PhotoMapViewHolder(view, onDeletePhotoListener)
    }

    override fun onBindViewHolder(holder: PhotoMapViewHolder, position: Int) =
        holder.bind(bitmapList[position])

    override fun getItemCount(): Int = bitmapList.count()


    // inner class
    class PhotoMapViewHolder(
        private val binding: CellAddPhotoBinding,
        private val onDeletePhotoListener: OnDeletePhotoListener
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(pair: Pair<Bitmap, String>) {
            Glide.with(itemView)
                .load(pair.first)
                .apply(
                    RequestOptions().transform(
                        CenterCrop(),
                        GranularRoundedCorners(4f, 4f, 0f, 0f)
                    )
                )
                .into(binding.photoImageView)

            binding.photoTitleTextView.text = pair.second

            binding.deleteButton.setOnClickListener {
                onDeletePhotoListener.onDeletePhoto(pair)
            }
        }
    }
}

