package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatCheckBox
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.model.PointOfInterestType

class CheckBoxDropdownAdapter(
    context: Context,
    itemResource: Int,
    items: List<Any>,
    private val existingData: List<PointOfInterestType>?,
    private val itemCheckListener: ItemCheckListener,
) : ArrayAdapter<Any>(context, itemResource, items) {

    // listener
    interface ItemCheckListener {
        fun onItemCheckListener(isChecked: Boolean, item: Any?)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val checkBox = convertView ?: View.inflate(context, R.layout.cell_poi_type, null)
        checkBox as AppCompatCheckBox
        checkBox.text = getItem(position).toString()
        existingData?.let {
            if (it.contains(getItem(position))) {
                checkBox.isChecked = true
            }
        }
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            itemCheckListener.onItemCheckListener(isChecked, getItem(position))
        }
        return checkBox
    }
}