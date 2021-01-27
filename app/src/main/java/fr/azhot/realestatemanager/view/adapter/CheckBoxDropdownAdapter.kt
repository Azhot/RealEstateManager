package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.appcompat.widget.AppCompatCheckBox
import fr.azhot.realestatemanager.R

class CheckBoxDropdownAdapter<T>(
    context: Context,
    itemResource: Int,
    items: List<T>,
    private val existingData: List<T>?,
    private val itemCheckListener: ItemCheckListener,
) : ArrayAdapter<T>(context, itemResource, items) {

    // listener
    interface ItemCheckListener {
        fun onItemCheckListener(isChecked: Boolean, item: Any?)
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: View.inflate(context, R.layout.cell_poi_type, null)
        val checkBox = view.findViewById<AppCompatCheckBox>(R.id.poi_checkbox)
        checkBox.apply {
            text = getItem(position).toString()
            existingData?.let { isChecked = it.contains(getItem(position)) }
            setOnCheckedChangeListener { _, isChecked ->
                itemCheckListener.onItemCheckListener(isChecked, getItem(position))
            }
        }
        return view
    }
}