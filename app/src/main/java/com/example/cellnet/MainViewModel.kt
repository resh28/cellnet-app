package com.example.cellnet

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.cellnet.core.common.model.AppTheme
import com.example.cellnet.core.data.iRepository.LocalStorageRepository
import com.example.cellnet.feature.home.HomeUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val localStorageRepository: LocalStorageRepository,
): ViewModel() {
    private val _appTheme = MutableStateFlow(AppTheme.SYSTEM_DEFAULT)
    val appTheme: StateFlow<AppTheme> = _appTheme.asStateFlow()

    init {
        viewModelScope.launch {
            localStorageRepository.getAppTheme().collect { appTheme ->
                _appTheme.update {
                    appTheme
                }
            }
        }
    }
}