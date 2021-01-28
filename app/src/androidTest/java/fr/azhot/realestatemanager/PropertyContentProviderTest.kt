package fr.azhot.realestatemanager

import android.content.ContentResolver
import android.content.ContentUris
import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import fr.azhot.realestatemanager.database.AppDatabase
import fr.azhot.realestatemanager.provider.PropertyContentProvider
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PropertyContentProviderTest {

    private lateinit var appDatabase: AppDatabase
    private lateinit var contentResolver: ContentResolver
    private val userId = 1L

    @Before
    fun setUp() = runBlocking {
        appDatabase = Room.inMemoryDatabaseBuilder(
            InstrumentationRegistry.getInstrumentation().context,
            AppDatabase::class.java
        )
            .allowMainThreadQueries()
            .build()
        contentResolver = InstrumentationRegistry.getInstrumentation().context.contentResolver
    }

    @Test
    fun testQuery() {
        val cursor = contentResolver.query(
            ContentUris.withAppendedId(PropertyContentProvider.uri, userId),
            null,
            null,
            null,
            null
        )
        assertNotNull(cursor)
        cursor?.getColumnIndexOrThrow("detailId")
        cursor?.getColumnIndexOrThrow("propertyType")
        cursor?.getColumnIndexOrThrow("price")
        cursor?.getColumnIndexOrThrow("squareMeters")
        cursor?.getColumnIndexOrThrow("rooms")
        cursor?.getColumnIndexOrThrow("description")
        cursor?.getColumnIndexOrThrow("entryTimeStamp")
        cursor?.getColumnIndexOrThrow("saleTimeStamp")
        cursor?.getColumnIndexOrThrow("a.zipCode")
        cursor?.getColumnIndexOrThrow("a.city")
        cursor?.getColumnIndexOrThrow("a.roadName")
        cursor?.getColumnIndexOrThrow("a.number")
        cursor?.getColumnIndexOrThrow("a.complement")
        cursor?.getColumnIndexOrThrow("poi.pointOfInterestType")
        cursor?.getColumnIndexOrThrow("poi.zipCode")
        cursor?.getColumnIndexOrThrow("poi.city")
        cursor?.getColumnIndexOrThrow("poi.roadName")
        cursor?.getColumnIndexOrThrow("poi.number")
        cursor?.getColumnIndexOrThrow("poi.complement")
        cursor?.getColumnIndexOrThrow("r.firstName")
        cursor?.getColumnIndexOrThrow("r.lastName")
        cursor?.close()
    }
}
