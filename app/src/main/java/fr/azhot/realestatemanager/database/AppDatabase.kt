package fr.azhot.realestatemanager.database

import android.content.Context
import android.net.Uri
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.azhot.realestatemanager.R
import fr.azhot.realestatemanager.database.dao.*
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.utils.TypeConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Database(
    entities = [
        Detail::class,
        Address::class,
        Photo::class,
        PointOfInterest::class,
        Realtor::class,
    ], version = 1, exportSchema = false
)
@TypeConverters(TypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    // daos
    abstract fun detailDao(): DetailDao
    abstract fun addressDao(): AddressDao
    abstract fun photoDao(): PhotoDao
    abstract fun pointOfInterestDao(): PointOfInterestDao
    abstract fun realtorDao(): RealtorDao


    // companion object
    companion object {

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
            return INSTANCE ?: synchronized(AppDatabase::class) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "Database"
                )
                    .fallbackToDestructiveMigration()
                    .addCallback(AppDatabaseCallback(scope))
                    .build()
                INSTANCE = instance
                return instance
            }
        }
    }


    // callback
    private class AppDatabaseCallback(private val scope: CoroutineScope) : RoomDatabase.Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val detailDao = database.detailDao()
                    val addressDao = database.addressDao()
                    val photoDao = database.photoDao()
                    val pointOfInterestDao = database.pointOfInterestDao()
                    val realtorDao = database.realtorDao()

                    addressDao.insertAddress(
                        Address(
                            "addressId1",
                            "75116",
                            "Paris",
                            "Rue Bidon",
                            "123",
                            "",
                        )
                    )

                    realtorDao.insertRealtor(
                        Realtor(
                            "realtorId1",
                            "Thomas",
                            "Duval",
                        )
                    )

                    detailDao.insertDetail(
                        Detail(
                            "detailId1",
                            PropertyType.DUPLEX,
                            1500000,
                            150,
                            7,
                            "Nice duplex in Paris",
                            "addressId1",
                            1000L,
                            null,
                            "realtorId1",
                        )
                    )

                    photoDao.insertPhoto(
                        Photo(
                            "photoId1",
                            "detailId1",
                            Uri.parse("android.resource:drawable" + R.drawable.thumb_living_room)
                                .toString(),
                            "Living-room",
                        )
                    )

                    pointOfInterestDao.insertPointOfInterest(
                        PointOfInterest(
                            "pointOfInterestId1",
                            "detailId1",
                            "Café des amis",
                            Address(
                                "75116",
                                "Paris",
                                "Rue du café",
                                "23",
                                "",
                            ),
                        )
                    )
                }
            }
        }
    }
}
