package com.sprheany.fundhelper.models

import com.sprheany.fundhelper.database.entities.FundEntity
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
    val exceptWorthDate: String
) {
    constructor(fundEntity: FundEntity) : this(
        code = fundEntity.code,
        name = fundEntity.name,
        netWorth = "--",
        worthDate = "",
        exceptWorth = "--",
        exceptGrowthWorth = "--",
        exceptGrowthPercent = "--",
        exceptWorthDate = "",
    )
}

enum class FundGrowthState {
    None,
    Up,
    Down
}

val FundWorth.state: FundGrowthState
    get() = if (exceptGrowthWorth.toDoubleOrNull() == null) FundGrowthState.None else
        if (exceptGrowthWorth.toDouble() >= 0) FundGrowthState.Up else FundGrowthState.Down

val FundWorth.growthPercent: String
    get() = if (state == FundGrowthState.Up) "+${exceptGrowthPercent}" else exceptGrowthPercent

