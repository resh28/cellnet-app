package com.example.cellnet.feature.settings

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.common.model.AppTheme
import com.example.cellnet.core.designsystem.appSnackbarHost.AppSnackBarHost
import com.example.cellnet.core.designsystem.outlinedTextFieldWithErrorLabel.OutlinedTextFieldWithErrorLabel

@Composable
internal fun SettingsRoute(
    modifier: Modifier = Modifier,
    navigateToAuthScreen: (NavOptions?) -> Unit,
    settingsViewModel: SettingsViewModel = hiltViewModel()
) {
    SettingsScreen(modifier = modifier, settingsViewModel= settingsViewModel, navigateToAuthScreen = navigateToAuthScreen)
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun SettingsScreen(
    modifier: Modifier,
    settingsViewModel: SettingsViewModel,
    navigateToAuthScreen: (NavOptions?) -> Unit,
) {
    val settingsUiState by settingsViewModel.uiState.collectAsState()
    
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarNotificationFlow by Util.getSnackbarFlow().collectAsStateWithLifecycle()

    val sheetState = rememberModalBottomSheetState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = snackBarNotificationFlow) {
        if (snackBarNotificationFlow.second != "") {
            snackbarHostState.showSnackbar(
                message = snackBarNotificationFlow.second,
                actionLabel = "",
                duration = SnackbarDuration.Short,
            )
        }
    }

    Scaffold(
        snackbarHost = {
            AppSnackBarHost(
                snackbarHostState = snackbarHostState,
                modifier = modifier,
                componentHeight = remember { mutableStateOf(20.dp) },
                infoLevel = snackBarNotificationFlow.first
            )
        },
    )
    {
        if (settingsUiState.showThemeChangeBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    settingsViewModel.updateShowThemeChangeBottomSheet(false)
                },
                sheetState = sheetState
            ) {
                // Sheet content
//                Button(onClick = {
//                    scope.launch { sheetState.hide() }.invokeOnCompletion {
//                        if (!sheetState.isVisible) {
//                            showBottomSheet = false
//                        }
//                    }
//                }) {
//                    Text("Hide bottom sheet")
//                }
                Column(
                    modifier = modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Select application theme",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = modifier
                            .padding(bottom = 10.dp)
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .padding(bottom = 6.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Use device theme")
                        RadioButton(
                            selected = settingsUiState.appTheme == AppTheme.SYSTEM_DEFAULT,
                            onClick = { settingsViewModel.updateApplicationTheme(AppTheme.SYSTEM_DEFAULT) }
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .padding(bottom = 6.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Light mode")
                        RadioButton(
                            selected = settingsUiState.appTheme == AppTheme.LIGHT,
                            onClick = { settingsViewModel.updateApplicationTheme(AppTheme.LIGHT) }
                        )
                    }
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = modifier
                            .padding(bottom = 6.dp)
                            .fillMaxWidth()
                    ) {
                        Text(text = "Dark mode")
                        RadioButton(
                            selected = settingsUiState.appTheme == AppTheme.DARK,
                            onClick = { settingsViewModel.updateApplicationTheme(AppTheme.DARK) }
                        )
                    }
                }
            }
        }
        if (settingsUiState.showPasswordChangeBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    settingsViewModel.updateShowPasswordChangeBottomSheet(false)
                },
                sheetState = sheetState
            ) {
                Column(
                    modifier = modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = "Change password",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = modifier
                            .padding(bottom = 10.dp)
                    )
                    OutlinedTextFieldWithErrorLabel(
                        value = settingsUiState.currentPassword,
                        labelText = "Current password",
                        onValueChange = { settingsViewModel.updateCurrentPassword(it) },
                        focusManager = focusManager,
                        errorMsg = settingsUiState.currentPasswordError,
                        keyboardType = KeyboardType.Password,
                        validateOnFocusChange = { settingsViewModel.validateOnFocusChange() },
                        modifier = modifier.fillMaxWidth(),
                        isTextVisible = false
                    )
                    OutlinedTextFieldWithErrorLabel(
                        value = settingsUiState.newPassword,
                        labelText = "New password",
                        onValueChange = { settingsViewModel.updateNewPassword(it) },
                        focusManager = focusManager,
                        errorMsg = settingsUiState.newPasswordError,
                        keyboardType = KeyboardType.Password,
                        validateOnFocusChange = { settingsViewModel.validateOnFocusChange() },
                        modifier = modifier.fillMaxWidth(),
                        isTextVisible = false
                    )
                    OutlinedTextFieldWithErrorLabel(
                        value = settingsUiState.confirmPassword,
                        labelText = "Confirm new password",
                        onValueChange = { settingsViewModel.updateConfirmPassword(it) },
                        focusManager = focusManager,
                        errorMsg = settingsUiState.confirmPasswordError,
                        keyboardType = KeyboardType.Password,
                        validateOnFocusChange = { settingsViewModel.validateOnFocusChange() },
                        modifier = modifier.fillMaxWidth(),
                        isTextVisible = false
                    )
                    Button(
                        modifier = modifier
                            .padding(top = 20.dp, bottom = 20.dp)
                            .fillMaxWidth(),
                        onClick = {
                            settingsViewModel.updateIsLoading(true)
                            settingsViewModel.updatePassword()
                        },
                        enabled = !settingsUiState.isLoading
                    ) {
                        Text(text = "Update password")
                        if (settingsUiState.isLoading)
                            CircularProgressIndicator(
                                modifier = modifier
                                    .size(24.dp)
                                    .padding(start = 5.dp),
                                strokeWidth = 2.dp,
                                color = Color.Gray
                            )
                    }

                }
            }
        }

        Column(
            modifier = modifier
                .padding(20.dp),
        ) {
            ElevatedCard(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Column(
                    modifier = modifier
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = "",
                            modifier = modifier
                                .size(80.dp)
                                .padding(end = 14.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "${settingsUiState.userData.firstName} ${settingsUiState.userData.lastName}",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = settingsUiState.userData.email,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }
                    }
                }
            }

            ElevatedCard(
                onClick = {
                    settingsViewModel.updateShowThemeChangeBottomSheet(true)
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Column(
                    modifier = modifier
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.LightMode,
                            contentDescription = "",
                            modifier = modifier
                                .padding(end = 10.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Select application theme",
                            )
                        }
                    }
                }
            }

            ElevatedCard(
                onClick = {
                    settingsViewModel.updateShowPasswordChangeBottomSheet(true)
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Column(
                    modifier = modifier
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Outlined.Lock,
                            contentDescription = "",
                            modifier = modifier
                                .padding(end = 10.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Change password",
                            )
                        }
                    }
                }
            }

            ElevatedCard(
                onClick = {
                    settingsViewModel.userSignOut()
                    navigateToAuthScreen(
                        navOptions {
                            launchSingleTop = true
                        }
                    )
                },
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer,
                    contentColor = MaterialTheme.colorScheme.onTertiary
                ),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 24.dp),
            ) {
                Column(
                    modifier = modifier
                        .padding(14.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Logout,
                            contentDescription = "",
                            modifier = modifier
                                .padding(end = 10.dp),
                            tint = MaterialTheme.colorScheme.primary
                        )
                        Column {
                            Text(
                                text = "Log out",
                            )
                        }
                    }
                }
            }
        }
    }
}