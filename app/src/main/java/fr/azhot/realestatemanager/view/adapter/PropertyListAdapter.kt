package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.azhot.realestatemanager.databinding.CellPropertyBinding
import fr.azhot.realestatemanager.model.Property
import java.util.*

class PropertyListAdapter(
    private val mContext: Context,
    private var mProperties: List<Property>,
    private val mListener: PropertyClickListener,
) : RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {


    // companion
    companion object {
        private val TAG = PropertyListAdapter::class.simpleName
    }


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
        return PropertyViewHolder(view)
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {

        val data = mProperties[position]
        holder.bind(data)
        holder.itemView.setOnClickListener {
            mListener.onPropertyClickListener(data)
        }
    }

    override fun getItemCount(): Int {
        return mProperties.count()
    }


    // inner class
    class PropertyViewHolder(private val mBinding: CellPropertyBinding) :
        RecyclerView.ViewHolder(mBinding.root) {

        private lateinit var mProperty: Property

        fun bind(property: Property) {
            mProperty = property
            // todo : set "no-picture"
            mBinding.photoImageView.setImageBitmap(if (property.photos.isNotEmpty()) property.photos[0] else null)
            mBinding.typeTextView.text = property.type
                .toString()
                .toLowerCase(Locale.ROOT)
                .capitalize(Locale.ROOT)
            mBinding.cityTextView.text = property.address.city
            // todo : format as price e.g. $1,500,000
            mBinding.priceTextView.text = property.price.toString()
        }
    }
}

