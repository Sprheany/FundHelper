package com.sprheany.fundhelper.repository


import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.sprheany.fundhelper.App
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class CollectionFundRepo {
    private val dataStore by lazy { App.instance.applicationContext.dataStore }

    val collectionFundFlow = dataStore.data
        .catch {
            emptySet<String>()
        }.map { preferences ->
            preferences[PreferencesKeys.COLLECTION_FUND] ?: emptySet()
        }

    private suspend fun saveCollectionFund(funds: List<String>) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.COLLECTION_FUND] = funds.toSet()
        }
    }

    suspend fun addFund(fundCode: String) {
        val list = collectionFundFlow.first().toMutableList()
        list.add(fundCode)
        saveCollectionFund(list)
    }

    suspend fun removeFund(fundCode: String) {
        val list = collectionFundFlow.first().toMutableList()
        list.remove(fundCode)
        saveCollectionFund(list)
    }
}

private const val COLLECTION_FUND_PREFERENCES_NAME = "collection_fund_preferences"

private val Context.dataStore by preferencesDataStore(
    name = COLLECTION_FUND_PREFERENCES_NAME
)

private object PreferencesKeys {
    val COLLECTION_FUND = stringSetPreferencesKey("collection_fund")
}