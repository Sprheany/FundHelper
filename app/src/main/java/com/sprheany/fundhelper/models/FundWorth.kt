package com.sprheany.fundhelper.models

import kotlinx.serialization.Serializable

/**
 * [2023-07-31, 1.3160, 1.3160, -0.0050, -0.38%, 3.00%, 0.0395, 1.3555, 1.3210, 2023-08-01, 15:00:00]
 * [2023-07-31, 1.3646, 1.3646, 0.0271, 2.03%, 0.00%, 0.0000, 0.0000, 1.3375, 0000-00-00, 15:00:00]
 */
@Serializable
data class FundWorth(
    val code: String,
    val name: String,

    val netWorth: String,
    val worthDate: String,

    val exceptWorth: String,
    val exceptGrowthWorth: String,
    val exceptGrowthPercent: String,
    val exceptWorthDate: String,

    val state: Int = if (exceptGrowthWorth.toDoubleOrNull() == null) FUND_STATE_NONE else
        if (exceptGrowthWorth.toDouble() >= 0) FUND_STATE_UP else FUND_STATE_DOWN,

    val growthPercent: String = if (state == FUND_STATE_UP)
        "+${exceptGrowthPercent}" else exceptGrowthPercent,
)

const val FUND_STATE_NONE = -1
const val FUND_STATE_UP = 1
const val FUND_STATE_DOWN = 0
