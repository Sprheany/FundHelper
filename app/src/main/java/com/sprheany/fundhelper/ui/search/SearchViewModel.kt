package com.sprheany.fundhelper.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sprheany.fundhelper.data.FundUseCase
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