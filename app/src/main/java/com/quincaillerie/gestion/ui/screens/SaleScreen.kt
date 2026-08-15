package com.quincaillerie.gestion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import com.quincaillerie.gestion.data.Product
import com.quincaillerie.gestion.viewmodel.CartLine
import com.quincaillerie.gestion.viewmodel.ProductViewModel
import com.quincaillerie.gestion.viewmodel.SaleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SaleScreen(
    productViewModel: ProductViewModel,
    saleViewModel: SaleViewModel
) {
    val products by productViewModel.products.collectAsStateWithLifecycle()
    val searchQuery by productViewModel.searchQuery.collectAsStateWithLifecycle()
    val cart by saleViewModel.cart.collectAsStateWithLifecycle()
    val cartTotal by saleViewModel.cartTotal.collectAsStateWithLifecycle()

    var quantityDialogProduct by remember { mutableStateOf<Product?>(null) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Nouvelle vente", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { productViewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Chercher un produit à ajouter") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (cart.isNotEmpty()) {
                Text("Panier (${cart.size})", fontWeight = FontWeight.Bold)
                LazyColumn(
                    modifier = Modifier.heightIn(max = 180.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    items(cart, key = { it.product.id }) { line ->
                        CartLineRow(line, onRemove = { saleViewModel.removeFromCart(line.product.id) })
                    }
                }
                Divider(modifier = Modifier.padding(vertical = 8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold)
                    Text("$cartTotal FCFA", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = {
                        saleViewModel.checkout { _, message ->
                            scope.launch { snackbarHostState.showSnackbar(message) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Valider la vente")
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            Text("Produits disponibles", fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(products.filter { it.quantity > 0 }, key = { it.id }) { product ->
                    AvailableProductRow(product) {
                        quantityDialogProduct = product
                    }
                }
            }
        }
    }

    quantityDialogProduct?.let { product ->
        QuantityDialog(
            product = product,
            onDismiss = { quantityDialogProduct = null },
            onConfirm = { qty ->
                saleViewModel.addToCart(product, qty)
                quantityDialogProduct = null
            }
        )
    }
}

@Composable
private fun CartLineRow(line: CartLine, onRemove: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("${line.product.name} x${line.quantity}")
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("${line.product.unitPrice * line.quantity} FCFA")
            IconButton(onClick = onRemove) {
                Icon(Icons.Default.Delete, contentDescription = "Retirer")
            }
        }
    }
}

@Composable
private fun AvailableProductRow(product: Product, onAdd: () -> Unit) {
    Card(onClick = onAdd, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text("${product.unitPrice} FCFA - Stock: ${product.quantity}", style = MaterialTheme.typography.bodySmall)
            }
            Text("Ajouter", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun QuantityDialog(
    product: Product,
    onDismiss: () -> Unit,
    onConfirm: (Int) -> Unit
) {
    var qtyText by remember { mutableStateOf("1") }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(modifier = Modifier.padding(20.dp)) {
                Text(product.name, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text("Stock disponible: ${product.quantity}", style = MaterialTheme.typography.bodySmall)
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedTextField(
                    value = qtyText,
                    onValueChange = { qtyText = it },
                    label = { Text("Quantité") },
                    singleLine = true
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = onDismiss) { Text("Annuler") }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {
                        val qty = qtyText.toIntOrNull() ?: 0
                        if (qty in 1..product.quantity) {
                            onConfirm(qty)
                        }
                    }) {
                        Text("Ajouter")
                    }
                }
            }
        }
    }
}
