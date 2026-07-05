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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.MainViewModel
import com.example.ui.components.FintechLineChart
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.components.PremiumInputField
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(viewModel: MainViewModel) {
    val currentUser by viewModel.currentUser.collectAsState()
    val transactions by viewModel.transactions.collectAsState()
    val investments by viewModel.investments.collectAsState()
    val loans by viewModel.loans.collectAsState()

    var showDepositDialog by remember { mutableStateOf(false) }
    var showWithdrawDialog by remember { mutableStateOf(false) }
    var showTransferDialog by remember { mutableStateOf(false) }

    var depositAmount by remember { mutableStateOf("") }
    var withdrawAmount by remember { mutableStateOf("") }
    var bankName by remember { mutableStateOf("Chase Bank") }
    var bankAccount by remember { mutableStateOf("") }

    var transferAmount by remember { mutableStateOf("") }
    var recipientEmail by remember { mutableStateOf("") }
    var transferNote by remember { mutableStateOf("") }

    // Computations
    val walletBal = currentUser?.walletBalance ?: 0.0
    val investBal = investments.sumOf { it.amountInvested }
    val activeLoans = loans.filter { it.status == "ACTIVE" || it.status == "APPROVED" }.sumOf { it.remainingAmount }
    val totalWealth = walletBal + investBal

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(top = 24.dp, bottom = 100.dp), // Space for bottom navigation
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Top Premium Profile Bar
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(DeepGreen)
                            .border(1.dp, GoldPrimary, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentUser?.fullName?.take(2)?.uppercase() ?: "AU",
                            color = GoldPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Welcome, ${currentUser?.fullName ?: "Client"}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = if (currentUser?.kycStatus == "APPROVED") Icons.Default.Verified else Icons.Default.ErrorOutline,
                                contentDescription = "Verification status",
                                tint = if (currentUser?.kycStatus == "APPROVED") GoldPrimary else ErrorRed,
                                modifier = Modifier.size(14.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (currentUser?.kycStatus == "APPROVED") "Tier 3 verified" else "Unverified Account",
                                fontSize = 11.sp,
                                color = TextSecondary,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                }

                // Secret Admin Gateway Toggle (Only visible for admin sheilaj08102@gmail.com)
                if (currentUser?.email == "sheilaj08102@gmail.com") {
                    IconButton(
                        onClick = { viewModel.navigateTo("admin") },
                        modifier = Modifier
                            .clip(CircleShape)
                            .background(DarkSurface)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AdminPanelSettings,
                            contentDescription = "Admin Gate",
                            tint = GoldPrimary
                        )
                    }
                }
            }
        }

        // Wealth Portfolio GlassCard
        item {
            GlassCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = BorderColor.copy(alpha = 0.5f)
            ) {
                Column(
                    modifier = Modifier
                        .background(
                            Brush.linearGradient(
                                colors = listOf(DeepGreen, DeepGreenLight, DeepGreenDark)
                            )
                        )
                        .padding(24.dp)
                ) {
                    Text(
                        text = "TOTAL WEALTH PORTFOLIO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = GoldPrimary,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = "₦${String.format("%,.2f", totalWealth)}",
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text("Wallet Cash", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text(
                                "₦${String.format("%,.2f", walletBal)}",
                                color = Color.White,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            Text("Active Investments", color = Color.White.copy(alpha = 0.6f), fontSize = 11.sp)
                            Text(
                                "₦${String.format("%,.2f", investBal)}",
                                color = GoldPrimary,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    // Balance Chart
                    Spacer(modifier = Modifier.height(8.dp))
                    FintechLineChart(
                        dataPoints = listOf(10000f, 15000f, 25000f, 40000f, 35000f, 65000f, totalWealth.toFloat()),
                        chartColor = GoldPrimary
                    )
                }
            }
        }

        // Quick Fintech Actions
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val actionList = listOf(
                    Triple("Deposit", Icons.Default.AddCard, { showDepositDialog = true }),
                    Triple("Withdraw", Icons.Default.Outbox, { showWithdrawDialog = true }),
                    Triple("Send Cash", Icons.Default.SwapHoriz, { showTransferDialog = true })
                )

                actionList.forEach { (label, icon, action) ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(16.dp))
                            .background(DarkSurface)
                            .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                            .clickable(onClick = action)
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(DeepGreen),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(icon, contentDescription = label, tint = GoldPrimary, modifier = Modifier.size(20.dp))
                            }
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(label, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // KYC Warning Notice (If pending or rejected)
        if (currentUser?.kycStatus != "APPROVED") {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(GoldPrimary.copy(alpha = 0.12f))
                        .border(1.dp, GoldPrimary.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                        .clickable { viewModel.navigateTo("kyc") }
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.WarningAmber, "Unverified", tint = GoldDark)
                    Spacer(modifier = Modifier.width(16.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Verify your KYC Identity",
                            color = TextPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Required for large real estate & agricultural contracts.",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                    Icon(Icons.Default.ChevronRight, "Navigate KYC", tint = TextSecondary)
                }
            }
        }

        // Active Loans / Thrift Brief
        if (activeLoans > 0) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(DarkSurface)
                        .border(1.dp, BorderColor, RoundedCornerShape(16.dp))
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(DeepGreen),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.MonetizationOn, "Loans active", tint = GoldPrimary)
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Active Loan Balance", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text("Repayment schedules current", color = TextSecondary, fontSize = 11.sp)
                        }
                    }
                    Text(
                        text = "₦${String.format("%,.2f", activeLoans)}",
                        color = GoldPrimary,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        // Referral Card
        item {
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("REFER & MULTIPLY WEALTH", color = GoldPrimary, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Code: ${currentUser?.referralCode ?: "AUR-4932"}", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Black)
                        Text("Earn 2.5% on referee first land unit buy.", color = TextSecondary, fontSize = 11.sp)
                    }
                    Button(
                        onClick = { /* Share link simulator */ },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepGreenLight, contentColor = TextPrimary),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Share Link", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Recent Transaction Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Transaction History",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "See All",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = GoldPrimary,
                    modifier = Modifier.clickable { /* Filter list */ }
                )
            }
        }

        // Recent Transaction list
        if (transactions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No transactions logged yet.", color = TextSecondary, fontSize = 13.sp)
                }
            }
        } else {
            items(transactions.take(5)) { tx ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(DarkSurface)
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(
                                    when (tx.type) {
                                        "DEPOSIT" -> DeepGreen
                                        "WITHDRAW" -> Color(0xFF42151B)
                                        else -> DarkSurfaceVariant
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = when (tx.type) {
                                    "DEPOSIT" -> Icons.Default.TrendingUp
                                    "WITHDRAW" -> Icons.Default.TrendingDown
                                    "INVESTMENT" -> Icons.Default.Domain
                                    else -> Icons.Default.ReceiptLong
                                },
                                contentDescription = tx.type,
                                tint = when (tx.type) {
                                    "DEPOSIT" -> GoldPrimary
                                    "WITHDRAW" -> ErrorRed
                                    else -> TextPrimary
                                },
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(tx.title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                            Text(tx.description, color = TextSecondary, fontSize = 11.sp)
                        }
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        val isPositive = tx.type == "DEPOSIT" || tx.type == "REFERRAL_BONUS"
                        val prefix = if (isPositive) "+" else "-"
                        val color = if (isPositive) GoldPrimary else TextPrimary
                        Text(
                            text = "$prefix₦${String.format("%,.2f", tx.amount)}",
                            color = color,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Black
                        )
                        val sdf = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
                        Text(
                            text = sdf.format(Date(tx.timestamp)),
                            color = TextSecondary,
                            fontSize = 10.sp
                        )
                    }
                }
            }
        }
    }

    // --- DIALOGS FOR MONEY ACTIONS ---

    // 1. Deposit Dialog
    if (showDepositDialog) {
        AlertDialog(
            onDismissRequest = { showDepositDialog = false },
            containerColor = DarkSurface,
            title = { Text("Instant Flutterwave Deposit", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text(
                        "Fund your Aurea wallet instantly using your Nigerian cards or bank transfers secured by Flutterwave API.",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(DarkSurfaceVariant)
                            .padding(12.dp)
                    ) {
                        Column {
                            Text("Aurea Providus Virtual Bank", fontSize = 11.sp, color = GoldPrimary, fontWeight = FontWeight.Bold)
                            Text("Account No: 9483210492", fontSize = 16.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                            Text("Beneficiary: Aurea - Sheila Johnson", fontSize = 11.sp, color = TextSecondary)
                        }
                    }
                    PremiumInputField(
                        value = depositAmount,
                        onValueChange = { depositAmount = it },
                        label = "DEPOSIT AMOUNT (₦)",
                        placeholder = "25000"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = depositAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.deposit(amt)
                            depositAmount = ""
                            showDepositDialog = false
                        }
                    }
                ) {
                    Text("Complete Funding", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDepositDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // 2. Withdraw Dialog
    if (showWithdrawDialog) {
        AlertDialog(
            onDismissRequest = { showWithdrawDialog = false },
            containerColor = DarkSurface,
            title = { Text("Withdraw Capital to Bank", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PremiumInputField(
                        value = withdrawAmount,
                        onValueChange = { withdrawAmount = it },
                        label = "WITHDRAWAL AMOUNT (₦)",
                        placeholder = "50000"
                    )
                    PremiumInputField(
                        value = bankName,
                        onValueChange = { bankName = it },
                        label = "DESTINATION BANK NAME (e.g. GTBank, Access)",
                        placeholder = "GTBank"
                    )
                    PremiumInputField(
                        value = bankAccount,
                        onValueChange = { bankAccount = it },
                        label = "NIGERIAN ACCOUNT NUMBER",
                        placeholder = "0123456789"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = withdrawAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0 && bankAccount.isNotEmpty()) {
                            viewModel.withdraw(amt, bankName, bankAccount)
                            withdrawAmount = ""
                            bankAccount = ""
                            showWithdrawDialog = false
                        }
                    }
                ) {
                    Text("Initiate Withdrawal", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showWithdrawDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // 3. Secure Transfer Dialog
    if (showTransferDialog) {
        AlertDialog(
            onDismissRequest = { showTransferDialog = false },
            containerColor = DarkSurface,
            title = { Text("Secure Wallet Transfer", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PremiumInputField(
                        value = recipientEmail,
                        onValueChange = { recipientEmail = it },
                        label = "RECIPIENT EMAIL / PHONE",
                        placeholder = "investor@example.com"
                    )
                    PremiumInputField(
                        value = transferAmount,
                        onValueChange = { transferAmount = it },
                        label = "TRANSFER AMOUNT (₦)",
                        placeholder = "10000"
                    )
                    PremiumInputField(
                        value = transferNote,
                        onValueChange = { transferNote = it },
                        label = "NOTE (OPTIONAL)",
                        placeholder = "Investment joint share"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = transferAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0 && recipientEmail.isNotEmpty()) {
                            viewModel.transfer(amt, recipientEmail, transferNote)
                            transferAmount = ""
                            recipientEmail = ""
                            transferNote = ""
                            showTransferDialog = false
                        }
                    }
                ) {
                    Text("Confirm Transfer", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showTransferDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
