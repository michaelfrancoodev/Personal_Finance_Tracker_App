package com.example.personalfinancetracker.data.repository

import com.example.personalfinancetracker.data.dao.TransactionDao
import com.example.personalfinancetracker.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Repository Pattern - Single Source of Truth
 *
 * Why use Repository?
 * - Separates data logic from UI logic
 * - Makes testing easier (can mock repository)
 * - Can switch data sources (local/remote) without changing ViewModel
 * - Provides clean API for data operations
 *
 * The ViewModel talks to Repository, Repository talks to DAO
 */
class TransactionRepository(private val dao: TransactionDao) {

    /**
     * Expose all data streams as Flow
     * Flow automatically updates UI when data changes in database
     */
    val allTransactions: Flow<List<Transaction>> = dao.getAllTransactions()
    val totalBalance: Flow<Double?> = dao.getTotalBalance()
    val totalIncome: Flow<Double?> = dao.getTotalIncome()
    val totalExpenses: Flow<Double?> = dao.getTotalExpenses()

    /**
     * Get transactions by specific category
     */
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>> {
        return dao.getTransactionsByCategory(category)
    }

    /**
     * Insert new transaction
     * Suspend function must be called from coroutine
     */
    suspend fun insert(transaction: Transaction) {
        dao.insertTransaction(transaction)
    }

    /**
     * Update existing transaction
     */
    suspend fun update(transaction: Transaction) {
        dao.updateTransaction(transaction)
    }

    /**
     * Delete transaction
     */
    suspend fun delete(transaction: Transaction) {
        dao.deleteTransaction(transaction)
    }

    /**
     * Delete all transactions (use with caution)
     */
    suspend fun deleteAll() {
        dao.deleteAllTransactions()
    }
}