package fr.azhot.realestatemanager.utils

import androidx.core.util.Pair
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker

/**
 * Builds and shows a MaterialDatePicker.
 * The date picked is then parsed as a [Long] corresponding to the date picked in milliseconds.
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

/**
 * Builds and shows a MaterialDateRangePicker.
 * The date range picked is then parsed as a [Pair] of [Long] corresponding to the dates
 * picked in milliseconds.
 *
 * @param supportFragmentManager The FragmentManager this dialog fragment will be added to.
 * @param initTimeRange The time range to set to the picker on initialization.
 * @param functionToCall The function to be called to perform additional processing with the set date range.
 */
fun buildMaterialDateRangePicker(
    supportFragmentManager: FragmentManager,
    initTimeRange: Pair<Long, Long>?,
    functionToCall: (selectedTimeRange: Pair<Long, Long>) -> (Unit),
): MaterialDatePicker<Pair<Long, Long>> {
    val picker = MaterialDatePicker.Builder.dateRangePicker().run {
        initTimeRange?.let { setSelection(initTimeRange) }
        build()
    }
    picker.addOnPositiveButtonClickListener { selectedTimeRange ->
        functionToCall(selectedTimeRange)
    }
    picker.show(supportFragmentManager, picker.toString())
    return picker
}