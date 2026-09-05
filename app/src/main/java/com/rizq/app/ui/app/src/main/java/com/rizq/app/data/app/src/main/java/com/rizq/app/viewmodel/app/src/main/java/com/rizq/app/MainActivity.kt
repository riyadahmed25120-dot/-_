package com.rizq.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.rizq.app.data.TransactionEntity
import com.rizq.app.ui.theme.RizqTheme
import com.rizq.app.viewmodel.FinanceViewModel
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RizqTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    MainScreen()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(viewModel: FinanceViewModel = viewModel()) {
    val transactions by viewModel.allTransactions.collectAsState(initial = emptyList())
    val bdtRate by viewModel.bdtRate.collectAsState()

    var title by remember { mutableStateOf("") }
    var amountStr by remember { mutableStateOf("") }
    var type by remember { mutableStateOf("Expense") }
    var currency by remember { mutableStateOf("SAR") }

    val totalIncome = transactions.filter { it.type == "Income" }.sumOf { it.amount }
    val totalExpense = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
    val balance = totalIncome - totalExpense

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Rizq - رزق (Financial Tracker)", fontWeight = FontWeight.Bold, fontSize = 18.sp) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            // Balance Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Current Balance / বর্তমান ব্যালেন্স", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("$balance $currency", fontSize = 28.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                    if (currency == "SAR") {
                        Text("≈ ${(balance * bdtRate).toInt()} BDT", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Input Form
            OutlinedTextField(
                value = title,
                onValueChange = { title = it },
                label = { Text("Title / বিবরণ") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = amountStr,
                onValueChange = { amountStr = it },
                label = { Text("Amount / পরিমাণ") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Button(onClick = { type = "Income" }, colors = ButtonDefaults.buttonColors(containerColor = if (type == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) {
                    Text("Income / আয়")
                }
                Button(onClick = { type = "Expense" }, colors = ButtonDefaults.buttonColors(containerColor = if (type == "Expense") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary)) {
                    Text("Expense / ব্যয়")
                }
                Button(onClick = { currency = if (currency == "SAR") "BDT" else "SAR" }) {
                    Text(currency)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = {
                    val amt = amountStr.toDoubleOrNull()
                    if (title.isNotBlank() && amt != null) {
                        viewModel.addTransaction(title, amt, type, currency)
                        title = ""
                        amountStr = ""
                    }
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Add Transaction / লেনদেন যোগ করুন")
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("History / লেনদেনের ইতিহাস", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // Transaction List
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(transactions) { tx ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(tx.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                                val sdf = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
                                Text(sdf.format(Date(tx.date)), fontSize = 12.sp, color = MaterialTheme.colorScheme.secondary)
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "${if (tx.type == "Income") "+" else "-"}${tx.amount} ${tx.currency}",
                                    color = if (tx.type == "Income") MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                TextButton(onClick = { viewModel.deleteTransaction(tx) }) {
                                    Text("Delete", color = MaterialTheme.colorScheme.error)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
