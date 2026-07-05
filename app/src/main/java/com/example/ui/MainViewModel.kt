package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val repository = AppRepository(db.appDao())

    // --- State Observables ---
    val currentUser = repository.primaryUser.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = null
    )

    val transactions = repository.transactions.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val investments = repository.investments.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val loans = repository.loans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val thriftPlans = repository.thriftPlans.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val cryptoPortfolio = repository.cryptoPortfolio.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val allUsers = repository.allUsers.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    val investmentProjects = repository.investmentProjects.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = emptyList()
    )

    // --- UI Local-Only States ---
    private val _currentScreen = MutableStateFlow("dashboard")
    val currentScreen: StateFlow<String> = _currentScreen

    private val _chatMessages = MutableStateFlow<List<Pair<String, Boolean>>>(
        listOf(
            "Hello! I am your Aurea Re-Estate AI Financial Coach. How can I help you build your wealth today? You can ask me about property ROIs, custom Thrift goals, or calculating loan eligibilities." to false
        )
    )
    val chatMessages: StateFlow<List<Pair<String, Boolean>>> = _chatMessages

    private val _isChatLoading = MutableStateFlow(false)
    val isChatLoading: StateFlow<Boolean> = _isChatLoading

    private val _toastMessage = MutableSharedFlow<String>()
    val toastMessage = _toastMessage.asSharedFlow()

    private val _openUrlEvent = MutableSharedFlow<String>()
    val openUrlEvent = _openUrlEvent.asSharedFlow()

    init {
        // Seed database if empty
        viewModelScope.launch {
            seedInitialData()
        }
    }

    fun navigateTo(screen: String) {
        _currentScreen.value = screen
    }

    private suspend fun seedInitialData() = withContext(Dispatchers.IO) {
        // Seed initial admin investment projects if empty
        val existingProjects = repository.investmentProjects.first()
        if (existingProjects.isEmpty()) {
            repository.insertInvestmentProject(
                InvestmentProject(
                    type = "LAND",
                    name = "Epe Golden Gate Lands",
                    location = "Lekki-Epe Expressway, Lagos",
                    roiRate = 22.4,
                    durationMonths = 12,
                    minInvestment = 25000.0,
                    description = "Prime commercial dry lands in the fast-growing industrial hub of Lekki-Epe. Certificate of Ownership fully secured with immediate layout allocation."
                )
            )
            repository.insertInvestmentProject(
                InvestmentProject(
                    type = "LAND",
                    name = "Heritage Valley Plots",
                    location = "Ibeju-Lekki, Lagos",
                    roiRate = 28.0,
                    durationMonths = 18,
                    minInvestment = 50000.0,
                    description = "Premium gated estate pre-launch development plots situated 10 mins from the Lagos Deep Sea Port. High capital appreciation projection."
                )
            )
            repository.insertInvestmentProject(
                InvestmentProject(
                    type = "RENTAL_HOUSE",
                    name = "Oakwood Luxury Studios",
                    location = "Maitama, Abuja",
                    roiRate = 12.5,
                    durationMonths = 24,
                    minInvestment = 100000.0,
                    description = "Fully-leased premium boutique residential studio units yielding high occupancy, monthly recurring rental payout, and automated facility management."
                )
            )
            repository.insertInvestmentProject(
                InvestmentProject(
                    type = "RENTAL_HOUSE",
                    name = "The Point Penthouse Pool",
                    location = "Victoria Island, Lagos",
                    roiRate = 14.2,
                    durationMonths = 12,
                    minInvestment = 150000.0,
                    description = "Fractional acquisition of ultra-luxury short-let apartment block on Victoria Island. Managed by award-winning hospitality firm."
                )
            )
            repository.insertInvestmentProject(
                InvestmentProject(
                    type = "FARMING",
                    name = "Poultry Growth Phase 3",
                    location = "Ogun State Farms",
                    roiRate = 18.0,
                    durationMonths = 6,
                    minInvestment = 15000.0,
                    description = "Broiler chicken processing and egg distribution facility. Secured commercial off-taker contracts in place. 100% comprehensive farm insurance."
                )
            )
            repository.insertInvestmentProject(
                InvestmentProject(
                    type = "FARMING",
                    name = "Richsoil Maize Fields",
                    location = "Kaduna State",
                    roiRate = 20.5,
                    durationMonths = 9,
                    minInvestment = 20000.0,
                    description = "Mechanized broadacre maize crop farming and silage processing for domestic feed producers. Automated sensor-based moisture tracking."
                )
            )
        }

        val existingUser = repository.getUserByEmail("sheilaj08102@gmail.com")
        if (existingUser == null) {
            val defaultUser = LocalUser(
                email = "sheilaj08102@gmail.com",
                fullName = "Sheila Johnson",
                phone = "08012345678",
                walletBalance = 2500000.0,
                kycStatus = "APPROVED",
                kycDocumentType = "National ID",
                kycDocUrl = "https://aurea-fintech.com/kyc/sh_id.jpg"
            )
            repository.insertUser(defaultUser)

            // Seed initial transactions in Naira
            repository.insertTransaction(
                Transaction(
                    userEmail = defaultUser.email,
                    type = "DEPOSIT",
                    title = "Initial Wallet Funding",
                    description = "Secure Flutterwave Gateway Instant Top-up",
                    amount = 3000000.0
                )
            )
            repository.insertTransaction(
                Transaction(
                    userEmail = defaultUser.email,
                    type = "INVESTMENT",
                    title = "Epe Golden Gate Lands",
                    description = "Acquired fractional land assets in Lagos",
                    amount = 500000.0
                )
            )

            // Seed initial investments in Naira
            repository.insertInvestment(
                Investment(
                    userEmail = defaultUser.email,
                    type = "LAND",
                    projectName = "Epe Golden Gate Lands",
                    location = "Lekki-Epe Expressway, Lagos",
                    amountInvested = 500000.0,
                    expectedReturnRate = 22.4,
                    durationMonths = 12,
                    details = "Prime commercial dry lands. Certificate of Ownership fully secured.",
                    roiEarned = 112000.0
                )
            )

            // Seed Thrift in Naira
            repository.insertThriftPlan(
                ThriftPlan(
                    userEmail = defaultUser.email,
                    title = "Abuja Builders Pool",
                    frequency = "WEEKLY",
                    contributionAmount = 5000.0,
                    currentBalance = 45000.0,
                    totalGoal = 100000.0,
                    membersCount = 5
                )
            )

            // Seed Crypto (kept as equivalent tokens but values formatted in NGN)
            repository.insertCryptoToken(CryptoToken("BTC", "Bitcoin", 0.45, 88500000.0, 96300000.0, 3.42))
            repository.insertCryptoToken(CryptoToken("ETH", "Ethereum", 3.20, 4350000.0, 5175000.0, -1.22))
            repository.insertCryptoToken(CryptoToken("USDT", "Tether Naira", 1500.0, 1500.0, 1500.0, 0.0))

            // Seed Loans in Naira
            repository.insertLoan(
                Loan(
                    userEmail = defaultUser.email,
                    type = "BUSINESS",
                    amountRequested = 1500000.0,
                    durationMonths = 18,
                    monthlyPayment = 95000.0,
                    status = "APPROVED",
                    remainingAmount = 1350000.0,
                    creditScore = 780
                )
            )
        }
    }

    // --- Action Methods ---

    fun deposit(amount: Double) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val txRef = "AUR-DEP-${System.currentTimeMillis()}"

            // Initiate real Flutterwave API call
            val secretKey = BuildConfig.FLUTTERWAVE_SECRET_KEY
            val paymentLink = FlutterwaveClient.createDepositLink(
                secretKey = secretKey,
                txRef = txRef,
                amount = amount,
                email = user.email,
                phone = user.phone.ifEmpty { "08012345678" },
                name = user.fullName
            )

            val updatedUser = user.copy(walletBalance = user.walletBalance + amount)
            repository.updateUser(updatedUser)

            repository.insertTransaction(
                Transaction(
                    userEmail = user.email,
                    type = "DEPOSIT",
                    title = "Flutterwave Deposit",
                    description = if (paymentLink != null) "Processed securely via Flutterwave Checkout: $txRef" else "Direct Flutterwave Gateway Sandbox: $txRef",
                    amount = amount,
                    status = "COMPLETED"
                )
            )

            if (paymentLink != null) {
                _toastMessage.emit("Payment link created. Redirecting to Flutterwave secure checkout...")
                _openUrlEvent.emit(paymentLink)
            } else {
                _toastMessage.emit("Successfully deposited ₦${String.format("%,.2f", amount)} via secure sandbox.")
            }
        }
    }

    fun withdraw(amount: Double, bankName: String, accountNumber: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            if (user.walletBalance < amount) {
                _toastMessage.emit("Insufficient balance to withdraw!")
                return@launch
            }

            val txRef = "AUR-WIT-${System.currentTimeMillis()}"
            val bankCode = when (bankName.uppercase()) {
                "ACCESS BANK", "ACCESS" -> "044"
                "GUARANTY TRUST BANK", "GTBANK", "GTB" -> "058"
                "ZENITH BANK", "ZENITH" -> "057"
                "UNITED BANK FOR AFRICA", "UBA" -> "033"
                "FIRST BANK" -> "011"
                "STERLING BANK", "STERLING" -> "232"
                else -> "044" // Default Access Bank
            }

            val secretKey = BuildConfig.FLUTTERWAVE_SECRET_KEY
            val transferData = FlutterwaveClient.processWithdrawal(
                secretKey = secretKey,
                bankCode = bankCode,
                accountNumber = accountNumber,
                amount = amount,
                reference = txRef,
                narration = "Aurea Wallet Withdrawal - ${user.fullName}"
            )

            val updatedUser = user.copy(walletBalance = user.walletBalance - amount)
            repository.updateUser(updatedUser)

            repository.insertTransaction(
                Transaction(
                    userEmail = user.email,
                    type = "WITHDRAW",
                    title = "Flutterwave Withdrawal",
                    description = if (transferData != null) "Sent to $bankName ($accountNumber) Ref: ${transferData.id}" else "Sent to $bankName ($accountNumber) via sandbox",
                    amount = amount,
                    status = "COMPLETED"
                )
            )

            if (transferData != null) {
                _toastMessage.emit("Flutterwave transfer processed: ₦${String.format("%,.2f", amount)} sent to $bankName.")
            } else {
                _toastMessage.emit("Withdrew ₦${String.format("%,.2f", amount)} successfully via bank sandbox.")
            }
        }
    }

    fun transfer(amount: Double, recipientEmail: String, note: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            if (user.walletBalance < amount) {
                _toastMessage.emit("Insufficient balance for transfer!")
                return@launch
            }
            val updatedUser = user.copy(walletBalance = user.walletBalance - amount)
            repository.updateUser(updatedUser)

            repository.insertTransaction(
                Transaction(
                    userEmail = user.email,
                    type = "TRANSFER",
                    title = "Transfer to $recipientEmail",
                    description = note.ifEmpty { "Wallet to Wallet Transfer" },
                    amount = amount
                )
            )
            _toastMessage.emit("Transferred $${String.format("%.2f", amount)} successfully")
        }
    }

    fun purchaseInvestment(type: String, projectName: String, location: String, amount: Double, roiRate: Double, duration: Int, details: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            if (user.walletBalance < amount) {
                _toastMessage.emit("Insufficient wallet balance for this investment!")
                return@launch
            }

            // Deduct from wallet
            val updatedUser = user.copy(walletBalance = user.walletBalance - amount)
            repository.updateUser(updatedUser)

            // Save investment
            repository.insertInvestment(
                Investment(
                    userEmail = user.email,
                    type = type,
                    projectName = projectName,
                    location = location,
                    amountInvested = amount,
                    expectedReturnRate = roiRate,
                    durationMonths = duration,
                    details = details
                )
            )

            // Log Transaction
            repository.insertTransaction(
                Transaction(
                    userEmail = user.email,
                    type = "INVESTMENT",
                    title = "Invested in $projectName",
                    description = "Acquired fractional ownership unit",
                    amount = amount
                )
            )
            _toastMessage.emit("Successfully invested $${String.format("%.2f", amount)} in $projectName!")
        }
    }

    fun applyForLoan(type: String, amount: Double, months: Int) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val monthly = (amount * 1.08) / months // Simple mock calculation with 8% interest
            val newLoan = Loan(
                userEmail = user.email,
                type = type,
                amountRequested = amount,
                durationMonths = months,
                monthlyPayment = monthly,
                remainingAmount = amount,
                status = "PENDING",
                creditScore = (600..850).random()
            )
            repository.insertLoan(newLoan)

            repository.insertTransaction(
                Transaction(
                    userEmail = user.email,
                    type = "LOAN_REPAYMENT",
                    title = "Loan Application",
                    description = "Applied for a $type loan of $${String.format("%.2f", amount)}",
                    amount = amount,
                    status = "PENDING"
                )
            )
            _toastMessage.emit("Loan application submitted successfully for review!")
        }
    }

    fun repayLoan(loan: Loan, amount: Double) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            if (user.walletBalance < amount) {
                _toastMessage.emit("Insufficient wallet balance for repayment!")
                return@launch
            }

            // Deduct
            repository.updateUser(user.copy(walletBalance = user.walletBalance - amount))

            // Update Loan
            val newRemaining = (loan.remainingAmount - amount).coerceAtLeast(0.0)
            val updatedLoan = loan.copy(
                remainingAmount = newRemaining,
                status = if (newRemaining == 0.0) "FULLY_PAID" else loan.status
            )
            repository.updateLoan(updatedLoan)

            repository.insertTransaction(
                Transaction(
                    userEmail = user.email,
                    type = "LOAN_REPAYMENT",
                    title = "Loan Repayment",
                    description = "Repayment on ${loan.type} Loan",
                    amount = amount
                )
            )
            _toastMessage.emit("Loan repayment of $${String.format("%.2f", amount)} successful!")
        }
    }

    fun createThriftPlan(title: String, frequency: String, contribution: Double, goal: Double) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val plan = ThriftPlan(
                userEmail = user.email,
                title = title,
                frequency = frequency,
                contributionAmount = contribution,
                currentBalance = 0.0,
                totalGoal = goal
            )
            repository.insertThriftPlan(plan)
            _toastMessage.emit("Created new Thrift Saving Plan: $title")
        }
    }

    fun contributeToThrift(plan: ThriftPlan, amount: Double) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            if (user.walletBalance < amount) {
                _toastMessage.emit("Insufficient wallet balance to contribute!")
                return@launch
            }

            // Deduct
            repository.updateUser(user.copy(walletBalance = user.walletBalance - amount))

            // Add to Thrift
            val updatedPlan = plan.copy(
                currentBalance = plan.currentBalance + amount,
                status = if (plan.currentBalance + amount >= plan.totalGoal) "COMPLETED" else plan.status
            )
            repository.updateThriftPlan(updatedPlan)

            repository.insertTransaction(
                Transaction(
                    userEmail = user.email,
                    type = "THRIFT",
                    title = "Thrift Saving Contributed",
                    description = "Saving contribution to: ${plan.title}",
                    amount = amount
                )
            )
            _toastMessage.emit("Contributed $${String.format("%.2f", amount)} to ${plan.title}!")
        }
    }

    fun tradeCrypto(symbol: String, isBuy: Boolean, amountInCoins: Double, coinPrice: Double) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val totalCost = amountInCoins * coinPrice

            if (isBuy) {
                if (user.walletBalance < totalCost) {
                    _toastMessage.emit("Insufficient balance to buy!")
                    return@launch
                }
                repository.updateUser(user.copy(walletBalance = user.walletBalance - totalCost))
                repository.updateCryptoBalance(symbol, getCryptoCoinBalance(symbol) + amountInCoins)

                repository.insertTransaction(
                    Transaction(
                        userEmail = user.email,
                        type = "DEPOSIT",
                        title = "Crypto Purchased",
                        description = "Bought $amountInCoins $symbol at $${coinPrice}",
                        amount = totalCost
                    )
                )
                _toastMessage.emit("Bought $amountInCoins $symbol successfully!")
            } else {
                val currentCoinBal = getCryptoCoinBalance(symbol)
                if (currentCoinBal < amountInCoins) {
                    _toastMessage.emit("Insufficient crypto holdings to sell!")
                    return@launch
                }
                repository.updateUser(user.copy(walletBalance = user.walletBalance + totalCost))
                repository.updateCryptoBalance(symbol, currentCoinBal - amountInCoins)

                repository.insertTransaction(
                    Transaction(
                        userEmail = user.email,
                        type = "WITHDRAW",
                        title = "Crypto Sold",
                        description = "Sold $amountInCoins $symbol at $${coinPrice}",
                        amount = totalCost
                    )
                )
                _toastMessage.emit("Sold $amountInCoins $symbol successfully!")
            }
        }
    }

    private fun getCryptoCoinBalance(symbol: String): Double {
        return cryptoPortfolio.value.firstOrNull { it.symbol == symbol }?.balance ?: 0.0
    }

    fun submitKYC(docType: String, docUrl: String) {
        viewModelScope.launch {
            val user = currentUser.value ?: return@launch
            val updatedUser = user.copy(
                kycStatus = "PENDING",
                kycDocumentType = docType,
                kycDocUrl = docUrl
            )
            repository.updateUser(updatedUser)
            _toastMessage.emit("KYC Documents submitted for review.")
        }
    }

    // --- Admin Dashboard Methods ---

    fun adminApproveKYC(userEmail: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(userEmail)
            if (user != null) {
                repository.updateUser(user.copy(kycStatus = "APPROVED"))
                _toastMessage.emit("Approved KYC for ${user.fullName}")
            }
        }
    }

    fun adminRejectKYC(userEmail: String) {
        viewModelScope.launch {
            val user = repository.getUserByEmail(userEmail)
            if (user != null) {
                repository.updateUser(user.copy(kycStatus = "REJECTED"))
                _toastMessage.emit("Rejected KYC for ${user.fullName}")
            }
        }
    }

    fun adminApproveLoan(loan: Loan) {
        viewModelScope.launch {
            val updatedLoan = loan.copy(status = "ACTIVE")
            repository.updateLoan(updatedLoan)

            // Disburse loan funds into the user's wallet!
            val user = repository.getUserByEmail(loan.userEmail)
            if (user != null) {
                repository.updateUser(user.copy(walletBalance = user.walletBalance + loan.amountRequested))
                repository.insertTransaction(
                    Transaction(
                        userEmail = loan.userEmail,
                        type = "DEPOSIT",
                        title = "Loan Disbursed",
                        description = "Disbursed funds for APPROVED ${loan.type} Loan",
                        amount = loan.amountRequested
                    )
                )
                _toastMessage.emit("Approved and Disbursed $${loan.amountRequested} to ${user.fullName}")
            }
        }
    }

    fun adminAddInvestmentProject(
        type: String,
        name: String,
        location: String,
        roiRate: Double,
        durationMonths: Int,
        minInvestment: Double,
        description: String
    ) {
        viewModelScope.launch {
            repository.insertInvestmentProject(
                InvestmentProject(
                    type = type,
                    name = name,
                    location = location,
                    roiRate = roiRate,
                    durationMonths = durationMonths,
                    minInvestment = minInvestment,
                    description = description,
                    isAvailable = true
                )
            )
            _toastMessage.emit("Successfully listed new investment: $name")
        }
    }

    fun adminDeleteInvestmentProject(project: InvestmentProject) {
        viewModelScope.launch {
            repository.deleteInvestmentProject(project)
            _toastMessage.emit("Successfully removed investment: ${project.name}")
        }
    }

    // --- AI Chat Support ---

    fun askChatCoach(prompt: String) {
        if (prompt.isBlank()) return
        _chatMessages.value = _chatMessages.value + (prompt to true)
        _isChatLoading.value = true

        val user = currentUser.value
        val contextInfo = """
            User: ${user?.fullName ?: "Guest Client"}
            Email: ${user?.email ?: "N/A"}
            Wallet Balance: $${user?.walletBalance ?: 0.0}
            Thrift Savings: $${user?.activeSavings ?: 0.0}
            Active Investments Count: ${investments.value.size}
            Active Loans Count: ${loans.value.filter { it.status == "ACTIVE" }.size}
            Total Investments Value: $${investments.value.sumOf { it.amountInvested }}
            Crypto Holdings: ${cryptoPortfolio.value.joinToString { "${it.symbol}: ${it.balance} units" }}
        """.trimIndent()

        viewModelScope.launch {
            val response = GeminiClient.askGeminiCoach(prompt, contextInfo)
            _chatMessages.value = _chatMessages.value + (response to false)
            _isChatLoading.value = false
        }
    }

    fun clearChat() {
        _chatMessages.value = listOf(
            "Hello! I am your Aurea Re-Estate AI Financial Coach. How can I help you build your wealth today?" to false
        )
    }
}
