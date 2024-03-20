package com.sprheany.fundhelper.net.services

import retrofit2.http.GET
import retrofit2.http.Path

const val EAST_MONEY_BASE_URL = "https://fund.eastmoney.com/"
const val EAST_MONEY_BASE_URL_1 = "https://fundgz.1234567.com.cn/"

interface EastMoneyService {

    @GET("${EAST_MONEY_BASE_URL_1}js/{fundcode}.js")
    suspend fun getFundValue(@Path("fundcode") fundCode: String): String

    @GET("${EAST_MONEY_BASE_URL}js/fundcode_search.js")
    suspend fun getAllFund(): String
}