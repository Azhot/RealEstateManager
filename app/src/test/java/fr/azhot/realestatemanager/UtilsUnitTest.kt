package fr.azhot.realestatemanager

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkInfo
import android.os.Build
import android.test.mock.MockContext
import fr.azhot.realestatemanager.utils.Utils
import fr.azhot.realestatemanager.utils.Utils.EUR_DOLLAR_EXCHANGE_RATE
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.roundToInt

class UtilsUnitTest {

    @Test
    fun convertEuroToDollar_isCorrect() {
        val dollars = 123
        val euros = (dollars * EUR_DOLLAR_EXCHANGE_RATE).roundToInt()

        assertEquals(dollars, Utils.convertEuroToDollar(euros))
    }

    @Test
    fun convertDollarToEuro_isCorrect() {
        val euros = 123
        val dollars = (euros / EUR_DOLLAR_EXCHANGE_RATE).roundToInt()

        assertEquals(dollars, Utils.convertEuroToDollar(euros))
    }

    @Test
    fun getDateFormatted_isWorking() {
        val format = SimpleDateFormat("dd/MM/yyyy")
        val date = format.format(Date())

        assertEquals(date, Utils.getTodayDate())
    }

    @Test
    fun isConnectionAvailable() {
        val cm = mock(ConnectivityManager::class.java)

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            val ni = mock(NetworkInfo::class.java)
            `when`(ni.isConnectedOrConnecting).thenReturn(true)
            `when`(cm.activeNetworkInfo).thenReturn(ni)
        } else {
            val nc = mock(NetworkCapabilities::class.java)
            `when`(nc.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)).thenReturn(true)
            `when`(cm.getNetworkCapabilities(cm.activeNetwork)).thenReturn(nc)
        }

        val context = mock(MockContext::class.java)
        `when`(context.getSystemService(Context.CONNECTIVITY_SERVICE)).thenReturn(cm)

        assertTrue(Utils.isInternetAvailable(context))
    }
}