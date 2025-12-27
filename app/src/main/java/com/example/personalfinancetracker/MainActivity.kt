package com.example.personalfinancetracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.personalfinancetracker.data.database.AppDatabase
import com.example.personalfinancetracker.data.repository.TransactionRepository
import com.example.personalfinancetracker.ui.screens.MainFinanceScreen
import com.example.personalfinancetracker.ui.theme.FinanceTrackerTheme
import com.example.personalfinancetracker.viewmodel.FinanceViewModel
import com.example.personalfinancetracker.viewmodel.FinanceViewModelFactory

/**
 * MainActivity - Entry point of Finance Tracker
 *
 * Sets up:
 * - Splash Screen
 * - Room Database
 * - Repository
 * - ViewModel Factory
 * - Compose UI with custom theme
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Install splash screen BEFORE super.onCreate()
        installSplashScreen()

        super.onCreate(savedInstanceState)

        // Enable edge-to-edge display (modern Android)
        enableEdgeToEdge()

        // Initialize database
        val database = AppDatabase.getDatabase(this)
        val dao = database.transactionDao()

        // Create repository
        val repository = TransactionRepository(dao)

        // Create ViewModel factory
        val factory = FinanceViewModelFactory(repository)

        setContent {
            // Apply custom theme
            FinanceTrackerTheme {
                Surface(
                    color = MaterialTheme.colorScheme.background
                ) {
                    // Get ViewModel instance
                    val financeViewModel: FinanceViewModel = viewModel(factory = factory)

                    // Display main screen
                    MainFinanceScreen(financeViewModel)
                }
            }
        }
    }
}