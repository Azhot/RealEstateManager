package fr.azhot.realestatemanager.view.adapter

import android.content.Context
import android.widget.ArrayAdapter

class ExposedDropdownMenuAdapter(
    context: Context,
    resource: Int,
    listString: List<String>,
) : ArrayAdapter<String>(context, resource, listString)