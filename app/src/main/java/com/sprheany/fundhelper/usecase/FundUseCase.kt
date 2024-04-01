package com.sprheany.fundhelper.usecase

import com.sprheany.fundhelper.App
import com.sprheany.fundhelper.models.FundWorth
import com.sprheany.fundhelper.repository.CollectionFundRepo
import com.sprheany.fundhelper.repository.DayFundRepo
import com.sprheany.fundhelper.repository.EastMoneyRepo
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map

object FundUseCase {
    private val eastMoneyRepo = EastMoneyRepo()
    private val dayFundRepo = DayFundRepo()
    private val collectionFundRepo = CollectionFundRepo()
    private val fundDao by lazy { App.instance.db.fundDao() }

    suspend fun requestAllFund() {
        val list = eastMoneyRepo.requestAllFund()
        fundDao.upsert(*list.toTypedArray())
    }

    suspend fun collectFund(fundCode: String) {
        collectionFundRepo.addFund(fundCode)
    }

    suspend fun removeCollectionFund(fundCode: String) {
        collectionFundRepo.removeFund(fundCode)
    }

    suspend fun swipeFund(fromCode: String, toCode: String) {
        collectionFundRepo.swipeFund(fromCode, toCode)
    }

    private fun findFund(text: String) =
        if (text.isEmpty()) fundDao.getAll() else fundDao.find(text)

    fun searchFunds(text: String) =
        findFund(text)
            .combine(collectionFundRepo.collectionFundFlow) { fundList, collectionFundList ->
                fundList.map { fund ->
                    fund.copy(isCollection = collectionFundList.any { it.code == fund.code })
                }
            }

    val collectionFundWorthFlow =
        collectionFundRepo.collectionFundFlow
            .map { fund -> fund.map { it.code } }
            .map { fundCodes ->
                val fundWorthList = ArrayList<FundWorth>()
                fundCodes.forEach { code ->
                    val fund = eastMoneyRepo.getFundByCode(code)
                        ?: dayFundRepo.getFundByCode(code)
                        ?: fundDao.get(code)?.run { FundWorth(this) }
                    fund?.run {
                        fundWorthList.add(this)
                    }
                }
                fundWorthList
            }
}
