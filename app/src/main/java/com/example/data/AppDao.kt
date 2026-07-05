package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface AppDao {
    // --- User Operations ---
    @Query("SELECT * FROM local_users LIMIT 1")
    fun getPrimaryUser(): Flow<LocalUser?>

    @Query("SELECT * FROM local_users WHERE email = :email")
    suspend fun getUserByEmail(email: String): LocalUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: LocalUser)

    @Update
    suspend fun updateUser(user: LocalUser)

    @Query("SELECT * FROM local_users")
    fun getAllUsersFlow(): Flow<List<LocalUser>>

    @Query("SELECT * FROM local_users")
    suspend fun getAllUsers(): List<LocalUser>

    // --- Transaction Operations ---
    @Query("SELECT * FROM transactions ORDER BY timestamp DESC")
    fun getTransactionsFlow(): Flow<List<Transaction>>

    @Query("SELECT * FROM transactions WHERE userEmail = :email ORDER BY timestamp DESC")
    fun getTransactionsByUserFlow(email: String): Flow<List<Transaction>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(tx: Transaction)

    // --- Investment Operations ---
    @Query("SELECT * FROM investments ORDER BY dateInvested DESC")
    fun getInvestmentsFlow(): Flow<List<Investment>>

    @Query("SELECT * FROM investments WHERE userEmail = :email ORDER BY dateInvested DESC")
    fun getInvestmentsByUserFlow(email: String): Flow<List<Investment>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestment(inv: Investment)

    @Update
    suspend fun updateInvestment(inv: Investment)

    // --- Loan Operations ---
    @Query("SELECT * FROM loans ORDER BY dateApplied DESC")
    fun getLoansFlow(): Flow<List<Loan>>

    @Query("SELECT * FROM loans WHERE userEmail = :email ORDER BY dateApplied DESC")
    fun getLoansByUserFlow(email: String): Flow<List<Loan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLoan(loan: Loan)

    @Update
    suspend fun updateLoan(loan: Loan)

    // --- Thrift Operations ---
    @Query("SELECT * FROM thrift_contributions ORDER BY dateStarted DESC")
    fun getThriftPlansFlow(): Flow<List<ThriftPlan>>

    @Query("SELECT * FROM thrift_contributions WHERE userEmail = :email ORDER BY dateStarted DESC")
    fun getThriftPlansByUserFlow(email: String): Flow<List<ThriftPlan>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertThriftPlan(plan: ThriftPlan)

    @Update
    suspend fun updateThriftPlan(plan: ThriftPlan)

    // --- Crypto Operations ---
    @Query("SELECT * FROM crypto_portfolios ORDER BY balance * livePrice DESC")
    fun getCryptoPortfolioFlow(): Flow<List<CryptoToken>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCryptoToken(token: CryptoToken)

    @Query("UPDATE crypto_portfolios SET balance = :newBalance WHERE symbol = :symbol")
    suspend fun updateCryptoBalance(symbol: String, newBalance: Double)

    // --- Investment Project Operations (Admin Managed) ---
    @Query("SELECT * FROM investment_projects ORDER BY id DESC")
    fun getInvestmentProjectsFlow(): Flow<List<InvestmentProject>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInvestmentProject(project: InvestmentProject)

    @Delete
    suspend fun deleteInvestmentProject(project: InvestmentProject)
}
