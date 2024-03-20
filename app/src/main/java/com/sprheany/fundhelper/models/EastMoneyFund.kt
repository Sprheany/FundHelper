package com.sprheany.fundhelper.models

import kotlinx.serialization.Serializable

/**{"fundcode":"004235","name":"中欧价值智选混合C","jzrq":"2023-08-01","dwjz":"3.9679","gsz":"3.9542","gszzl":"-0.34","gztime":"2023-08-02 11:28"}
 */
@Serializable
data class EastMoneyFund(
    val fundcode: String,
    val name: String,
    val jzrq: String,
    val dwjz: String,
    val gsz: String,
    val gszzl: String,
    val gztime: String,
)
