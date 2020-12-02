package fr.azhot.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.azhot.realestatemanager.databinding.CellPropertyBinding
import fr.azhot.realestatemanager.model.Property
import java.util.*

class PropertyListAdapter(
    private var mProperties: List<Property>,
    private val mListener: PropertyListListener
) :
    RecyclerView.Adapter<PropertyListAdapter.PropertyViewHolder>() {

    interface PropertyListListener {
        fun onClickListener(property: Property)
    }

    class PropertyViewHolder(
        private val mBinding: CellPropertyBinding,
        private val mListener: PropertyListListener
    ) :
        RecyclerView.ViewHolder(mBinding.root), View.OnClickListener {

        private lateinit var mProperty: Property

        fun bind(property: Property) {
            mProperty = property
            mBinding.photoImageView.setImageBitmap(property.photos[0])
            mBinding.typeTextView.text = property.type
                .toString()
                .toLowerCase(Locale.ROOT)
                .capitalize(Locale.ROOT)
            mBinding.cityTextView.text = property.address.city
            mBinding.priceTextView.text = property.price.toString()
        }

        override fun onClick(v: View?) {
            mListener.onClickListener(mProperty)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PropertyViewHolder {
        return PropertyViewHolder(
            CellPropertyBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            ),
            mListener
        )
    }

    override fun onBindViewHolder(holder: PropertyViewHolder, position: Int) {
        holder.bind(mProperties[position])
    }

    override fun getItemCount(): Int {
        return mProperties.size
    }
}

