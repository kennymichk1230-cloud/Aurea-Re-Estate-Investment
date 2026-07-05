package com.example.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appDao: AppDao) {

    val primaryUser: Flow<LocalUser?> = appDao.getPrimaryUser()
    val transactions: Flow<List<Transaction>> = appDao.getTransactionsFlow()
    val investments: Flow<List<Investment>> = appDao.getInvestmentsFlow()
    val loans: Flow<List<Loan>> = appDao.getLoansFlow()
    val thriftPlans: Flow<List<ThriftPlan>> = appDao.getThriftPlansFlow()
    val cryptoPortfolio: Flow<List<CryptoToken>> = appDao.getCryptoPortfolioFlow()
    val allUsers: Flow<List<LocalUser>> = appDao.getAllUsersFlow()
    val investmentProjects: Flow<List<InvestmentProject>> = appDao.getInvestmentProjectsFlow()

    fun getTransactionsByUser(email: String): Flow<List<Transaction>> = appDao.getTransactionsByUserFlow(email)
    fun getInvestmentsByUser(email: String): Flow<List<Investment>> = appDao.getInvestmentsByUserFlow(email)
    fun getLoansByUser(email: String): Flow<List<Loan>> = appDao.getLoansByUserFlow(email)
    fun getThriftPlansByUser(email: String): Flow<List<ThriftPlan>> = appDao.getThriftPlansByUserFlow(email)

    suspend fun getUserByEmail(email: String): LocalUser? = appDao.getUserByEmail(email)
    suspend fun getAllUsersList(): List<LocalUser> = appDao.getAllUsers()

    suspend fun insertUser(user: LocalUser) = appDao.insertUser(user)
    suspend fun updateUser(user: LocalUser) = appDao.updateUser(user)

    suspend fun insertTransaction(tx: Transaction) = appDao.insertTransaction(tx)

    suspend fun insertInvestment(inv: Investment) = appDao.insertInvestment(inv)
    suspend fun updateInvestment(inv: Investment) = appDao.updateInvestment(inv)

    suspend fun insertLoan(loan: Loan) = appDao.insertLoan(loan)
    suspend fun updateLoan(loan: Loan) = appDao.updateLoan(loan)

    suspend fun insertThriftPlan(plan: ThriftPlan) = appDao.insertThriftPlan(plan)
    suspend fun updateThriftPlan(plan: ThriftPlan) = appDao.updateThriftPlan(plan)

    suspend fun insertCryptoToken(token: CryptoToken) = appDao.insertCryptoToken(token)
    suspend fun updateCryptoBalance(symbol: String, newBalance: Double) = appDao.updateCryptoBalance(symbol, newBalance)

    suspend fun insertInvestmentProject(project: InvestmentProject) = appDao.insertInvestmentProject(project)
    suspend fun deleteInvestmentProject(project: InvestmentProject) = appDao.deleteInvestmentProject(project)
}
