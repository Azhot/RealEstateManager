package fr.azhot.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.RequestManager
import fr.azhot.realestatemanager.databinding.CellPropertyBinding
import fr.azhot.realestatemanager.model.Property
import java.text.NumberFormat
import java.util.*

class PropertyListAdapter(
    private val glide: RequestManager,
    private var propertyList: List<Property>,
    private val listener: PropertyClickListener,
) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {


    // interface
    interface PropertyClickListener {
        fun onPropertyClickListener(property: Property)
    }


    // functions
    fun setPropertyList(newPropertyList: List<Property>) {
        propertyList = newPropertyList
        notifyDataSetChanged()
    }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = CellPropertyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PropertyViewHolder(view, glide)
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
        private val glide: RequestManager,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(property: Property) {
            glide
                .load(property.photoList[0].uri)
                .centerCrop()
                .into(binding.photoImageView)

            binding.typeTextView.text =
                property.detail.propertyType.toString()
                    .toLowerCase(Locale.ROOT)
                    .capitalize(Locale.ROOT)

            binding.cityTextView.text = property.address?.city

            val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            numberFormat.maximumFractionDigits = 0
            binding.priceTextView.text = numberFormat.format(property.detail.price)
        }
    }
}

