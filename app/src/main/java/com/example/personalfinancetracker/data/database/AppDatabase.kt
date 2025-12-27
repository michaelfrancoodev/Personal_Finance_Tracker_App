package com.example.personalfinancetracker.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.personalfinancetracker.data.dao.TransactionDao
import com.example.personalfinancetracker.data.entity.Transaction

/**
 * Room Database Configuration
 *
 * This is the main database holder
 * Room generates all the implementation code automatically
 */
@Database(
    entities = [Transaction::class],  // List of all tables
    version = 1,                      // Increment when schema changes
    exportSchema = false              // Disable schema export for simplicity
)
abstract class AppDatabase : RoomDatabase() {

    /**
     * Provide access to the DAO
     * Room automatically creates the implementation
     */
    abstract fun transactionDao(): TransactionDao

    companion object {
        /**
         * Singleton instance
         * @Volatile ensures all threads see the same value
         */
        @Volatile
        private var INSTANCE: AppDatabase? = null

        /**
         * Get database instance (create if doesn't exist)
         *
         * Uses double-check locking to ensure thread safety
         * Only one instance of database exists throughout app lifetime
         */
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "finance_tracker_database"  // Database file name
                )
                    .fallbackToDestructiveMigration()  // Delete & recreate if schema changes
                    .build()

                INSTANCE = instance
                instance
            }
        }
    }
}