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
import com.example.data.Loan
import com.example.data.ThriftPlan
import com.example.ui.MainViewModel
import com.example.ui.components.GlassCard
import com.example.ui.components.GoldButton
import com.example.ui.components.PremiumInputField
import com.example.ui.theme.*

@Composable
fun FinancialServicesScreen(viewModel: MainViewModel) {
    val loansState by viewModel.loans.collectAsState()
    val thriftState by viewModel.thriftPlans.collectAsState()
    val userState by viewModel.currentUser.collectAsState()

    var activeSubTab by remember { mutableStateOf("LOANS") } // LOANS, THRIFT

    // State for loan application dialog
    var showLoanApplyDialog by remember { mutableStateOf(false) }
    var loanType by remember { mutableStateOf("PERSONAL") }
    var loanAmount by remember { mutableStateOf("") }
    var loanDuration by remember { mutableStateOf("12") }

    // State for Thrift creation dialog
    var showThriftCreateDialog by remember { mutableStateOf(false) }
    var thriftTitle by remember { mutableStateOf("") }
    var thriftFreq by remember { mutableStateOf("WEEKLY") }
    var thriftContribution by remember { mutableStateOf("") }
    var thriftGoal by remember { mutableStateOf("") }

    // State for Thrift contribution dialog
    var selectedThriftPlan by remember { mutableStateOf<ThriftPlan?>(null) }
    var thriftPaymentAmount by remember { mutableStateOf("") }

    // State for Loan payment dialog
    var selectedRepayLoan by remember { mutableStateOf<Loan?>(null) }
    var repayAmount by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .padding(horizontal = 20.dp),
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = "DEBT & SAVINGS DESK",
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            color = DeepGreen,
            letterSpacing = 1.sp
        )
        Text(
            text = "Financial Services",
            fontSize = 24.sp,
            fontWeight = FontWeight.Black,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Sub-tabs LOANS / THRIFT
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(DarkSurface)
                .padding(4.dp)
        ) {
            listOf("LOANS" to "Secure Credit", "THRIFT" to "Thrift (Ajo)").forEach { (id, label) ->
                val selected = activeSubTab == id
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(10.dp))
                        .background(if (selected) DeepGreen else Color.Transparent)
                        .clickable { activeSubTab = id }
                        .padding(vertical = 12.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (selected) Color.White else TextSecondary
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        if (activeSubTab == "LOANS") {
            // LOANS SYSTEM VIEW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Credit Score Indicator
                GlassCard(modifier = Modifier.weight(1.2f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("CREDIT SCORE", fontSize = 9.sp, color = TextSecondary)
                        Text("780 / 850", fontSize = 20.sp, fontWeight = FontWeight.Black, color = DeepGreen)
                        Text("Tier 3 Prime Rating", fontSize = 10.sp, color = DeepGreenLight, fontWeight = FontWeight.Bold)
                    }
                }
                // Loan eligibility
                GlassCard(modifier = Modifier.weight(1.8f)) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text("MAX ELIGIBILITY", fontSize = 9.sp, color = TextSecondary)
                        Text("₦150,000.00", fontSize = 20.sp, fontWeight = FontWeight.Black, color = TextPrimary)
                        Text("Collateralized by land contracts", fontSize = 10.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Your Credit Portfolios", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                Button(
                    onClick = { showLoanApplyDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = DarkBackground),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Icon(Icons.Default.Add, "Apply", modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Request Credit", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            val currentLoans = loansState
            if (currentLoans.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No active credit applications found.", color = TextSecondary, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(currentLoans) { loan ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("${loan.type} LOAN", color = DeepGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text("Balance: ₦${String.format("%,.2f", loan.remainingAmount)}", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Black)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(
                                                when (loan.status) {
                                                    "APPROVED", "ACTIVE" -> DeepGreen.copy(alpha = 0.12f)
                                                    "PENDING" -> GoldPrimary.copy(alpha = 0.15f)
                                                    else -> DarkSurfaceVariant
                                                }
                                            )
                                            .padding(horizontal = 10.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = loan.status,
                                            color = when (loan.status) {
                                                "APPROVED", "ACTIVE" -> DeepGreen
                                                "PENDING" -> GoldDark
                                                else -> TextSecondary
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.Bottom
                                ) {
                                    Column {
                                        Text("MONTHLY EMI", fontSize = 10.sp, color = TextSecondary)
                                        Text("₦${String.format("%,.2f", loan.monthlyPayment)}", fontSize = 13.sp, color = TextPrimary, fontWeight = FontWeight.Bold)
                                    }
                                    if (loan.status == "ACTIVE" && loan.remainingAmount > 0) {
                                        Button(
                                            onClick = { selectedRepayLoan = loan },
                                            colors = ButtonDefaults.buttonColors(containerColor = DeepGreenLight, contentColor = TextPrimary),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Repay EMI", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        } else {
            // THRIFT SAVINGS VIEW
            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("AJO SAVINGS MULTIPLIER", color = DeepGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Automated Thrift Pool", color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                        Text("Automated weekly / daily group payouts.", color = TextSecondary, fontSize = 11.sp)
                    }
                    Button(
                        onClick = { showThriftCreateDialog = true },
                        colors = ButtonDefaults.buttonColors(containerColor = DeepGreen, contentColor = Color.White),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("+ Create Plan", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            val currentThrifts = thriftState
            if (currentThrifts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 40.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("No Thrift Saving plans configured.", color = TextSecondary, fontSize = 13.sp)
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f),
                    contentPadding = PaddingValues(bottom = 100.dp)
                ) {
                    items(currentThrifts) { plan ->
                        GlassCard(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text("${plan.frequency} THRIFT", color = DeepGreen, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                                        Text(plan.title, color = TextPrimary, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(DeepGreen)
                                            .padding(horizontal = 8.dp, vertical = 4.dp)
                                    ) {
                                        Text(
                                            text = "${plan.membersCount} Members",
                                            color = GoldPrimary,
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Progress Bar
                                val progress = (plan.currentBalance / plan.totalGoal).toFloat().coerceIn(0f, 1f)
                                Column {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text("Balance: ₦${String.format("%,.0f", plan.currentBalance)}", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                        Text("Goal: ₦${String.format("%,.0f", plan.totalGoal)}", color = TextSecondary, fontSize = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.height(6.dp))
                                    LinearProgressIndicator(
                                        progress = { progress },
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(CircleShape),
                                        color = DeepGreen,
                                        trackColor = BorderColor
                                    )
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "Next: ₦${String.format("%,.2f", plan.contributionAmount)} due in 2 days",
                                        color = TextSecondary,
                                        fontSize = 11.sp
                                    )
                                    if (plan.status == "ACTIVE") {
                                        Button(
                                            onClick = { selectedThriftPlan = plan },
                                            colors = ButtonDefaults.buttonColors(containerColor = DeepGreen, contentColor = Color.White),
                                            shape = RoundedCornerShape(8.dp)
                                        ) {
                                            Text("Save Now", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // --- DIALOGS FOR SAVINGS / DEBTS ---

    // 1. Apply Loan Dialog
    if (showLoanApplyDialog) {
        AlertDialog(
            onDismissRequest = { showLoanApplyDialog = false },
            containerColor = DarkSurface,
            title = { Text("Apply for Credit Extension", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    // Type selector
                    Text("LOAN TYPE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("PERSONAL", "BUSINESS").forEach { type ->
                            val active = loanType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) GoldPrimary else DarkSurfaceVariant)
                                    .clickable { loanType = type }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(type, color = if (active) DarkBackground else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                            }
                        }
                    }

                    PremiumInputField(
                        value = loanAmount,
                        onValueChange = { loanAmount = it },
                        label = "LOAN PRINCIPAL AMOUNT (₦)",
                        placeholder = "50000"
                    )

                    PremiumInputField(
                        value = loanDuration,
                        onValueChange = { loanDuration = it },
                        label = "DURATION (MONTHS)",
                        placeholder = "12"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = loanAmount.toDoubleOrNull() ?: 0.0
                        val dur = loanDuration.toIntOrNull() ?: 12
                        if (amt > 0 && dur > 0) {
                            viewModel.applyForLoan(loanType, amt, dur)
                            loanAmount = ""
                            showLoanApplyDialog = false
                        }
                    }
                ) {
                    Text("Submit Application", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showLoanApplyDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // 2. Repay Loan Dialog
    if (selectedRepayLoan != null) {
        val loan = selectedRepayLoan!!
        AlertDialog(
            onDismissRequest = { selectedRepayLoan = null },
            containerColor = DarkSurface,
            title = { Text("Repay Credit Facility", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Repaying on your ${loan.type} Loan.", color = TextSecondary, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Current Remaining:", color = TextSecondary, fontSize = 13.sp)
                        Text("₦${String.format("%,.2f", loan.remainingAmount)}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    PremiumInputField(
                        value = repayAmount,
                        onValueChange = { repayAmount = it },
                        label = "REPAYMENT AMOUNT (₦)",
                        placeholder = "Monthly EMI: ₦${String.format("%.2f", loan.monthlyPayment)}"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = repayAmount.toDoubleOrNull() ?: 0.0
                        if (amt > 0) {
                            viewModel.repayLoan(loan, amt)
                            repayAmount = ""
                            selectedRepayLoan = null
                        }
                    }
                ) {
                    Text("Repay Now", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedRepayLoan = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // 3. Create Thrift Dialog
    if (showThriftCreateDialog) {
        AlertDialog(
            onDismissRequest = { showThriftCreateDialog = false },
            containerColor = DarkSurface,
            title = { Text("New Thrift Cycle (Ajo)", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    PremiumInputField(
                        value = thriftTitle,
                        onValueChange = { thriftTitle = it },
                        label = "SAVING TARGET TITLE",
                        placeholder = "Thrift Builders / Car Goal / Land Vault"
                    )

                    Text("CONTRIBUTION CYCLE", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = GoldPrimary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                        listOf("DAILY", "WEEKLY", "MONTHLY").forEach { freq ->
                            val active = thriftFreq == freq
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(if (active) GoldPrimary else DarkSurfaceVariant)
                                    .clickable { thriftFreq = freq }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(freq, color = if (active) DarkBackground else TextPrimary, fontWeight = FontWeight.Bold, fontSize = 11.sp)
                            }
                        }
                    }

                    PremiumInputField(
                        value = thriftContribution,
                        onValueChange = { thriftContribution = it },
                        label = "CONTRIBUTION AMOUNT PER CYCLE (₦)",
                        placeholder = "1000"
                    )

                    PremiumInputField(
                        value = thriftGoal,
                        onValueChange = { thriftGoal = it },
                        label = "TOTAL TARGET GOAL (₦)",
                        placeholder = "50000"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val contr = thriftContribution.toDoubleOrNull() ?: 0.0
                        val goal = thriftGoal.toDoubleOrNull() ?: 0.0
                        if (thriftTitle.isNotEmpty() && contr > 0 && goal > 0) {
                            viewModel.createThriftPlan(thriftTitle, thriftFreq, contr, goal)
                            thriftTitle = ""
                            thriftContribution = ""
                            thriftGoal = ""
                            showThriftCreateDialog = false
                        }
                    }
                ) {
                    Text("Initiate Thrift Pool", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showThriftCreateDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    // 4. Contribute Thrift Dialog
    if (selectedThriftPlan != null) {
        val plan = selectedThriftPlan!!
        AlertDialog(
            onDismissRequest = { selectedThriftPlan = null },
            containerColor = DarkSurface,
            title = { Text("Contribute to Thrift Plan", color = GoldPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                    Text("Plan: ${plan.title}", color = TextSecondary, fontSize = 12.sp)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("Regular Cycle Amount:", color = TextSecondary, fontSize = 13.sp)
                        Text("₦${String.format("%,.2f", plan.contributionAmount)}", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    }
                    PremiumInputField(
                        value = thriftPaymentAmount,
                        onValueChange = { thriftPaymentAmount = it },
                        label = "CONTRIBUTION SUM (₦)",
                        placeholder = "${String.format("%.0f", plan.contributionAmount)}"
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        val amt = thriftPaymentAmount.toDoubleOrNull() ?: plan.contributionAmount
                        if (amt > 0) {
                            viewModel.contributeToThrift(plan, amt)
                            thriftPaymentAmount = ""
                            selectedThriftPlan = null
                        }
                    }
                ) {
                    Text("Confirm Savings", color = GoldPrimary, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedThriftPlan = null }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }
}
