package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fingerprint
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
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
import com.example.ui.components.GoldButton
import com.example.ui.components.PremiumInputField
import com.example.ui.theme.*

@Composable
fun LoginScreen(viewModel: MainViewModel, onLoginSuccess: () -> Unit) {
    var isRegisterMode by remember { mutableStateOf(false) }
    var email by remember { mutableStateOf("sheilaj08102@gmail.com") }
    var password by remember { mutableStateOf("••••••••") }
    var fullName by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }

    // PIN Authentication States
    var pinEntered by remember { mutableStateOf("") }
    var usePinMode by remember { mutableStateOf(true) }

    val userState by viewModel.currentUser.collectAsState()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Decorative rich gradient backgrounds
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.35f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(DeepGreen.copy(alpha = 0.6f), Color.Transparent)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp)
                .navigationBarsPadding()
                .statusBarsPadding(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "AUREA",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 6.sp,
                    color = GoldPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "RE-ESTATE",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 4.sp,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "Invest Smart. Grow Wealth.",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }

            // Main Forms
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
                    .padding(vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (usePinMode && !isRegisterMode) {
                    // PIN Screen
                    Text(
                        text = "Enter Access PIN",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Dots indicator
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier.padding(bottom = 32.dp)
                    ) {
                        for (i in 1..4) {
                            val active = pinEntered.length >= i
                            Box(
                                modifier = Modifier
                                    .size(16.dp)
                                    .clip(CircleShape)
                                    .background(if (active) GoldPrimary else BorderColor)
                                    .border(1.dp, if (active) GoldLight else Color.Transparent, CircleShape)
                            )
                        }
                    }

                    // Keypad
                    Column(
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.width(280.dp)
                    ) {
                        val keys = listOf(
                            listOf("1", "2", "3"),
                            listOf("4", "5", "6"),
                            listOf("7", "8", "9"),
                            listOf("Bio", "0", "Del")
                        )

                        keys.forEach { row ->
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                row.forEach { key ->
                                    Box(
                                        modifier = Modifier
                                            .weight(1f)
                                            .aspectRatio(1.3f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .background(DarkSurface)
                                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                                            .clickable {
                                                when (key) {
                                                    "Bio" -> {
                                                        // Simulated Biometric success
                                                        onLoginSuccess()
                                                    }
                                                    "Del" -> {
                                                        if (pinEntered.isNotEmpty()) {
                                                            pinEntered = pinEntered.dropLast(1)
                                                        }
                                                    }
                                                    else -> {
                                                        if (pinEntered.length < 4) {
                                                            pinEntered += key
                                                            if (pinEntered.length == 4) {
                                                                // Simulated login check
                                                                if (pinEntered == "1234") {
                                                                    onLoginSuccess()
                                                                } else {
                                                                    pinEntered = "" // Clear on mismatch
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            },
                                        contentAlignment = Alignment.Center
                                    ) {
                                        if (key == "Bio") {
                                            Icon(
                                                imageVector = Icons.Default.Fingerprint,
                                                contentDescription = "Biometric Login",
                                                tint = GoldPrimary,
                                                modifier = Modifier.size(28.dp)
                                            )
                                        } else {
                                            Text(
                                                text = key,
                                                fontSize = 20.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (key == "Del") ErrorRed else TextPrimary
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "Use Password instead",
                        color = GoldPrimary,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier
                            .clickable { usePinMode = false }
                            .padding(8.dp)
                    )

                } else {
                    // Password/Registration Forms
                    if (isRegisterMode) {
                        PremiumInputField(
                            value = fullName,
                            onValueChange = { fullName = it },
                            label = "FULL NAME",
                            placeholder = "Sheila Johnson",
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = GoldPrimary) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        PremiumInputField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = "PHONE NUMBER",
                            placeholder = "+1 (555) 234-5678",
                            leadingIcon = { Icon(Icons.Default.Phone, null, tint = GoldPrimary) }
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                    }

                    PremiumInputField(
                        value = email,
                        onValueChange = { email = it },
                        label = "EMAIL ADDRESS",
                        placeholder = "yourname@example.com",
                        leadingIcon = { Icon(Icons.Default.Mail, null, tint = GoldPrimary) }
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    PremiumInputField(
                        value = password,
                        onValueChange = { password = it },
                        label = "PASSWORD",
                        placeholder = "••••••••",
                        leadingIcon = { Icon(Icons.Default.Lock, null, tint = GoldPrimary) }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    GoldButton(
                        text = if (isRegisterMode) "Create Account" else "Secure Login",
                        onClick = {
                            onLoginSuccess()
                        }
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = if (isRegisterMode) "Already have an account? Login" else "New to Aurea? Create Account",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        modifier = Modifier
                            .clickable { isRegisterMode = !isRegisterMode }
                            .padding(8.dp)
                    )

                    if (!isRegisterMode) {
                        Text(
                            text = "Switch back to quick PIN Auth",
                            color = GoldPrimary,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .clickable { usePinMode = true }
                                .padding(8.dp)
                        )
                    }
                }
            }

            // Footer / Compliance
            Text(
                text = "Secured with AES-256 Military Encryption.\nMember NDIC & SEC licensed portal.",
                fontSize = 10.sp,
                color = TextSecondary,
                textAlign = TextAlign.Center,
                lineHeight = 14.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
    }
}
