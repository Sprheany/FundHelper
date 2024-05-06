package com.sprheany.fundhelper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sprheany.fundhelper.data.FundUseCase
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {
    init {
        refreshFundWorth()
    }

    fun refreshFundWorth() {
        viewModelScope.launch {
            FundUseCase.refreshFundWorth()
        }
    }

    fun onSwiped(fromCode: String, toCode: String) {
        viewModelScope.launch {
            FundUseCase.swipeFund(fromCode, toCode)
        }
    }

}