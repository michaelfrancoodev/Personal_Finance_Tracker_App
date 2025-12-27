package com.example.personalfinancetracker.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.personalfinancetracker.data.entity.Transaction
import com.example.personalfinancetracker.ui.components.*
import com.example.personalfinancetracker.ui.theme.TextSecondary
import com.example.personalfinancetracker.viewmodel.FinanceViewModel

/**
 * Main Finance Screen - Beautiful, smooth, and functional
 *
 * Features:
 * - Stunning gradient balance card
 * - Real-time search
 * - Smooth animations
 * - Empty states
 * - Color-coded transactions
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainFinanceScreen(viewModel: FinanceViewModel) {
    // Collect states
    val transactions by viewModel.filteredTransactions.collectAsState()
    val totalBalance by viewModel.totalBalance.collectAsState()
    val totalIncome by viewModel.totalIncome.collectAsState()
    val totalExpenses by viewModel.totalExpenses.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    // Dialog states
    var showAddDialog by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    var selectedTransaction by remember { mutableStateOf<Transaction?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            "Finance Tracker",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            "${transactions.size} transactions",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = {
                    Icon(Icons.Default.Add, contentDescription = null)
                },
                text = {
                    Text(
                        "Add Transaction",
                        fontWeight = FontWeight.Bold
                    )
                },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = RoundedCornerShape(16.dp)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Balance Card
            AnimatedVisibility(
                visible = true,
                enter = fadeIn() + slideInVertically()
            ) {
                BalanceCard(
                    totalBalance = totalBalance,
                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }

            // Search Bar
            SearchBar(
                query = searchQuery,
                onQueryChange = { viewModel.updateSearchQuery(it) },
                onClearClick = { viewModel.clearSearch() },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            )

            // Transactions List
            if (transactions.isEmpty()) {
                EmptyState(
                    isSearching = searchQuery.isNotBlank(),
                    onClearSearch = { viewModel.clearSearch() }
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(
                        items = transactions,
                        key = { it.id }
                    ) { transaction ->
                        AnimatedVisibility(
                            visible = true,
                            enter = fadeIn() + expandVertically(),
                            exit = fadeOut() + shrinkVertically()
                        ) {
                            TransactionItem(
                                transaction = transaction,
                                onEdit = {
                                    selectedTransaction = transaction
                                    showEditDialog = true
                                },
                                onDelete = {
                                    selectedTransaction = transaction
                                    showDeleteDialog = true
                                }
                            )
                        }
                    }

                    // Extra space for FAB
                    item {
                        Spacer(modifier = Modifier.height(80.dp))
                    }
                }
            }
        }

        // Add Transaction Dialog
        if (showAddDialog) {
            AddTransactionDialog(
                title = "Add Transaction",
                transaction = null,
                onDismiss = { showAddDialog = false },
                onSave = { title, amount, category, isExpense, note ->
                    viewModel.addTransaction(title, amount, category, isExpense, note)
                    showAddDialog = false
                }
            )
        }

        // Edit Transaction Dialog
        if (showEditDialog && selectedTransaction != null) {
            AddTransactionDialog(
                title = "Edit Transaction",
                transaction = selectedTransaction,
                onDismiss = {
                    showEditDialog = false
                    selectedTransaction = null
                },
                onSave = { title, amount, category, isExpense, note ->
                    viewModel.updateTransaction(
                        selectedTransaction!!.copy(
                            title = title,
                            amount = amount,
                            category = category,
                            isExpense = isExpense,
                            note = note
                        )
                    )
                    showEditDialog = false
                    selectedTransaction = null
                }
            )
        }

        // Delete Confirmation Dialog
        if (showDeleteDialog && selectedTransaction != null) {
            AlertDialog(
                onDismissRequest = {
                    showDeleteDialog = false
                    selectedTransaction = null
                },
                icon = {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                },
                title = {
                    Text(
                        "Delete Transaction?",
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Text("Are you sure you want to delete \"${selectedTransaction?.title}\"? This action cannot be undone.")
                },
                confirmButton = {
                    Button(
                        onClick = {
                            viewModel.deleteTransaction(selectedTransaction!!)
                            showDeleteDialog = false
                            selectedTransaction = null
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error
                        ),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Delete", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(
                        onClick = {
                            showDeleteDialog = false
                            selectedTransaction = null
                        },
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("Cancel")
                    }
                },
                shape = RoundedCornerShape(24.dp)
            )
        }
    }
}

/**
 * Empty State - Beautiful state when no transactions
 */
@Composable
fun EmptyState(
    isSearching: Boolean,
    onClearSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            if (isSearching) Icons.Default.SearchOff else Icons.Default.AccountBalanceWallet,
            contentDescription = null,
            modifier = Modifier.size(120.dp),
            tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.3f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            if (isSearching) "No Results Found" else "No Transactions Yet",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            if (isSearching)
                "Try different search terms"
            else
                "Click the + button to add your first transaction",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        if (isSearching) {
            Spacer(modifier = Modifier.height(16.dp))
            FilledTonalButton(
                onClick = onClearSearch,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(Icons.Default.Close, contentDescription = null)
                Spacer(modifier = Modifier.width(6.dp))
                Text("Clear Search")
            }
        }
    }
}