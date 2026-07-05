package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.components.PremiumInputField
import com.example.ui.theme.*

@Composable
fun InvestmentsScreen(viewModel: MainViewModel) {
    val investmentsState by viewModel.investments.collectAsState()
    val userState by viewModel.currentUser.collectAsState()
    val dynamicProjects by viewModel.investmentProjects.collectAsState()

    var activeTab by remember { mutableStateOf("LAND") } // LAND, RENTAL_HOUSE, FARMING, FOOD

    // State for checkout dialog
    var selectedProject by remember { mutableStateOf<com.example.data.InvestmentProject?>(null) }
    var investAmount by remember { mutableStateOf("") }

    val tabs = listOf(
        TabItem("LAND", "Lands", Icons.Default.Terrain),
        TabItem("RENTAL_HOUSE", "Rentals", Icons.Default.Domain),
        TabItem("FARMING", "Farming", Icons.Default.Agriculture),
        TabItem("FOOD", "Food Co", Icons.Default.LocalPizza)
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
    ) {
        // Title Header
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "PREMIUM INVESTMENT DESK",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DeepGreen,
            letterSpacing = 1.sp
        )
        Text(
            text = "Capital Markets",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Tab Selector Row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(DarkSurface)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            tabs.forEach { tab ->
                val active = activeTab == tab.id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (active) DeepGreen else Color.Transparent)
                        .border(1.dp, if (active) GoldPrimary.copy(alpha = 0.3f) else Color.Transparent, RoundedCornerShape(10.dp))
                        .clickable { activeTab = tab.id }
                        .padding(vertical = 10.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.label,
                            tint = if (active) GoldPrimary else TextSecondary,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = tab.label,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (active) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Quick Active Portfolio Summary for selected tab
        val matchingActive = investmentsState.filter { it.type == activeTab }
        val sumInvested = matchingActive.sumOf { it.amountInvested }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurfaceVariant)
                .padding(14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "YOUR ACTIVE ${activeTab} CAPITAL",
                    fontSize = 9.sp,
                    color = DeepGreen,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "₦${String.format("%,.2f", sumInvested)}",
                    fontSize = 16.sp,
                    color = TextPrimary,
                    fontWeight = FontWeight.Black
                )
            }
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(DeepGreen)
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${matchingActive.size} contracts",
                    color = GoldPrimary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Investment Listings
        val projectList = dynamicProjects.filter { it.type == activeTab && it.isAvailable }

        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(bottom = 100.dp)
        ) {
            if (projectList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "No active investment programs under $activeTab at the moment.",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            } else {
                items(projectList) { proj ->
                    GlassCard(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.Top
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = proj.location,
                                        fontSize = 11.sp,
                                        color = DeepGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = proj.name,
                                        fontSize = 18.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TextPrimary
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(10.dp))
                                        .background(DeepGreen)
                                        .padding(horizontal = 10.dp, vertical = 6.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "${proj.roiRate}% ROI",
                                        color = GoldPrimary,
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = proj.description,
                                fontSize = 12.sp,
                                color = TextSecondary,
                                lineHeight = 16.sp
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text("DURATION", fontSize = 9.sp, color = TextSecondary)
                                    Text("${proj.durationMonths} Months", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Column {
                                    Text("MIN INVEST", fontSize = 9.sp, color = TextSecondary)
                                    Text("₦${String.format("%,.0f", proj.minInvestment)}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { selectedProject = proj },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = GoldPrimary,
                                        contentColor = DarkBackground
                                    ),
                                    shape = RoundedCornerShape(10.dp)
                                ) {
                                    Text("Invest Now", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- PURCHASE / CHECKOUT DIALOG ---
    if (selectedProject != null) {
        val proj = selectedProject!!
        AlertDialog(
            onDismissRequest = { selectedProject = null },
            containerColor = DarkSurface,
            title = { Text("Fractional Acquisition", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        text = "You are investing in: ${proj.name}. Funds are drawn directly from your secure Aurea Cash Wallet.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Expected ROI:", color = TextSecondary, fontSize = 13.sp)
                        Text("${proj.roiRate}% Annually", color = GoldPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Your Liquid Cash:", color = TextSecondary, fontSize = 13.sp)
                        Text("₦${String.format("%,.2f", userState?.walletBalance ?: 0.0)}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }

                    PremiumInputField(
                        value = investAmount,
                        onValueChange = { investAmount = it },
                        label = "INVESTMENT SUM (₦)",
                        placeholder = "Minimum: ₦${proj.minInvestment}"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = investAmount.toDoubleOrNull() ?: 0.0
                        if (amt >= proj.minInvestment) {
                            viewModel.purchaseInvestment(
                                type = proj.type,
                                projectName = proj.name,
                                location = proj.location,
                                amount = amt,
                                roiRate = proj.roiRate,
                                duration = proj.durationMonths,
                                details = proj.description
                            )
                            investAmount = ""
                            selectedProject = null
                        }
                    }
                ) {
                    Text("Confirm Investment", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedProject = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}

data class TabItem(val id: String, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
