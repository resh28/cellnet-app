package com.example.cellnet.feature.authentication

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusManager
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.example.cellnet.core.common.Util
import com.example.cellnet.core.designsystem.appSnackbarHost.AppSnackBarHost
import com.example.cellnet.core.designsystem.outlinedTextFieldWithErrorLabel.OutlinedTextFieldWithErrorLabel

@Composable
internal fun AuthRoute(
    navigateToHomeScreen: (NavOptions?) -> Unit,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val authUiState by authViewModel.uiState.collectAsState()

    AuthScreen(modifier = modifier, authViewModel = authViewModel, authUiState = authUiState, navigateToHomeScreen = navigateToHomeScreen)
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
internal fun AuthScreen(
    modifier: Modifier,
    navigateToHomeScreen: (NavOptions?) -> Unit,
    authViewModel: AuthViewModel,
    authUiState: AuthUiState
) {
    val context = LocalContext.current
    val focusManager = LocalFocusManager.current

    val snackbarHostState = remember { SnackbarHostState() }
    val snackBarNotificationFlow by Util.getSnackbarFlow().collectAsStateWithLifecycle()

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
        Column(
            modifier = modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectTapGestures(
                        onTap = {
                            focusManager.clearFocus()
                        }
                    )
                }
                .background(MaterialTheme.colorScheme.background)
                .padding(horizontal = 40.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (authUiState.isSignIn)
                SignInScreen(
                    modifier = modifier,
                    authViewModel = authViewModel,
                    authUiState = authUiState,
                    navigateToHomeScreen = navigateToHomeScreen,
                    context = context,
                    focusManager = focusManager
                )
            else SignUpScreen(
                modifier = modifier,
                authViewModel = authViewModel,
                authUiState = authUiState,
                navigateToHomeScreen = navigateToHomeScreen,
                context = context,
                focusManager = focusManager
            )
        }
    }
}

