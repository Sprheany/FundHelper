package com.sprheany.fundhelper.net.services

import retrofit2.http.GET
import retrofit2.http.Query

//const val BASE_URL = "https://www.dayfund.cn/ajs/ajaxdata.shtml?showtype=getfundvalue&fundcode=016463&t=1302"
//const val BASE_URL = "https://www.dayfund.cn/ajs/ajaxdata.shtml?showtype=getstockvalue&stockcode=sh000001,sz399001,sz399006,sh000300,sh000011&t=1304"
const val DAY_FUND_BASE_URL = "https://www.dayfund.cn/"

interface DayFundService {

    @GET("${DAY_FUND_BASE_URL}ajs/ajaxdata.shtml?showtype=getfundvalue")
    suspend fun getFundValue(@Query("fundcode") fundCode: String): String
}