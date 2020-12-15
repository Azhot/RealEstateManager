package fr.azhot.realestatemanager.utils

import android.widget.TextView
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import java.util.*

/**
 * Builds and shows a MaterialDatePicker.
 * The date picked is then set to a TextView.
 *
 * @param supportFragmentManager the parent Context
 * @param calendar the Calendar instance to be set
 * @param textView the TextView which text is to be set
 */
fun buildMaterialDatePicker(
    supportFragmentManager: FragmentManager,
    calendar: Calendar,
    textView: TextView,
): MaterialDatePicker<Long> {
    val builder = MaterialDatePicker.Builder.datePicker()
    builder.setSelection(calendar.timeInMillis)
    val picker = builder.build()
    picker.addOnPositiveButtonClickListener { selection ->
        calendar.timeInMillis = selection
        textView.text = picker.headerText
    }
    picker.show(supportFragmentManager, picker.toString())
    return picker
}