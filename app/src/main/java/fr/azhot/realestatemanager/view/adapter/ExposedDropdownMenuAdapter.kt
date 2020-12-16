package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.widget.ArrayAdapter

class ExposedDropdownMenuAdapter(
    context: Context,
    resource: Int,
    var list: List<*>,
) : ArrayAdapter<Any>(context, resource, list) {
    var position: Int? = null
}