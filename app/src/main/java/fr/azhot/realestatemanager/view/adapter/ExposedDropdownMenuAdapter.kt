package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.widget.ArrayAdapter

class ExposedDropdownMenuAdapter(
    context: Context,
    resource: Int,
    list: MutableList<Any>,
) : ArrayAdapter<Any>(context, resource, list) {
    var list: MutableList<Any> = list
        set(value) {
            field = value
            clear()
            addAll(list)
        }
}