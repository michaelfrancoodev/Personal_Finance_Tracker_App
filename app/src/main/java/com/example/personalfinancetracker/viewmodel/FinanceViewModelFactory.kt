package com.example.personalfinancetracker.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.personalfinancetracker.data.repository.TransactionRepository

/**
 * ViewModel Factory - Creates ViewModel with dependencies
 *
 * Why needed?
 * - ViewModels can't have constructor parameters by default
 * - Factory pattern allows us to inject repository
 * - Android framework uses this to create ViewModels
 */
class FinanceViewModelFactory(
    private val repository: TransactionRepository
) : ViewModelProvider.Factory {

    /**
     * Create ViewModel instance with repository injected
     *
     * @param modelClass The class of ViewModel to create
     * @return Instance of FinanceViewModel
     * @throws IllegalArgumentException if wrong ViewModel class
     */
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FinanceViewModel::class.java)) {
            return FinanceViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}