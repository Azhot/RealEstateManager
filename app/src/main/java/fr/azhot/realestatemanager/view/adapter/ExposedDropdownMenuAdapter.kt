package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.widget.ArrayAdapter

class ExposedDropdownMenuAdapter(
    context: Context,
    resource: Int,
    list: MutableList<*>,
) : ArrayAdapter<Any>(context, resource, list) {
    var position: Int? = null
    var list: MutableList<*> = list
        set(value) {
            field = value
            clear()
            for (item in value) {
                add(item.toString())
            }
        }
}