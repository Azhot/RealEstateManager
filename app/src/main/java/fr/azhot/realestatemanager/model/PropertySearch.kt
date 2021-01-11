package fr.azhot.realestatemanager.model

class PropertySearch(
    var propertyType: PropertyType? = null,
    var price: List<Float>? = null,
    var squareMeters: List<Float>? = null,
    var rooms: List<Float>? = null,
    var photoListSize: Float? = null,
) {

    fun isActivated(): Boolean {
        return propertyType != null
                && price != null
                && squareMeters != null
                && rooms != null
                && photoListSize != null
    }
}