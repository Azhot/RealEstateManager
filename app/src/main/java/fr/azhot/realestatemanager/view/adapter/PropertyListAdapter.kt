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
    private val mGlide: RequestManager,
    private var mPropertyList: List<Property>,
    private val mListener: PropertyClickListener,
) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {


    // interface
    interface PropertyClickListener {
        fun onPropertyClickListener(property: Property)
    }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        val view = CellPropertyBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PropertyViewHolder(view, mGlide)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        val property = mPropertyList[position]
        holder.bind(property)
        holder.itemView.setOnClickListener {
            mListener.onPropertyClickListener(property)
        }
    }

    override fun getItemCount(): Int = mPropertyList.count()


    // inner class
    class PropertyViewHolder(
        private val mBinding: CellPropertyBinding,
        private val mGlide: RequestManager
    ) :
        RecyclerView.ViewHolder(mBinding.root) {

        fun bind(property: Property) {
            // todo : set "no-picture"
            if (property.photos.isNotEmpty()) {
                mGlide
                    .load(property.photos[0].bitmap)
                    .centerCrop()
                    .into(mBinding.photoImageView)
            }

            mBinding.typeTextView.text = property.type
                .toString()
                .toLowerCase(Locale.ROOT)
                .capitalize(Locale.ROOT)

            mBinding.cityTextView.text = property.address.city

            val numberFormat = NumberFormat.getCurrencyInstance(Locale.US)
            numberFormat.maximumFractionDigits = 0
            mBinding.priceTextView.text = numberFormat.format(property.price)
        }
    }
}

