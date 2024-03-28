package com.sprheany.fundhelper.repository

import com.sprheany.fundhelper.App
import com.sprheany.fundhelper.database.entities.CollectionFundEntity
import kotlinx.coroutines.flow.first

class CollectionFundRepo {
    private val collectionFundDao by lazy { App.instance.db.collectionFundDao() }

    val collectionFundFlow = collectionFundDao.getAll()

    suspend fun addFund(fundCode: String) {
        val fund = CollectionFundEntity(
            code = fundCode, sort = collectionFundDao.getMaxSort() + 1
        )
        collectionFundDao.insertAll(fund)
    }

    suspend fun removeFund(fundCode: String) {
        collectionFundDao.delete(CollectionFundEntity(code = fundCode))
    }

    suspend fun swipeFund(fromCode: String, toCode: String) {
        collectionFundFlow.first().run {
            val fromIndex = this.indexOfFirst { it.code == fromCode }
            val toIndex = this.indexOfFirst { it.code == toCode }
            val preIndex = if (fromIndex > toIndex) toIndex - 1 else toIndex
            val nextIndex = preIndex + 1
            val preSort = this.getOrNull(preIndex)?.sort ?: 0f
            val nextSort = this.getOrNull(nextIndex)?.sort ?: (collectionFundDao.getMaxSort() + 1)
            val sort = (preSort + nextSort) / 2
            collectionFundDao.upsert(this[fromIndex].apply { this.sort = sort })
        }
    }
}
