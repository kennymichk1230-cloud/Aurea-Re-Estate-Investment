package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "local_users")
data class LocalUser(
    @PrimaryKey val email: String,
    val fullName: String,
    val phone: String,
    val referralCode: String = "AUR-${(1000..9999).random()}",
    val referredBy: String? = null,
    val walletBalance: Double = 50000.0, // Default starting demo balance
    val kycStatus: String = "PENDING", // PENDING, APPROVED, REJECTED
    val kycDocumentType: String = "", // National ID, Passport, Driver's License
    val kycDocUrl: String = "",
    val pinCode: String = "1234",
    val isBiometricEnabled: Boolean = true,
    val isTwoFactorEnabled: Boolean = false,
    val referralEarnings: Double = 0.0,
    val activeSavings: Double = 1200.0
)

@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val type: String, // DEPOSIT, WITHDRAW, TRANSFER, INVESTMENT, LOAN_REPAYMENT, REFERRAL_BONUS, THRIFT
    val title: String,
    val description: String,
    val amount: Double,
    val timestamp: Long = System.currentTimeMillis(),
    val status: String = "COMPLETED" // COMPLETED, PENDING, FAILED
)

@Entity(tableName = "investments")
data class Investment(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val type: String, // LAND, RENTAL_HOUSE, FARMING, FOOD
    val projectName: String,
    val location: String,
    val amountInvested: Double,
    val expectedReturnRate: Double, // in % (e.g. 15.5)
    val durationMonths: Int,
    val dateInvested: Long = System.currentTimeMillis(),
    val roiEarned: Double = 0.0,
    val status: String = "ACTIVE", // ACTIVE, MATURED, LIQUIDATED
    val imageUrl: String = "",
    val details: String = ""
)

@Entity(tableName = "loans")
data class Loan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val type: String, // PERSONAL, BUSINESS
    val amountRequested: Double,
    val durationMonths: Int,
    val monthlyPayment: Double,
    val interestRate: Double = 8.5, // % per annum
    val creditScore: Int = 750,
    val status: String = "PENDING", // PENDING, APPROVED, REJECTED, ACTIVE, FULLY_PAID
    val dateApplied: Long = System.currentTimeMillis(),
    val remainingAmount: Double
)

@Entity(tableName = "thrift_contributions")
data class ThriftPlan(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val userEmail: String,
    val title: String,
    val frequency: String, // DAILY, WEEKLY, MONTHLY
    val contributionAmount: Double,
    val currentBalance: Double = 0.0,
    val totalGoal: Double,
    val membersCount: Int = 1, // 1 for self, higher for Group Thrift
    val autoDebit: Boolean = true,
    val dateStarted: Long = System.currentTimeMillis(),
    val status: String = "ACTIVE" // ACTIVE, COMPLETED
)

@Entity(tableName = "crypto_portfolios")
data class CryptoToken(
    @PrimaryKey val symbol: String, // BTC, ETH, SOL, ADA, USDT
    val name: String,
    val balance: Double,
    val averageBuyPrice: Double,
    val livePrice: Double,
    val priceChangePercent24h: Double
)

@Entity(tableName = "investment_projects")
data class InvestmentProject(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val type: String, // LAND, RENTAL_HOUSE, FARMING, FOOD
    val name: String,
    val location: String,
    val roiRate: Double,
    val durationMonths: Int,
    val minInvestment: Double,
    val description: String,
    val isAvailable: Boolean = true
)

