package com.example.cellnet

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.cellnet.core.designsystem.theme.CellnetTheme
import com.example.cellnet.navigation.CellnetNavHost
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            CellnetTheme {
                CellnetNavHost(
                    navController = rememberNavController()
                )
            }
        }
    }
}
