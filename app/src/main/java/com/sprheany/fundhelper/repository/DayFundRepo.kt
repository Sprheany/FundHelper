package com.sprheany.fundhelper.repository

import android.text.format.DateUtils
import com.sprheany.fundhelper.App
import com.sprheany.fundhelper.models.FundWorth
import com.sprheany.fundhelper.net.Net
import com.sprheany.fundhelper.net.services.DayFundService
import java.text.SimpleDateFormat
import java.util.Locale

class DayFundRepo {
    val TAG = "FundDayRepository"

    /**
     * 2023-08-01|1.4109|1.4609|0.0489|3.59%|-0.08%|-0.0011|1.4098|1.3620|2023-08-02|11:40:08
     */
    suspend fun getFundByCode(fundCode: String): FundWorth? {
        val result = try {
            Net.instance.create(DayFundService::class.java)
                .getFundValue(fundCode = fundCode)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val dataList = result.split("|")

        if (dataList.size < 11) {
            return null
        }

        val fund = App.instance.db.fundDao().get(fundCode)

        var exceptWorth = dataList[7]
        var exceptGrowthWorth = dataList[6]
        var exceptGrowthPercent = dataList[5]
        val exceptWorthDate = dataList[9] + " " + dataList[10]

        val exceptDate = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).parse(exceptWorthDate)
        if (!DateUtils.isToday(exceptDate?.time ?: 0)) {
            exceptWorth = "--"
            exceptGrowthWorth = "--"
            exceptGrowthPercent = "--"
        }

        return FundWorth(
            code = fundCode,
            name = fund?.name ?: "",
            netWorth = dataList[1],
            worthDate = dataList[0],
            exceptWorth,
            exceptGrowthWorth,
            exceptGrowthPercent,
            exceptWorthDate,
        )
    }
}