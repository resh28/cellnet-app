package com.example.cellnet.feature.settings

import com.example.cellnet.core.common.model.AppTheme
import com.example.cellnet.core.common.model.User

data class SettingsUiState(
    val userData: User = User(),
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT
)
