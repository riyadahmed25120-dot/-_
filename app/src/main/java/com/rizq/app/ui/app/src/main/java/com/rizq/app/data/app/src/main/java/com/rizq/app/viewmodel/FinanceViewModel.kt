package com.rizq.app.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Room
import com.rizq.app.data.TransactionEntity
import com.rizq.app.data.TransactionDao
import androidx.room.Database
import androidx.room.RoomDatabase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar

@Database(entities = [TransactionEntity::class], version = 1, exportSchema = false)
abstract class RizqDatabase : RoomDatabase() {
    abstract fun transactionDao(): TransactionDao
    companion object {
        @Volatile private var INSTANCE: RizqDatabase? = null
        fun getDatabase(context: Application): RizqDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    RizqDatabase::class.java,
                    "rizq_db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

class FinanceViewModel(application: Application) : AndroidViewModel(application) {
    private val dao = RizqDatabase.getDatabase(application).transactionDao()
    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    private val _bdtRate = MutableStateFlow(0.033) // 1 SAR = ~33 BDT dummy rate
    val bdtRate: StateFlow<Double> = _bdtRate.asStateFlow()

    fun addTransaction(title: String, amount: Double, type: String, currency: String) {
        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            val date = calendar.timeInMillis
            val monthYear = "${calendar.get(Calendar.MONTH) + 1}/${calendar.get(Calendar.YEAR)}"
            dao.insertTransaction(
                TransactionEntity(
                    title = title,
                    amount = amount,
                    type = type,
                    currency = currency,
                    date = date,
                    monthYear = monthYear
                )
            )
        }
    }

    fun deleteTransaction(transaction: TransactionEntity) {
        viewModelScope.launch {
            dao.deleteTransaction(transaction)
        }
    }
}
