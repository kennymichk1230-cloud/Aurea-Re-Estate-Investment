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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.theme.*

import com.example.ui.components.PremiumInputField

@Composable
fun AdminDashboardScreen(viewModel: MainViewModel) {
    val allUsers by viewModel.allUsers.collectAsState()
    val loans by viewModel.loans.collectAsState()
    val investments by viewModel.investments.collectAsState()
    val investmentProjects by viewModel.investmentProjects.collectAsState()

    val pendingKycUsers = allUsers.filter { it.kycStatus == "PENDING" }
    val pendingLoans = loans.filter { it.status == "PENDING" }

    // Analytics (NGN-based)
    val totalAum = investments.sumOf { it.amountInvested } + allUsers.sumOf { it.walletBalance }
    val outstandingLoans = loans.filter { it.status == "ACTIVE" }.sumOf { it.remainingAmount }

    // Add Project Form State
    var selectedType by remember { mutableStateOf("LAND") } // LAND, RENTAL_HOUSE, FARMING, FOOD
    var projName by remember { mutableStateOf("") }
    var projLocation by remember { mutableStateOf("") }
    var projRoi by remember { mutableStateOf("") }
    var projDuration by remember { mutableStateOf("") }
    var projMinInvest by remember { mutableStateOf("") }
    var projDesc by remember { mutableStateOf("") }

    val typesList = listOf("LAND", "RENTAL_HOUSE", "FARMING", "FOOD")

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 120.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = { viewModel.navigateTo("dashboard") },
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(DarkSurface)
                ) {
                    Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = GoldPrimary)
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column {
                    Text(
                        text = "Compliance Admin Node",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Set rules, approve disbursements, and manage investment assets.",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }
            }
        }

        // Metrics Grid (Naira-based)
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("TOTAL ASSETS UNDER MGMT", fontSize = 9.sp, color = TextSecondary)
                        Text("₦${String.format("%,.2f", totalAum)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = GoldPrimary)
                    }
                }
                GlassCard(modifier = Modifier.weight(1f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("LOANS OUTSTANDING", fontSize = 9.sp, color = TextSecondary)
                        Text("₦${String.format("%,.2f", outstandingLoans)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                    }
                }
            }
        }

        // Pending KYC Panel Header
        item {
            Text(
                text = "Pending KYC Verifications (${pendingKycUsers.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (pendingKycUsers.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("All client verifications are fully up to date.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            items(pendingKycUsers) { user ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(user.fullName, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text("Email: ${user.email}", fontSize = 11.sp, color = TextSecondary)
                                Text("Doc: ${user.kycDocumentType}", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                            }
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(
                                    onClick = { viewModel.adminApproveKYC(user.email) },
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepGreen, contentColor = GoldPrimary),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Approve", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                                Button(
                                    onClick = { viewModel.adminRejectKYC(user.email) },
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF42151B), contentColor = ErrorRed),
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
                                ) {
                                    Text("Reject", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }

        // Pending Loan Approvals Header
        item {
            Text(
                text = "Pending Credit Disbursements (${pendingLoans.size})",
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        if (pendingLoans.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No loan applications pending approval.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            items(pendingLoans) { loan ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("${loan.type} LOAN REQUEST", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                                Text("Principal: ₦${String.format("%,.2f", loan.amountRequested)}", fontSize = 16.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                                Text("Applicant: ${loan.userEmail}", fontSize = 11.sp, color = TextSecondary)
                                Text("Credit Score: ${loan.creditScore} (Confidence High)", fontSize = 11.sp, color = DeepGreenLight, fontWeight = FontWeight.Bold)
                            }
                            Button(
                                onClick = { viewModel.adminApproveLoan(loan) },
                                colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("Disburse", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Dynamic Investment Projects Manager
        item {
            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = "Active Investment Opportunities (${investmentProjects.size})",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
        }

        if (investmentProjects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(12.dp))
                        .background(DarkSurface)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No dynamic investment projects in database.", color = TextSecondary, fontSize = 12.sp)
                }
            }
        } else {
            items(investmentProjects) { project ->
                GlassCard(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(DeepGreen)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(project.type, fontSize = 9.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(project.location, fontSize = 11.sp, color = TextSecondary)
                                }
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(project.name, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                                Text(
                                    "Min Investment: ₦${String.format("%,.0f", project.minInvestment)} | ROI: ${project.roiRate}% | Duration: ${project.durationMonths}m",
                                    fontSize = 11.sp,
                                    color = GoldPrimary,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            IconButton(
                                onClick = { viewModel.adminDeleteInvestmentProject(project) },
                                modifier = Modifier
                                    .clip(CircleShape)
                                    .background(Color(0xFF42151B))
                            ) {
                                Icon(Icons.Default.Delete, contentDescription = "Delete", tint = ErrorRed)
                            }
                        }
                    }
                }
            }
        }

        // Create New Project Form
        item {
            Divider(color = BorderColor, modifier = Modifier.padding(vertical = 12.dp))
            Text(
                text = "Create Investment Program",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Instantly list real estate, apartments, or agro partnerships.",
                fontSize = 11.sp,
                color = TextSecondary,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Select Type Buttons
            Text("Opportunity Category", fontSize = 12.sp, color = TextSecondary, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(6.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                typesList.forEach { type ->
                    val isSel = selectedType == type
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(8.dp))
                            .background(if (isSel) DeepGreen else DarkSurface)
                            .border(1.dp, if (isSel) GoldPrimary else BorderColor, RoundedCornerShape(8.dp))
                            .clickable { selectedType = type }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = when (type) {
                                "LAND" -> "Land"
                                "RENTAL_HOUSE" -> "Rental"
                                "FARMING" -> "Farm"
                                "FOOD" -> "Food Co"
                                else -> type
                            },
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSel) Color.White else TextSecondary
                        )
                    }
                }
            }
        }

        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                PremiumInputField(
                    value = projName,
                    onValueChange = { projName = it },
                    label = "PROJECT NAME",
                    placeholder = "e.g. Lekki Palms Phase 2"
                )
                PremiumInputField(
                    value = projLocation,
                    onValueChange = { projLocation = it },
                    label = "LOCATION",
                    placeholder = "e.g. Ibeju-Lekki, Lagos"
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumInputField(
                            value = projRoi,
                            onValueChange = { projRoi = it },
                            label = "ROI RATE % (ANNUAL)",
                            placeholder = "e.g. 24.5"
                        )
                    }
                    Box(modifier = Modifier.weight(1f)) {
                        PremiumInputField(
                            value = projDuration,
                            onValueChange = { projDuration = it },
                            label = "DURATION (MONTHS)",
                            placeholder = "e.g. 12"
                        )
                    }
                }
                PremiumInputField(
                    value = projMinInvest,
                    onValueChange = { projMinInvest = it },
                    label = "MINIMUM INVESTMENT (₦)",
                    placeholder = "e.g. 50000"
                )
                PremiumInputField(
                    value = projDesc,
                    onValueChange = { projDesc = it },
                    label = "PROJECT DETAILED DESCRIPTION",
                    placeholder = "Highlight ownership titles, secure structures, and yields..."
                )

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = {
                        val roi = projRoi.toDoubleOrNull() ?: 0.0
                        val dur = projDuration.toIntOrNull() ?: 0
                        val minInv = projMinInvest.toDoubleOrNull() ?: 0.0

                        if (projName.isNotBlank() && projLocation.isNotBlank() && roi > 0 && dur > 0 && minInv > 0 && projDesc.isNotBlank()) {
                            viewModel.adminAddInvestmentProject(
                                type = selectedType,
                                name = projName,
                                location = projLocation,
                                roiRate = roi,
                                durationMonths = dur,
                                minInvestment = minInv,
                                description = projDesc
                            )
                            // Clear form
                            projName = ""
                            projLocation = ""
                            projRoi = ""
                            projDuration = ""
                            projMinInvest = ""
                            projDesc = ""
                        } else {
                            // Validation issue managed by general empty check
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(vertical = 14.dp)
                ) {
                    Text("Add Investment Program", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}
