package fr.azhot.realestatemanager.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.sqlite.db.SupportSQLiteDatabase
import com.github.javafaker.Faker
import fr.azhot.realestatemanager.database.dao.*
import fr.azhot.realestatemanager.model.*
import fr.azhot.realestatemanager.utils.TypeConverter
import fr.azhot.realestatemanager.utils.roundInt
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

                    for (i in 0..30) {
                        createProperty(
                            detailDao,
                            addressDao,
                            photoDao,
                            pointOfInterestDao,
                            realtorDao
                        )
                    }
                }
            }
        }

        private suspend fun createProperty(
            detailDao: DetailDao,
            addressDao: AddressDao,
            photoDao: PhotoDao,
            pointOfInterestDao: PointOfInterestDao,
            realtorDao: RealtorDao
        ) {
            val faker = Faker()
            val calendar: Calendar = Calendar.getInstance().apply {
                this.set(
                    2021,
                    faker.number().numberBetween(0, 11),
                    faker.number().numberBetween(1, 32)
                )
            }
            val photoList = arrayListOf(
                Photo(
                    uri = "https://static.cotemaison.fr/medias_11931/w_600,h_600,c_fill,g_north/v1566200335/amenager-un-salon-cosy-en-multipliant-les-assises_6109137.jpg",
                    title = "Living-room",
                ),
                Photo(
                    uri = "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/living-room-ideas-rds-work-queens-road-01-1594233253.jpg?crop=1.00xw:0.803xh;0,0.176xh&resize=640:*",
                    title = "Living-room",
                ),
                Photo(
                    uri = "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/living-room-inspiration-1592237936.jpg",
                    title = "Living-room",
                ),
                Photo(
                    uri = "https://stylebyemilyhenderson.com/wp-content/uploads/2019/08/Emily-Henderson-Moutain-House-Living-Room-LoRes1.jpg",
                    title = "Living-room",
                ),
                Photo(
                    uri = "https://jumanji.livspace-cdn.com/magazine/wp-content/uploads/2019/09/16191216/Contemporary-Living-Room-Easy-Functionality.jpg",
                    title = "Living-room",
                ),
                Photo(
                    uri = "https://cdn.vox-cdn.com/thumbor/mzq4g3lgBnqzBY1-v0R2bt-dQSk=/0x0:4000x2667/1200x800/filters:focal(1680x1014:2320x1654)/cdn.vox-cdn.com/uploads/chorus_image/image/65894378/2BWFDG7.8.jpg",
                    title = "Living-room",
                ),
                Photo(
                    uri = "https://www.mymove.com/wp-content/uploads/2020/07/layout_GettyImages-1177004304.jpg",
                    title = "Living-room",
                ),
                Photo(
                    uri = "https://static.dezeen.com/uploads/2020/03/mark-seelen-grove-house-roger-ferris-partners-hamptons-new-york_dezeen_2364_col_1-852x568.jpg",
                    title = "Living-room",
                ),
                Photo(
                    uri = "https://www.nouvomeuble.com/boutique/images_produits/chambre-a-coucher-design-blanc_zd1-z.jpg",
                    title = "Bed-room",
                ),
                Photo(
                    uri = "https://static.cotemaison.fr/medias_11749/w_2048,h_1146,c_crop,x_0,y_164/w_2000,h_1125,c_fill,g_north/v1518095166/chambre-parentale-avec-mur-bleu_6015724.jpg",
                    title = "Bed-room",
                ),
                Photo(
                    uri = "https://paradisahomes.com/wp-content/uploads/2017/06/IMG_0158.jpg",
                    title = "Bed-room",
                ),
                Photo(
                    uri = "https://www.decorist.com/static/blog_images/328-dcrstcnsrtmnrpllr_cntrst_361_1-_-1a394af7a04f4ebfa797336457d38fa2.jpg",
                    title = "Bed-room",
                ),
                Photo(
                    uri = "https://www.alsacecuisine.fr/public/donnees/cms/sources/pages/hp-03.jpg",
                    title = "Kitchen",
                ),
                Photo(
                    uri = "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/kitchen-ideas-calderone-kitchen-006-1583960334.jpg",
                    title = "Kitchen",
                ),
                Photo(
                    uri = "https://i1.wp.com/movingtips.wpengine.com/wp-content/uploads/2019/04/updated-kitchen.jpg?fit=1024%2C684&ssl=1",
                    title = "Kitchen",
                ),
                Photo(
                    uri = "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/small-kitchen-1572367025.png",
                    title = "Kitchen",
                ),
                Photo(
                    uri = "https://www.jardiner-malin.fr/wp-content/uploads/2020/04/petit_jardin_mise_avant.jpg",
                    title = "Garden",
                ),
                Photo(
                    uri = "https://static.cotemaison.fr/medias_11846/w_640,h_360,c_fill,g_north/v1527605498/un-jardin-parfait_6065458.jpg",
                    title = "Garden",
                ),
                Photo(
                    uri = "https://fac.img.pmdstatic.net/fit/http.3A.2F.2Fprd2-bone-image.2Es3-website-eu-west-1.2Eamazonaws.2Ecom.2FFAC.2Fvar.2Ffemmeactuelle.2Fstorage.2Fimages.2Fjardin.2Fjardinage-les-conseils.2Fconseils-preparer-beau-jardin-facilement-48778.2F14916576-2-fre-FR.2F15-conseils-pour-preparer-un-beau-jardin-facilement.2Ejpg/1200x1200/quality/80/crop-from/center/15-conseils-pour-preparer-un-beau-jardin-facilement.jpeg",
                    title = "Garden",
                ),
                Photo(
                    uri = "https://edito.seloger.com/sites/default/files/styles/manual_crop_735x412/public/article/image/jardins-therapeutiques-lead.jpg?itok=jBLIBjBd",
                    title = "Garden",
                ),
            )

            val address = Address(
                zipCode = faker.address().zipCode(),
                city = faker.address().cityName(),
                roadName = faker.address().streetAddress(),
                number = faker.address().streetAddressNumber(),
                complement = faker.address().streetSuffix(),
            )

            val realtor = Realtor(
                firstName = faker.name().firstName(),
                lastName = faker.name().lastName()
            )

            val detail = Detail(
                propertyType = PropertyType.values()[faker.number().numberBetween(0, 4)],
                price = roundInt(faker.number().numberBetween(500000, 2500001), 10000),
                squareMeters = faker.number().numberBetween(100, 251),
                rooms = faker.number().numberBetween(5, 16),
                description = faker.lorem().paragraph(faker.number().numberBetween(4, 11)),
                addressId = address.addressId,
                entryTimeStamp = calendar.timeInMillis,
                saleTimeStamp = if (faker.bool().bool()) calendar.run {
                    set(Calendar.MONTH, 2)
                    timeInMillis
                } else null,
                realtorId = realtor.realtorId,
            )

            addressDao.insertAddress(address)
            realtorDao.insertRealtor(realtor)
            detailDao.insertDetail(detail)
            repeat(faker.number().numberBetween(2, 8)) {
                photoDao.insertPhoto(
                    photoList[faker.number().numberBetween(0, photoList.size)].apply {
                        detailId = detail.detailId
                    })
            }

            repeat(faker.number().numberBetween(0, 7)) {
                pointOfInterestDao.insertPointOfInterest(
                    PointOfInterest(
                        detailId = detail.detailId,
                        name = faker.address().cityName(),
                        address = Address(
                            zipCode = faker.address().zipCode(),
                            city = faker.address().cityName(),
                            roadName = faker.address().streetAddress(),
                            number = faker.address().streetAddressNumber(),
                            complement = faker.address().streetSuffix(),
                        )
                    )
                )
            }
        }
    }
}
