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

            property.detail.propertyType?.let {
                binding.typeTextView.text =
                    it.toString().toLowerCase(Locale.ROOT).capitalize(Locale.ROOT)
            }

            property.address?.let {
                binding.cityTextView.text = it.city
            }

            property.detail.price?.let {
                binding.priceTextView.text = NumberFormat.getCurrencyInstance(Locale.US).run {
                    maximumFractionDigits = 0
                    format(it)
                }
            }
        }
    }
}

