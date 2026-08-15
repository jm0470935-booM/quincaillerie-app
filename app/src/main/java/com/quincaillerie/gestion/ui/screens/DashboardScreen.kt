package com.quincaillerie.gestion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quincaillerie.gestion.data.Product
import com.quincaillerie.gestion.ui.theme.RedAlert
import com.quincaillerie.gestion.viewmodel.ProductViewModel
import com.quincaillerie.gestion.viewmodel.SaleViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun DashboardScreen(
    productViewModel: ProductViewModel,
    saleViewModel: SaleViewModel
) {
    val lowStock by productViewModel.lowStockProducts.collectAsStateWithLifecycle()
    val todayTotal by saleViewModel.todaySalesTotal.collectAsStateWithLifecycle()
    val todaySales by saleViewModel.todaySales.collectAsStateWithLifecycle()
    val previousDays by saleViewModel.previousDaysSummaries.collectAsStateWithLifecycle()
    val dateSearchQuery by saleViewModel.dateSearchQuery.collectAsStateWithLifecycle()

    val currencyFormat = NumberFormat.getNumberInstance(Locale.FRANCE)
    val todayDateLabel = remember {
        java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(java.util.Date())
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                "Tableau de bord",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            Text(
                "Aujourd'hui, $todayDateLabel",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Ventes aujourd'hui",
                    value = "${currencyFormat.format(todayTotal)} FCFA",
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Transactions",
                    value = "${todaySales.size}",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(Icons.Default.Warning, contentDescription = null, tint = RedAlert)
                Text(
                    "Stock bas (${lowStock.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        if (lowStock.isEmpty()) {
            item {
                Text(
                    "Aucun produit en stock bas",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(lowStock) { product ->
                LowStockItem(product)
            }
        }

        item {
            Text(
                "Performances des jours précédents",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        item {
            OutlinedTextField(
                value = dateSearchQuery,
                onValueChange = { saleViewModel.setDateSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Rechercher une date (ex: 12/08/2026)") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )
        }

        if (previousDays.isEmpty()) {
            item {
                Text(
                    "Aucune donnée pour le moment",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            items(previousDays) { summary ->
                DailySummaryItem(summary, currencyFormat)
            }
        }
    }
}

@Composable
private fun StatCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(title, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun DailySummaryItem(summary: com.quincaillerie.gestion.data.DailySummary, currencyFormat: NumberFormat) {
    val displayDate = remember(summary.day) {
        try {
            val parser = java.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val display = java.text.SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            display.format(parser.parse(summary.day) ?: summary.day)
        } catch (e: Exception) {
            summary.day
        }
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(displayDate, fontWeight = FontWeight.Bold)
            Column(horizontalAlignment = Alignment.End) {
                Text("${currencyFormat.format(summary.totalSales)} FCFA", fontWeight = FontWeight.Bold)
                Text(
                    "${summary.transactionCount} transaction${if (summary.transactionCount > 1) "s" else ""}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
@Composable
private fun LowStockItem(product: Product) {
    Card(
        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text(product.category, style = MaterialTheme.typography.bodySmall)
            }
            Text(
                "${product.quantity} / min ${product.minStock}",
                color = RedAlert,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
