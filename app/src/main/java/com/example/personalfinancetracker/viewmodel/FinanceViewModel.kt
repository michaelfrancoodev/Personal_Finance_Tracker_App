package com.example.personalfinancetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.personalfinancetracker.data.entity.Transaction
import com.example.personalfinancetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

/**
 * FinanceViewModel - Brain of the app
 *
 * Responsibilities:
 * - Hold UI state (transactions, balance, search query)
 * - Handle user actions (add, edit, delete, search)
 * - Expose data to UI via StateFlow
 * - Survives configuration changes (screen rotation)
 */
class FinanceViewModel(private val repository: TransactionRepository) : ViewModel() {

    // ========== PRIVATE STATE (Internal) ==========

    private val _transactions = MutableStateFlow<List<Transaction>>(emptyList())
    private val _totalBalance = MutableStateFlow(0.0)
    private val _totalIncome = MutableStateFlow(0.0)
    private val _totalExpenses = MutableStateFlow(0.0)
    private val _searchQuery = MutableStateFlow("")

    // ========== PUBLIC STATE (Observable by UI) ==========

    val transactions: StateFlow<List<Transaction>> = _transactions.asStateFlow()
    val totalBalance: StateFlow<Double> = _totalBalance.asStateFlow()
    val totalIncome: StateFlow<Double> = _totalIncome.asStateFlow()
    val totalExpenses: StateFlow<Double> = _totalExpenses.asStateFlow()
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    /**
     * Filtered transactions based on search query
     *
     * Combines two flows: transactions + searchQuery
     * Whenever either changes, the result updates automatically
     *
     * Searches in: title, category, note (case-insensitive)
     */
    val filteredTransactions: StateFlow<List<Transaction>> =
        _transactions.combine(_searchQuery) { transactionList, query ->
            if (query.isBlank()) {
                transactionList  // Show all if no search
            } else {
                transactionList.filter { transaction ->
                    transaction.title.contains(query, ignoreCase = true) ||
                            transaction.category.contains(query, ignoreCase = true) ||
                            transaction.note.contains(query, ignoreCase = true)
                }
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),  // Keep alive 5s after UI stops observing
            initialValue = emptyList()
        )

    // ========== INITIALIZATION ==========

    init {
        // Collect all transactions from database
        viewModelScope.launch {
            repository.allTransactions.collect { list ->
                _transactions.value = list
            }
        }

        // Collect total balance
        viewModelScope.launch {
            repository.totalBalance.collect { balance ->
                _totalBalance.value = balance ?: 0.0  // Default to 0 if null
            }
        }

        // Collect total income
        viewModelScope.launch {
            repository.totalIncome.collect { income ->
                _totalIncome.value = income ?: 0.0
            }
        }

        // Collect total expenses
        viewModelScope.launch {
            repository.totalExpenses.collect { expenses ->
                _totalExpenses.value = expenses ?: 0.0
            }
        }
    }

    // ========== USER ACTIONS ==========

    /**
     * Add a new transaction
     */
    fun addTransaction(
        title: String,
        amount: Double,
        category: String,
        isExpense: Boolean,
        note: String = ""
    ) = viewModelScope.launch {
        val transaction = Transaction(
            title = title,
            amount = amount,
            category = category,
            isExpense = isExpense,
            date = System.currentTimeMillis(),
            note = note
        )
        repository.insert(transaction)
    }

    /**
     * Update existing transaction
     */
    fun updateTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.update(transaction)
    }

    /**
     * Delete transaction
     */
    fun deleteTransaction(transaction: Transaction) = viewModelScope.launch {
        repository.delete(transaction)
    }

    /**
     * Update search query (triggers automatic filtering)
     */
    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * Clear search
     */
    fun clearSearch() {
        _searchQuery.value = ""
    }

    /**
     * Delete all transactions (use with caution)
     */
    fun deleteAllTransactions() = viewModelScope.launch {
        repository.deleteAll()
    }
}