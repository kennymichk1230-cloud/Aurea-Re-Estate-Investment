package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.CryptoToken
import com.example.ui.MainViewModel
import com.example.ui.components.FintechDonutChart
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.components.PremiumInputField
import com.example.ui.theme.*

@Composable
fun CryptoScreen(viewModel: MainViewModel) {
    val cryptoPortfolio by viewModel.cryptoPortfolio.collectAsState()
    val userState by viewModel.currentUser.collectAsState()

    var selectedToken by remember { mutableStateOf<CryptoToken?>(null) }
    var tradeIsBuy by remember { mutableStateOf(true) }
    var coinAmount by remember { mutableStateOf("") }

    // Computations
    val totalCryptoValue = cryptoPortfolio.sumOf { it.balance * it.livePrice }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "LIQUID ASSETS DESK",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DeepGreen,
            letterSpacing = 1.sp
        )
        Text(
            text = "Crypto Trading",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Portfolio summary card
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(20.dp)) {
                Text("TOTAL LIQUID CRYPTO WALLET", fontSize = 10.sp, color = DeepGreen, fontWeight = FontWeight.Bold)
                Text("₦${String.format("%,.2f", totalCryptoValue)}", fontSize = 28.sp, fontWeight = FontWeight.Black, color = TextPrimary)

                Spacer(modifier = Modifier.height(16.dp))

                // Multi-allocation chart
                FintechDonutChart(
                    allocations = cryptoPortfolio.map { it.symbol to (it.balance * it.livePrice) }
                        .filter { it.second > 0 }
                        .ifEmpty { listOf("BTC" to 1.0) },
                    colors = listOf(GoldPrimary, DeepGreenLight, Color(0xFFE5C158))
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "Live Market Price Trackers",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        // Tokens List
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            items(cryptoPortfolio) { token ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(14.dp))
                        .clickable { selectedToken = token }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .clip(CircleShape)
                                .background(DeepGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = token.symbol.take(1),
                                color = GoldPrimary,
                                fontWeight = FontWeight.Black,
                                fontSize = 16.sp
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(token.name, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
                            Text("${token.balance} ${token.symbol}", color = TextSecondary, fontSize = 12.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text("₦${String.format("%,.2f", token.livePrice)}", color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Black)
                        val changeColor = if (token.priceChangePercent24h >= 0) GoldPrimary else ErrorRed
                        val changeSign = if (token.priceChangePercent24h >= 0) "+" else ""
                        Text(
                            text = "$changeSign${token.priceChangePercent24h}%",
                            color = changeColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }

    // --- CRYPTO BUY/SELL TRADE MODAL ---
    if (selectedToken != null) {
        val token = selectedToken!!
        AlertDialog(
            onDismissRequest = { selectedToken = null },
            containerColor = DarkSurface,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .clip(CircleShape)
                            .background(DeepGreen),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(token.symbol, fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.Black)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text("Trade ${token.name}", color = TextPrimary, fontWeight = FontWeight.Bold)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = { tradeIsBuy = true },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (tradeIsBuy) GoldPrimary else DarkSurfaceVariant,
                                contentColor = if (tradeIsBuy) DarkBackground else TextPrimary
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("BUY", fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { tradeIsBuy = false },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (!tradeIsBuy) GoldPrimary else DarkSurfaceVariant,
                                contentColor = if (!tradeIsBuy) DarkBackground else TextPrimary
                            ),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("SELL", fontWeight = FontWeight.Bold)
                        }
                    }

                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Exchange Rate:", color = TextSecondary, fontSize = 12.sp)
                        Text("₦${String.format("%,.2f", token.livePrice)} / ${token.symbol}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(if (tradeIsBuy) "Available Cash:" else "Available Holdings:", color = TextSecondary, fontSize = 12.sp)
                        Text(
                            text = if (tradeIsBuy) "₦${String.format("%,.2f", userState?.walletBalance ?: 0.0)}" else "${token.balance} ${token.symbol}",
                            color = GoldPrimary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    PremiumInputField(
                        value = coinAmount,
                        onValueChange = { coinAmount = it },
                        label = "AMOUNT IN COINS (${token.symbol})",
                        placeholder = "e.g., 0.05"
                    )

                    val coins = coinAmount.toDoubleOrNull() ?: 0.0
                    val totalCost = coins * token.livePrice
                    if (coins > 0) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .background(DarkSurfaceVariant)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(if (tradeIsBuy) "Total Cost:" else "Total Value:", color = TextSecondary, fontSize = 13.sp)
                            Text("₦${String.format("%,.2f", totalCost)}", color = GoldPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        }
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val coins = coinAmount.toDoubleOrNull() ?: 0.0
                        if (coins > 0) {
                            viewModel.tradeCrypto(token.symbol, tradeIsBuy, coins, token.livePrice)
                            coinAmount = ""
                            selectedToken = null
                        }
                    }
                ) {
                    Text(if (tradeIsBuy) "Execute Buy" else "Execute Sell", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedToken = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
