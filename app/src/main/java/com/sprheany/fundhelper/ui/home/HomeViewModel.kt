package com.sprheany.fundhelper.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sprheany.fundhelper.data.FundUseCase
import com.sprheany.fundhelper.models.FundWorth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel : ViewModel() {

    private val _fundWorthFlow = MutableStateFlow<List<FundWorth>>(emptyList())

    val fundWorthFlow = _fundWorthFlow.asStateFlow()

    init {
        refreshFundWorth()
    }

    fun refreshFundWorth() {
        viewModelScope.launch {
            FundUseCase.collectionFundWorthFlow.collect {
                _fundWorthFlow.value = it
            }
        }
    }

    fun onSwiped(fromIndex: Int, toIndex: Int) {
        viewModelScope.launch {
            fundWorthFlow.value.run {
                val fromCode = get(fromIndex).code
                val toCode = get(toIndex).code

                FundUseCase.swipeFund(fromCode, toCode)
            }
        }
    }

}