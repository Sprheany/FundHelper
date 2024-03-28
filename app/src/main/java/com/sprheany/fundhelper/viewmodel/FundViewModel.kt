package com.sprheany.fundhelper.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sprheany.fundhelper.models.FundWorth
import com.sprheany.fundhelper.usecase.FundUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class FundViewModel : ViewModel() {

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