package com.sprheany.fundhelper.repository

import android.text.format.DateUtils
import com.sprheany.fundhelper.App
import com.sprheany.fundhelper.models.FundWorth
import com.sprheany.fundhelper.net.Net
import com.sprheany.fundhelper.net.services.DayFundService
import java.text.SimpleDateFormat
import java.util.Locale

class DayFundRepo {

    /**
     * 2023-08-01|1.4109|1.4609|0.0489|3.59%|-0.08%|-0.0011|1.4098|1.3620|2023-08-02|11:40:08
     */
    suspend fun getFundByCode(fundCode: String): FundWorth? {
        val result = try {
            Net.instance.create(DayFundService::class.java).getFundValue(fundCode = fundCode)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }

        val dataList = result.split("|")

        if (dataList.size < 11 || dataList[0].isEmpty()) {
            return null
        }

        val fund = App.instance.db.fundDao().get(fundCode)

        var exceptWorth = "--"
        var exceptGrowthWorth = "--"
        var exceptGrowthPercent = "--"
        val exceptWorthDate = dataList[9] + " " + dataList[10]

        try {
            val formatter = when (dataList[10].split(":").size) {
                3 -> "yyyy-MM-dd HH:mm:ss"
                2 -> "yyyy-MM-dd HH:mm"
                else -> "yyyy-MM-dd"
            }
            val exceptDate = SimpleDateFormat(formatter, Locale.getDefault()).parse(exceptWorthDate)
            if (DateUtils.isToday(exceptDate?.time ?: 0)) {
                exceptWorth = dataList[7]
                exceptGrowthWorth = dataList[6]
                exceptGrowthPercent = dataList[5]
            }
        } catch (e: Exception) {
            e.printStackTrace()
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