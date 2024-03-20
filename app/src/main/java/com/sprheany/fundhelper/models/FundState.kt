package com.sprheany.fundhelper.models

import kotlinx.serialization.Serializable

@Serializable
sealed interface FundState {
    @Serializable
    data object Loading : FundState

    @Serializable
    data class Success(val fundWorth: List<FundWorth>) : FundState

    @Serializable
    data class Error(val message: String) : FundState
}