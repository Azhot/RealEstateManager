package fr.azhot.realestatemanager.view.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.azhot.realestatemanager.databinding.CellPoiTypeBinding
import fr.azhot.realestatemanager.model.PointOfInterestType

class PoiTypeListAdapter(
    private val poiTypeArray: Array<PointOfInterestType>,
    private val existingData: MutableList<PointOfInterestType>?,
    private val poiCheckboxListener: PoiCheckboxListener,
) :
    RecyclerView.Adapter<PoiTypeListAdapter.PoiTypeViewHolder>() {


    // interface
    interface PoiCheckboxListener {
        fun onPoiCheckboxListener(poiType: PointOfInterestType, isChecked: Boolean)
    }


    // overridden functions
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiTypeViewHolder {
        val view = CellPoiTypeBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return PoiTypeViewHolder(view, poiCheckboxListener)
    }

    override fun onBindViewHolder(holder: PoiTypeViewHolder, position: Int) =
        holder.bind(poiTypeArray[position], existingData?.contains(poiTypeArray[position]))

    override fun getItemCount(): Int = poiTypeArray.count()


    // inner class
    class PoiTypeViewHolder(
        private val binding: CellPoiTypeBinding,
        private val poiCheckboxListener: PoiCheckboxListener,
    ) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(poiType: PointOfInterestType, isAlreadyChecked: Boolean?) {
            binding.root.text = poiType.toString()
            isAlreadyChecked?.let { binding.root.isChecked = it }
            binding.root.setOnCheckedChangeListener { _, isChecked ->
                poiCheckboxListener.onPoiCheckboxListener(poiType, isChecked)
            }
        }
    }
}

