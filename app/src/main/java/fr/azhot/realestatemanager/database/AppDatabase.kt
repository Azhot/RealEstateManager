package fr.azhot.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import fr.azhot.realestatemanager.database.dao.*
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.utils.TypeConverter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.util.*

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

                    addressDao.insertAddress(
                        Address(
                            "addressId2",
                            "78480",
                            "Verneuil-sur-seine",
                            "Rue du Hameau",
                            "52",
                            "TER",
                        )
                    )

                    realtorDao.insertRealtor(
                        Realtor(
                            "realtorId1",
                            "Thomas",
                            "Duval",
                        )
                    )

                    realtorDao.insertRealtor(
                        Realtor(
                            "realtorId2",
                            "François",
                            "Jouvelot",
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
                            Calendar.getInstance().run {
                                this.set(2020, 12, 10)
                                timeInMillis
                            },
                            null,
                            "realtorId1",
                        )
                    )

                    detailDao.insertDetail(
                        Detail(
                            "detailId2",
                            PropertyType.MANSION,
                            800000,
                            100,
                            5,
                            "Impressive mansion in the Paris suburban area.",
                            "addressId2",
                            Calendar.getInstance().run {
                                this.set(2020, 12, 15)
                                timeInMillis
                            },
                            Calendar.getInstance().run {
                                this.set(2020, 12, 25)
                                timeInMillis
                            },
                            "realtorId2",
                        )
                    )

                    photoDao.insertPhoto(
                        Photo(
                            "photoId1",
                            "detailId1",
                            "https://static.cotemaison.fr/medias_11931/w_600,h_600,c_fill,g_north/v1566200335/amenager-un-salon-cosy-en-multipliant-les-assises_6109137.jpg",
                            "Living-room",
                        )
                    )

                    photoDao.insertPhoto(
                        Photo(
                            "photoId2",
                            "detailId1",
                            "https://www.nouvomeuble.com/boutique/images_produits/chambre-a-coucher-design-blanc_zd1-z.jpg",
                            "Bed-room",
                        )
                    )

                    photoDao.insertPhoto(
                        Photo(
                            "photoId3",
                            "detailId1",
                            "https://www.sundgau-energies.com/public/donnees/cms/sources/pages/bg-sdb-mobile.jpg",
                            "Bath-room",
                        )
                    )

                    photoDao.insertPhoto(
                        Photo(
                            "photoId4",
                            "detailId2",
                            "https://www.nouvomeuble.com/boutique/images_produits/chambre-a-coucher-design-blanc_zd1-z.jpg",
                            "Bed-room",
                        )
                    )

                    photoDao.insertPhoto(
                        Photo(
                            "photoId5",
                            "detailId2",
                            "https://www.sundgau-energies.com/public/donnees/cms/sources/pages/bg-sdb-mobile.jpg",
                            "Bath-room",
                        )
                    )

                    photoDao.insertPhoto(
                        Photo(
                            "photoId6",
                            "detailId2",
                            "https://static.cotemaison.fr/medias_11931/w_600,h_600,c_fill,g_north/v1566200335/amenager-un-salon-cosy-en-multipliant-les-assises_6109137.jpg",
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

                    for (i in 2..8) {
                        pointOfInterestDao.insertPointOfInterest(
                            PointOfInterest(
                                "pointOfInterestId$i",
                                "detailId2",
                                "Hopital${i-1}",
                                Address(
                                    "78300",
                                    "Poissy",
                                    "Rue du Champ-Gaillard",
                                    "10",
                                    "",
                                ),
                            )
                        )
                    }
                }
            }
        }
    }
}
