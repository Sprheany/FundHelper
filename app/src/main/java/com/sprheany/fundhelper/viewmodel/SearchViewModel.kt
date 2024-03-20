package com.sprheany.fundhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sprheany.fundhelper.usecase.FundUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    fun searchFunds(text: String) =
        FundUseCase.searchFunds(text)
            .flowOn(Dispatchers.IO)

    fun collectFund(fundCode: String, isCollect: Boolean) {
        viewModelScope.launch {
            if (isCollect) {
                FundUseCase.collectFund(fundCode)
            } else {
                FundUseCase.removeCollectionFund(fundCode)
            }
        }
    }
}