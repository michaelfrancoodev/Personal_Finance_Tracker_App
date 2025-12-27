package com.example.personalfinancetracker.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.personalfinancetracker.data.entity.Transaction
import com.example.personalfinancetracker.ui.theme.*

/**
 * Add/Edit Transaction Dialog
 *
 * Beautiful form with validation and smooth animations
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddTransactionDialog(
    title: String,
    transaction: Transaction?,
    onDismiss: () -> Unit,
    onSave: (String, Double, String, Boolean, String) -> Unit
) {
    // Form state
    var transactionTitle by remember { mutableStateOf(transaction?.title ?: "") }
    var amount by remember { mutableStateOf(transaction?.amount?.toString() ?: "") }
    var category by remember { mutableStateOf(transaction?.category ?: "Food") }
    var isExpense by remember { mutableStateOf(transaction?.isExpense ?: true) }
    var note by remember { mutableStateOf(transaction?.note ?: "") }
    var showCategoryMenu by remember { mutableStateOf(false) }

    // Validation
    val isTitleValid = transactionTitle.isNotBlank()
    val isAmountValid = amount.isNotBlank() &&
            amount.toDoubleOrNull() != null &&
            amount.toDouble() > 0
    val isValid = isTitleValid && isAmountValid

    // Categories with icons
    val categories = listOf(
        "Food", "Transport", "Shopping", "Entertainment",
        "Bills", "Salary", "Health", "Education", "Other"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        if (transaction == null) Icons.Default.Add else Icons.Default.Edit,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                Text(
                    title,
                    fontWeight = FontWeight.Bold
                )
            }
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Title Input
                OutlinedTextField(
                    value = transactionTitle,
                    onValueChange = { transactionTitle = it },
                    label = { Text("Title *") },
                    leadingIcon = {
                        Icon(Icons.Default.Description, null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    isError = !isTitleValid && transactionTitle.isNotEmpty(),
                    supportingText = if (!isTitleValid && transactionTitle.isNotEmpty()) {
                        { Text("Title is required") }
                    } else null,
                    shape = RoundedCornerShape(12.dp)
                )

                // Amount Input
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d{0,2}$"))) {
                            amount = it
                        }
                    },
                    label = { Text("Amount (TSh) *") },
                    leadingIcon = {
                        Icon(Icons.Default.AttachMoney, null)
                    },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    isError = !isAmountValid && amount.isNotEmpty(),
                    supportingText = if (!isAmountValid && amount.isNotEmpty()) {
                        { Text("Enter valid amount greater than 0") }
                    } else null,
                    shape = RoundedCornerShape(12.dp)
                )

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category *") },
                        leadingIcon = {
                            Icon(Icons.Default.Category, null)
                        },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        categories.forEach { cat ->
                            DropdownMenuItem(
                                text = {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        Icon(
                                            getCategoryIcon(cat),
                                            contentDescription = null,
                                            modifier = Modifier.size(20.dp)
                                        )
                                        Text(cat)
                                    }
                                },
                                onClick = {
                                    category = cat
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                // Transaction Type Toggle
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        "Transaction Type",
                        style = MaterialTheme.typography.labelMedium,
                        color = TextSecondary,
                        fontWeight = FontWeight.Medium
                    )
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        // Income Chip
                        FilterChip(
                            selected = !isExpense,
                            onClick = { isExpense = false },
                            label = {
                                Text(
                                    "Income",
                                    fontWeight = if (!isExpense) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.TrendingUp,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (!isExpense) IncomeGreen else TextSecondary
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = IncomeLight,
                                selectedLabelColor = IncomeDark,
                                selectedLeadingIconColor = IncomeGreen
                            )
                        )

                        // Expense Chip
                        FilterChip(
                            selected = isExpense,
                            onClick = { isExpense = true },
                            label = {
                                Text(
                                    "Expense",
                                    fontWeight = if (isExpense) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Default.TrendingDown,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = if (isExpense) ExpenseRed else TextSecondary
                                )
                            },
                            modifier = Modifier.weight(1f),
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = ExpenseLight,
                                selectedLabelColor = ExpenseDark,
                                selectedLeadingIconColor = ExpenseRed
                            )
                        )
                    }
                }

                // Note Input (Optional)
                OutlinedTextField(
                    value = note,
                    onValueChange = { note = it },
                    label = { Text("Note (Optional)") },
                    leadingIcon = {
                        Icon(Icons.Default.Notes, null)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    shape = RoundedCornerShape(12.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (isValid) {
                        onSave(
                            transactionTitle.trim(),
                            amount.toDouble(),
                            category,
                            isExpense,
                            note.trim()
                        )
                    }
                },
                enabled = isValid,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Check,
                    null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Save", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    Icons.Default.Close,
                    null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(24.dp)
    )
}

/**
 * Get category icon helper
 */
private fun getCategoryIcon(category: String): androidx.compose.ui.graphics.vector.ImageVector {
    return when (category.lowercase()) {
        "food" -> Icons.Default.Restaurant
        "transport" -> Icons.Default.DirectionsCar
        "shopping" -> Icons.Default.ShoppingBag
        "entertainment" -> Icons.Default.Movie
        "bills" -> Icons.Default.Receipt
        "salary" -> Icons.Default.AccountBalance
        "health" -> Icons.Default.LocalHospital
        "education" -> Icons.Default.School
        "other" -> Icons.Default.Category
        else -> Icons.Default.AttachMoney
    }
}