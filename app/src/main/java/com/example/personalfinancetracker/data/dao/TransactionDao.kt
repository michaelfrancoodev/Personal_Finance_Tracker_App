package com.example.personalfinancetracker.data.dao

import androidx.room.*
import com.example.personalfinancetracker.data.entity.Transaction
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) - Contains all database queries
 *
 * This interface tells Room how to interact with the database
 * Room automatically implements these functions at compile time
 */
@Dao
interface TransactionDao {

    /**
     * Get all transactions ordered by date (newest first)
     * Flow automatically notifies UI when data changes
     */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<Transaction>>

    /**
     * Calculate total balance using SQL
     *
     * Logic:
     * - If isExpense = 1 (true), multiply amount by -1 (subtract)
     * - If isExpense = 0 (false), keep amount positive (add)
     * - SUM all results = Net Balance
     *
     * Example:
     * Income: +50000
     * Expense: -10000
     * Balance: 40000
     */
    @Query("""
        SELECT SUM(
            CASE 
                WHEN isExpense = 1 THEN -amount 
                ELSE amount 
            END
        ) FROM transactions
    """)
    fun getTotalBalance(): Flow<Double?>

    /**
     * Calculate total income only (where isExpense = 0)
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 0")
    fun getTotalIncome(): Flow<Double?>

    /**
     * Calculate total expenses only (where isExpense = 1)
     */
    @Query("SELECT SUM(amount) FROM transactions WHERE isExpense = 1")
    fun getTotalExpenses(): Flow<Double?>

    /**
     * Get transactions filtered by category
     */
    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<Transaction>>

    /**
     * Insert a new transaction
     * 'suspend' means this runs on a background thread
     */
    @Insert
    suspend fun insertTransaction(transaction: Transaction)

    /**
     * Update an existing transaction
     */
    @Update
    suspend fun updateTransaction(transaction: Transaction)

    /**
     * Delete a transaction
     */
    @Delete
    suspend fun deleteTransaction(transaction: Transaction)

    /**
     * Delete all transactions (useful for testing or reset)
     */
    @Query("DELETE FROM transactions")
    suspend fun deleteAllTransactions()
}