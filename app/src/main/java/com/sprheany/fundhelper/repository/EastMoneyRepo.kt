package com.sprheany.fundhelper.repository

import com.sprheany.fundhelper.database.entities.FundEntity
import com.sprheany.fundhelper.models.EastMoneyFund
import com.sprheany.fundhelper.models.FundWorth
import com.sprheany.fundhelper.net.Net
import com.sprheany.fundhelper.net.services.EastMoneyService
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json

class EastMoneyRepo {
    /**
     * jsonpgz({"fundcode":"004235","name":"中欧价值智选混合C","jzrq":"2023-08-01","dwjz":"3.9679","gsz":"3.9542","gszzl":"-0.34","gztime":"2023-08-02 11:28"})
     */
    suspend fun getFundByCode(fundCode: String): FundWorth? {
        val result = try {
            Net.instance.create(EastMoneyService::class.java).getFundValue(fundCode = fundCode)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        val json = Regex("jsonpgz\\((.*)\\)").find(result)?.groupValues?.get(1)
        if (json.isNullOrBlank()) {
            return null
        }
        val data = Json.decodeFromString(EastMoneyFund.serializer(), json)
        return FundWorth(
            code = data.fundcode,
            name = data.name,
            netWorth = data.dwjz,
            worthDate = data.jzrq,
            exceptWorth = data.gsz,
            exceptWorthDate = data.gztime,
            exceptGrowthPercent = "${data.gszzl}%",
            exceptGrowthWorth = String.format(
                "%.4f",
                (data.gsz.toFloatOrNull() ?: 0f) - (data.dwjz.toFloatOrNull() ?: 0f)
            ),
        )
    }

    /**
     * var r = [["000001","HXCZHH","华夏成长混合","混合型-灵活","HUAXIACHENGZHANGHUNHE"],["000002","HXCZHH","华夏成长混合(后端)","混合型-灵活","HUAXIACHENGZHANGHUNHE"]];
     */
    suspend fun requestAllFund(): List<FundEntity> {
        val result = Net.instance.create(EastMoneyService::class.java).getAllFund()
        val json = result.replace("var r = ", "").replace(";", "")
        val strList = Json.decodeFromString(ListSerializer(ListSerializer(String.serializer())), json)
        return strList.map {
            FundEntity(
                code = it[0],
                py = it[1],
                name = it[2],
                type = it[3],
                pinyin = it[4],
            )
        }
    }
}