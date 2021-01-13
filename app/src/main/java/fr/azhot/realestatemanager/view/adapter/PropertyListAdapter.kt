package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.os.Build
import android.view.LayoutInflater
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.databinding.CellPropertyBinding
import fr.azhot.realestatemanager.model.Property
import java.text.NumberFormat
import java.util.*

class PropertyListAdapter(
    propertyList: List<Property>,
    private val listener: PropertyClickListener,
) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {


    // interface
    interface PropertyClickListener {
        fun onPropertyClickListener(property: Property)
    }


    // variables
    var propertyList: List<Property> = propertyList
        set(value) {
            field = value
            notifyDataSetChanged()
        }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = CellPropertyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PropertyViewHolder(view, parent.context)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = propertyList[position]
        holder.bind(property)
        holder.itemView.setOnClickListener {
            listener.onPropertyClickListener(property)
        }
    }

    override fun getItemCount(): Int = propertyList.count()


    // inner class
    class PropertyViewHolder(
        private val binding: CellPropertyBinding,
        private val context: Context
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(property: Property) {
            if (property.photoList.isNotEmpty()) { // to deal with database populating
                Glide.with(itemView)
                    .load(property.photoList[0].uri)
                    .centerCrop()
                    .into(binding.photoImageView)
            }

            binding.typeTextView.text =
                if (property.detail.propertyType != null) property.detail.propertyType.toString()
                    .toLowerCase(Locale.ROOT)
                    .capitalize(Locale.ROOT)
                else null

            binding.cityTextView.text = property.address.city

            binding.priceTextView.text =
                if (property.detail.price != null) NumberFormat.getCurrencyInstance(Locale.US).run {
                    maximumFractionDigits = 0
                    format(property.detail.price)
                }
                else null

            if (property.detail.saleTimeStamp != null) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.constraintLayout.foreground = ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_white_transparent_foreground_with_corner
                    )
                }
                binding.soldImageView.visibility = VISIBLE
            } else {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    binding.constraintLayout.foreground = null
                }
                binding.soldImageView.visibility = GONE
            }
        }
    }
}

