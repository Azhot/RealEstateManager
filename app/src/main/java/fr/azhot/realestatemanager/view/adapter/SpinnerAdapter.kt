package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.marginStart
import fr.azhot.realestatemanager.R

class SpinnerAdapter(context: Context, resource: Int, private var list: MutableList<String>) :
    ArrayAdapter<String>(context, resource, list) {

    override fun isEnabled(position: Int): Boolean {
        return position > 0
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = super.getDropDownView(position, convertView, parent)
        if (position == 0) {
            (view as TextView).setTextColor(ContextCompat.getColor(context, R.color.gray))
            view.setPadding(32, 16, 32, 16)
        } else {
            (view as TextView).setTextColor(ContextCompat.getColor(context, R.color.black))
            view.setPadding(64, 16, 64, 16)
        }
        return view
    }

    fun updateList(newList: List<String>) {
        list.clear()
        list.addAll(newList)
        notifyDataSetChanged()
    }
}