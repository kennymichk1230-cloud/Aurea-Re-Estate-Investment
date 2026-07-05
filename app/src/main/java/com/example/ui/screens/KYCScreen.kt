package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
fun KYCScreen(viewModel: MainViewModel) {
    val userState by viewModel.currentUser.collectAsState()
    val scrollState = rememberScrollState()

    var docType by remember { mutableStateOf("National ID") }
    var idNumber by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    var selfieSubmitted by remember { mutableStateOf(false) }
    var docUploaded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBackground)
            .verticalScroll(scrollState)
            .padding(24.dp)
            .padding(bottom = 80.dp) // Bottom navigation padding
    ) {
        // Header
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
                    text = "Identity Verification",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "Comply with global SEC / KYC guidelines.",
                    fontSize = 12.sp,
                    color = TextSecondary
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // KYC Status Banner
        val status = userState?.kycStatus ?: "PENDING"
        val bannerBg = when (status) {
            "APPROVED" -> DeepGreen.copy(alpha = 0.12f)
            "REJECTED" -> ErrorRed.copy(alpha = 0.12f)
            else -> DarkSurfaceVariant
        }
        val bannerTint = when (status) {
            "APPROVED" -> DeepGreen
            "REJECTED" -> ErrorRed
            else -> TextSecondary
        }
        val bannerText = when (status) {
            "APPROVED" -> "Your KYC Identity verification is APPROVED. All high-limit investments are unlocked."
            "REJECTED" -> "Your KYC submission was REJECTED. Please re-upload clear photos."
            else -> "KYC Verification: PENDING. Some premium and high-limit investments are locked until approved."
        }
        val bannerTextColor = when (status) {
            "APPROVED" -> DeepGreen
            "REJECTED" -> ErrorRed
            else -> TextPrimary
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(bannerBg)
                .border(1.dp, bannerTint.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = when (status) {
                    "APPROVED" -> Icons.Default.CheckCircle
                    "REJECTED" -> Icons.Default.Cancel
                    else -> Icons.Default.Info
                },
                contentDescription = status,
                tint = bannerTint,
                modifier = Modifier.size(28.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = bannerText,
                fontSize = 13.sp,
                color = bannerTextColor,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(24.dp))

        if (status != "APPROVED") {
            Text(
                text = "Submit Identity Credentials",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold,
                color = DeepGreen,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            GlassCard(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(20.dp)) {
                    // Document selector
                    Text(
                        text = "DOCUMENT TYPE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = DeepGreen,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        listOf("National ID", "Passport", "License").forEach { type ->
                            val selected = docType == type
                            Box(
                                modifier = Modifier
                                    .weight(1f)
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(if (selected) DeepGreen else DarkSurfaceVariant)
                                    .border(1.dp, if (selected) DeepGreenLight else BorderColor, RoundedCornerShape(10.dp))
                                    .clickable { docType = type }
                                    .padding(vertical = 10.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = type,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (selected) Color.White else TextPrimary
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    PremiumInputField(
                        value = idNumber,
                        onValueChange = { idNumber = it },
                        label = "DOCUMENT / ID NUMBER",
                        placeholder = "A-98327421"
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    PremiumInputField(
                        value = address,
                        onValueChange = { address = it },
                        label = "RESIDENTIAL ADDRESS",
                        placeholder = "12 Luxury Boulevard, High-wealth District"
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    // Document Photo Upload
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .clickable { docUploaded = !docUploaded }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (docUploaded) Icons.Default.CheckCircle else Icons.Default.CloudUpload,
                            contentDescription = "Upload Document",
                            tint = if (docUploaded) DeepGreenLight else GoldPrimary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (docUploaded) "ID Uploaded successfully" else "Upload Front & Back of $docType",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (docUploaded) "File: ${docType.lowercase()}_front.jpg" else "Max size: 10MB. Formats: JPG, PNG",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Selfie Video Verification
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(DarkSurfaceVariant)
                            .border(1.dp, BorderColor, RoundedCornerShape(12.dp))
                            .clickable { selfieSubmitted = !selfieSubmitted }
                            .padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = if (selfieSubmitted) Icons.Default.CheckCircle else Icons.Default.PhotoCamera,
                            contentDescription = "Take Selfie",
                            tint = if (selfieSubmitted) DeepGreenLight else GoldPrimary
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = if (selfieSubmitted) "Selfie Bio-Scan Completed" else "Capture Selfie Verification",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = TextPrimary
                            )
                            Text(
                                text = if (selfieSubmitted) "AI Match score: 98.4% (Confidence high)" else "Make sure your face is clearly lit.",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    GoldButton(
                        text = "Submit to Compliance",
                        onClick = {
                            viewModel.submitKYC(docType, "https://secure-docs.aurea.com/sheila_id_jpg")
                        },
                        enabled = idNumber.isNotBlank() && address.isNotBlank() && selfieSubmitted && docUploaded
                    )
                }
            }
        } else {
            // Already approved layout
            Spacer(modifier = Modifier.height(40.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(DeepGreen),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.VerifiedUser,
                        contentDescription = "Verified",
                        tint = GoldPrimary,
                        modifier = Modifier.size(64.dp)
                    )
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                    text = "Aurea Diamond Tier Verified",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
                Text(
                    text = "You have unlimited access to the savings vaults, high-yield agricultural cycles, and commercial real estate fractional pools.",
                    fontSize = 13.sp,
                    color = TextSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 12.dp)
                )
            }
        }
    }
}
