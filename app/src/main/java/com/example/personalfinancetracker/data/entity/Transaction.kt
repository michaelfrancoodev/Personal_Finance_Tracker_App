package com.example.personalfinancetracker.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Transaction Entity - Represents a single financial transaction
 *
 * This is our database table where we store all income and expense records
 * Each row in the table represents one transaction
 */
@Entity(tableName = "transactions")
data class Transaction(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,

    val title: String,              // e.g., "Grocery Shopping"
    val amount: Double,             // e.g., 25000.50
    val category: String,           // e.g., "Food"
    val isExpense: Boolean = true,  // true = money out, false = money in
    val date: Long = System.currentTimeMillis(), // timestamp in milliseconds
    val note: String = ""           // optional additional details
)