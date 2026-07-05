package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Agriculture
import androidx.compose.material.icons.filled.CandlestickChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material3.Icon
import androidx.compose.ui.unit.dp
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.example.ui.MainViewModel
import com.example.ui.screens.AdminDashboardScreen
import com.example.ui.screens.ChatCoachScreen
import com.example.ui.screens.CryptoScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.FinancialServicesScreen
import com.example.ui.screens.InvestmentsScreen
import com.example.ui.screens.KYCScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.theme.BorderColor
import com.example.ui.theme.DarkBackground
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.DarkSurfaceVariant
import com.example.ui.theme.DeepGreen
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.flow.collectLatest

class MainActivity : ComponentActivity() {
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                var isLoggedIn by remember { mutableStateOf(false) }

                // Collect and show Toast alerts from ViewModel
                LaunchedEffect(Unit) {
                    viewModel.toastMessage.collectLatest { msg ->
                        Toast.makeText(this@MainActivity, msg, Toast.LENGTH_LONG).show()
                    }
                }

                // Collect and open secure payment link URLs from ViewModel
                LaunchedEffect(Unit) {
                    viewModel.openUrlEvent.collectLatest { url ->
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                            this@MainActivity.startActivity(intent)
                        } catch (e: Exception) {
                            Toast.makeText(this@MainActivity, "Failed to open link: ${e.message}", Toast.LENGTH_LONG).show()
                        }
                    }
                }

                if (!isLoggedIn) {
                    LoginScreen(viewModel = viewModel) {
                        isLoggedIn = true
                    }
                } else {
                    MainAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = DarkBackground,
        bottomBar = {
            // Hide bottom bar if on Admin, Login, or KYC nodes
            if (currentScreen != "admin" && currentScreen != "kyc") {
                NavigationBar(
                    containerColor = DarkSurface,
                    tonalElevation = 8.dp
                ) {
                    val navItems = listOf(
                        NavItem("dashboard", "Home", Icons.Default.Home),
                        NavItem("investments", "Invest", Icons.Default.Agriculture),
                        NavItem("services", "Credit", Icons.Default.ReceiptLong),
                        NavItem("crypto", "Crypto", Icons.Default.CandlestickChart),
                        NavItem("coach", "AI Coach", Icons.Default.AutoAwesome)
                    )

                    navItems.forEach { item ->
                        val selected = currentScreen == item.screenId
                        NavigationBarItem(
                            selected = selected,
                            onClick = { viewModel.navigateTo(item.screenId) },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = DeepGreen,
                                unselectedIconColor = TextSecondary,
                                selectedTextColor = DeepGreen,
                                unselectedTextColor = TextSecondary,
                                indicatorColor = DarkSurfaceVariant
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    fadeIn().togetherWith(fadeOut())
                },
                label = "ScreenTransitions"
            ) { screen ->
                when (screen) {
                    "dashboard" -> DashboardScreen(viewModel = viewModel)
                    "investments" -> InvestmentsScreen(viewModel = viewModel)
                    "services" -> FinancialServicesScreen(viewModel = viewModel)
                    "crypto" -> CryptoScreen(viewModel = viewModel)
                    "coach" -> ChatCoachScreen(viewModel = viewModel)
                    "admin" -> AdminDashboardScreen(viewModel = viewModel)
                    "kyc" -> KYCScreen(viewModel = viewModel)
                    else -> DashboardScreen(viewModel = viewModel)
                }
            }
        }
    }
}

data class NavItem(val screenId: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
