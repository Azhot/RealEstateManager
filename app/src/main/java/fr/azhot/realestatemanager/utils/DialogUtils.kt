package fr.azhot.realestatemanager.utils

import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker

/**
 * Builds and shows a MaterialDatePicker.
 * The date picked is then parsed as a [Long] corresponding to the time in milliseconds.
 *
 * @param supportFragmentManager The FragmentManager this dialog fragment will be added to.
 * @param initTimeInMillis The time to set to the picker on initialization.
 * @param functionToCall The function to be called to perform additional processing with the set date.
 */
fun buildMaterialDatePicker(
    supportFragmentManager: FragmentManager,
    initTimeInMillis: Long,
    functionToCall: (selectedTimeInMillis: Long) -> (Unit),
): MaterialDatePicker<Long> {
    val picker = MaterialDatePicker.Builder.datePicker().run {
        setSelection(initTimeInMillis)
        build()
    }
    picker.addOnPositiveButtonClickListener { selectedTimeInMillis ->
        functionToCall(selectedTimeInMillis)
    }
    picker.show(supportFragmentManager, picker.toString())
    return picker
}