package fr.azhot.realestatemanager.model

data class Address(
    val id: Long,
    var name: String,
    var zipCode: String,
    var city: String,
    var roadName: String,
    var number: String,
    var complement: String?,
)