@Composable
internal fun SignInScreen(
    modifier: Modifier,
    navigateToHomeScreen: (NavOptions?) -> Unit,
    authViewModel: AuthViewModel,
    authUiState: AuthUiState,
    context: Context,
    focusManager: FocusManager
) {
    Image(
        modifier = Modifier
            .height(200.dp)
            .padding(bottom = 50.dp),
        painter = painterResource(id = com.example.cellnet.core.common.R.drawable.cellnetlogo),
        contentDescription = "Cellnet Logo",
        contentScale = ContentScale.Fit,
    )

    OutlinedTextFieldWithErrorLabel(
        value = authUiState.email,
        labelText = "E-mail",
        onValueChange = { authViewModel.updateEmail(it) },
        focusManager = focusManager,
        errorMsg = authUiState.emailError,
        keyboardType = KeyboardType.Email,
        validateOnFocusChange = { authViewModel.validateOnFocusChange() },
        modifier = modifier.fillMaxWidth(),
        isTextVisible = true
    )
    OutlinedTextFieldWithErrorLabel(
        value = authUiState.password,
        labelText = "Password",
        onValueChange = { authViewModel.updatePassword(it) },
        focusManager = focusManager,
        errorMsg = authUiState.passwordError,
        keyboardType = KeyboardType.Password,
        validateOnFocusChange = { authViewModel.validateOnFocusChange() },
        modifier = modifier.fillMaxWidth(),
        isTextVisible = false
    )

    Button(
        modifier = modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        onClick = {
            authViewModel.updateIsLoading(true)
            authViewModel.userSignIn(navigateToHomeScreen, context)
                  },
        enabled = !authUiState.isLoading
    ) {
        Text(text = "Log in")
        if (authUiState.isLoading)
            CircularProgressIndicator(
                modifier = modifier
                    .size(24.dp)
                    .padding(start = 5.dp),
                strokeWidth = 2.dp,
                color = Color.Gray
            )
    }
    Row(
        modifier = modifier
            .padding(top = 6.dp)
    ) {
        Text(
            text = "Don't have an account? ",
            fontSize = 14.sp,
        )
        Text(
            text = "Sign up",
            modifier = Modifier
                .clickable {
                    authViewModel.toggleAuthScreen()
                },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
internal fun SignUpScreen(
    modifier: Modifier,
    navigateToHomeScreen: (NavOptions?) -> Unit,
    authViewModel: AuthViewModel,
    authUiState: AuthUiState,
    context: Context,
    focusManager: FocusManager
) {
    Image(
        modifier = Modifier
            .height(200.dp)
            .padding(bottom = 50.dp),
        painter = painterResource(id = com.example.cellnet.core.common.R.drawable.cellnetlogo),
        contentDescription = "Cellnet Logo",
        contentScale = ContentScale.Fit,
    )

    OutlinedTextFieldWithErrorLabel(
        value = authUiState.firstName,
        labelText = "First Name",
        onValueChange = { authViewModel.updateFirstName(it) },
        focusManager = focusManager,
        errorMsg = authUiState.firstNameError,
        keyboardType = KeyboardType.Text,
        validateOnFocusChange = { authViewModel.validateOnFocusChange() },
        modifier = modifier.fillMaxWidth(),
        isTextVisible = true
    )
    OutlinedTextFieldWithErrorLabel(
        value = authUiState.lastName,
        labelText = "Last Name",
        onValueChange = { authViewModel.updateLastName(it) },
        focusManager = focusManager,
        errorMsg = authUiState.lastNameError,
        keyboardType = KeyboardType.Text,
        validateOnFocusChange = { authViewModel.validateOnFocusChange() },
        modifier = modifier.fillMaxWidth(),
        isTextVisible = true
    )
    OutlinedTextFieldWithErrorLabel(
        value = authUiState.email,
        labelText = "E-mail",
        onValueChange = { authViewModel.updateEmail(it) },
        focusManager = focusManager,
        errorMsg = authUiState.emailError,
        keyboardType = KeyboardType.Email,
        validateOnFocusChange = { authViewModel.validateOnFocusChange() },
        modifier = modifier.fillMaxWidth(),
        isTextVisible = true
    )
    OutlinedTextFieldWithErrorLabel(
        value = authUiState.password,
        labelText = "Password",
        onValueChange = { authViewModel.updatePassword(it) },
        focusManager = focusManager,
        errorMsg = authUiState.passwordError,
        keyboardType = KeyboardType.Password,
        validateOnFocusChange = { authViewModel.validateOnFocusChange() },
        modifier = modifier.fillMaxWidth(),
        isTextVisible = false
    )
    OutlinedTextFieldWithErrorLabel(
        value = authUiState.confirmPassword,
        labelText = "Confirm Password",
        onValueChange = { authViewModel.updateConfirmPassword(it) },
        focusManager = focusManager,
        errorMsg = authUiState.confirmPasswordError,
        keyboardType = KeyboardType.Password,
        validateOnFocusChange = { authViewModel.validateOnFocusChange() },
        modifier = modifier.fillMaxWidth(),
        isTextVisible = false
    )
    Button(
        modifier = modifier
            .padding(top = 20.dp)
            .fillMaxWidth(),
        onClick = {
            authViewModel.updateIsLoading(true)
            authViewModel.userSignUp(navigateToHomeScreen, context)
                  },
        enabled = !authUiState.isLoading
    ) {
        Text(text = "Sign up")
        if (authUiState.isLoading)
            CircularProgressIndicator(
                modifier = modifier
                    .size(24.dp)
                    .padding(start = 5.dp),
                strokeWidth = 2.dp,
                color = Color.Gray
            )
    }
    Row(
        modifier = modifier
            .padding(top = 6.dp)
    ) {
        Text(
            text = "Already have an account? ",
            fontSize = 14.sp,
        )
        Text(
            text = "Log In",
            modifier = Modifier
                .clickable {
                    authViewModel.toggleAuthScreen()
                },
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.primary,
        )
    }
}