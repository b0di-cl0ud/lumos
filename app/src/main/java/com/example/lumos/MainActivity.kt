package com.example.lumos

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.lumos.ui.screens.Welcome
import com.example.lumos.ui.screens.scan.Scan
import com.example.lumos.ui.screens.scan.ScanViewModel
import com.example.lumos.ui.theme.LumosTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LumosTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    val navController = rememberNavController()
                    NavHost(
                        modifier = Modifier.padding(innerPadding),
                        navController = navController,
                        startDestination = "welcome"
                    ) {
                        composable("welcome") {
                            Welcome(
                                onScanClick = {
                                    navController.navigate("scan")
                                },
                            )
                        }
                        composable("scan") {
                            Scan(
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}