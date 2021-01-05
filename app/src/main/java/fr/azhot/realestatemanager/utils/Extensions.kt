package fr.azhot.realestatemanager.utils

import androidx.lifecycle.MutableLiveData

/**
 * Extends MutableLiveData holding a MutableList of objects with the += operator.
 * The held MutableList will be incremented with the [item] passed in.
 *
 * @param item the item to add to the MutableList held by the MutableLiveData
 */
operator fun <T> MutableLiveData<MutableList<T>>.plusAssign(item: T) {
    val value = this.value ?: mutableListOf()
    value += item
    this.value = value
}

/**
 * Extends MutableLiveData holding a MutableMap of objects with the += operator.
 * The held MutableMap will be incremented with the [Pair] passed in.
 *
 * @param pair the [Pair] to add to the MutableMap held by the MutableLiveData
 */
operator fun <T, S> MutableLiveData<MutableMap<T, S>>.plusAssign(pair: Pair<T, S>) {
    val value = this.value ?: mutableMapOf()
    value[pair.first] = pair.second
    this.value = value
}