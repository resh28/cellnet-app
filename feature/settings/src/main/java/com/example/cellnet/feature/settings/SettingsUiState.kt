package com.example.cellnet.feature.settings

import com.example.cellnet.core.common.model.AppTheme
import com.example.cellnet.core.common.model.User

data class SettingsUiState(
    val userData: User = User(),
    val appTheme: AppTheme = AppTheme.SYSTEM_DEFAULT,
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordError: String = "",
    val newPasswordError: String = "",
    val confirmPasswordError: String = "",
    val isLoading: Boolean = false,
    val showPasswordChangeBottomSheet: Boolean = false,
    val showThemeChangeBottomSheet: Boolean = false,
)
