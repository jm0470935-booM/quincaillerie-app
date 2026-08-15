package com.quincaillerie.gestion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quincaillerie.gestion.data.SoldProductSummary
import com.quincaillerie.gestion.viewmodel.SaleViewModel
import java.text.NumberFormat
import java.util.Locale

@Composable
fun SoldProductsScreen(saleViewModel: SaleViewModel) {
    val soldProducts by saleViewModel.soldProductsSummary.collectAsStateWithLifecycle()
    val currencyFormat = NumberFormat.getNumberInstance(Locale.FRANCE)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            "Produits vendus",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(12.dp))

        if (soldProducts.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                    "Aucune vente enregistrée pour le moment",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(soldProducts, key = { it.productName }) { summary ->
                    SoldProductCard(summary, currencyFormat)
                }
                item { Spacer(modifier = Modifier.height(24.dp)) }
            }
        }
    }
}

@Composable
private fun SoldProductCard(summary: SoldProductSummary, currencyFormat: NumberFormat) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(12.dp)) {
            Text(summary.productName, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text("Quantité vendue", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${summary.totalQuantity}", fontWeight = FontWeight.Bold)
                }
                Column {
                    Text("Prix unitaire", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${currencyFormat.format(summary.avgUnitPrice)} FCFA", fontWeight = FontWeight.Bold)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text("Total", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Text("${currencyFormat.format(summary.totalRevenue)} FCFA", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
