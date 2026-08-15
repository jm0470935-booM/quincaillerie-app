package com.quincaillerie.gestion.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.quincaillerie.gestion.data.Product
import com.quincaillerie.gestion.ui.theme.RedAlert
import com.quincaillerie.gestion.viewmodel.ProductViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(viewModel: ProductViewModel) {
    val products by viewModel.products.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()

    var showDialog by remember { mutableStateOf(false) }
    var editingProduct by remember { mutableStateOf<Product?>(null) }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                editingProduct = null
                showDialog = true
            }) {
                Icon(Icons.Default.Add, contentDescription = "Ajouter un produit")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            Text("Produits", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Rechercher par nom ou code-barres") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (products.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("Aucun produit trouvé", color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            } else {
                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(products, key = { it.id }) { product ->
                        ProductCard(
                            product = product,
                            onClick = {
                                editingProduct = product
                                showDialog = true
                            }
                        )
                    }
                    item { Spacer(modifier = Modifier.height(72.dp)) }
                }
            }
        }
    }

    if (showDialog) {
        ProductDialog(
            product = editingProduct,
            onDismiss = { showDialog = false },
            onSave = { product ->
                if (editingProduct == null) {
                    viewModel.addProduct(product)
                } else {
                    viewModel.updateProduct(product)
                }
                showDialog = false
            },
            onDelete = editingProduct?.let {
                {
                    viewModel.deleteProduct(it)
                    showDialog = false
                }
            }
        )
    }
}

@Composable
private fun ProductCard(product: Product, onClick: () -> Unit) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(product.name, fontWeight = FontWeight.Bold)
                Text(product.category, style = MaterialTheme.typography.bodySmall)
                Text("${product.unitPrice} FCFA / unité", style = MaterialTheme.typography.bodySmall)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    "${product.quantity} en stock",
                    color = if (product.isLowStock) RedAlert else MaterialTheme.colorScheme.onSurface,
                    fontWeight = if (product.isLowStock) FontWeight.Bold else FontWeight.Normal
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ProductDialog(
    product: Product?,
    onDismiss: () -> Unit,
    onSave: (Product) -> Unit,
    onDelete: (() -> Unit)?
) {
    var name by remember { mutableStateOf(product?.name ?: "") }
    var category by remember { mutableStateOf(product?.category ?: "") }
    var barcode by remember { mutableStateOf(product?.barcode ?: "") }
    var price by remember { mutableStateOf(product?.unitPrice?.toString() ?: "") }
    var quantity by remember { mutableStateOf(product?.quantity?.toString() ?: "") }
    var minStock by remember { mutableStateOf(product?.minStock?.toString() ?: "5") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (product == null) "Nouveau produit" else "Modifier le produit") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Nom") }, singleLine = true)
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Catégorie") }, singleLine = true)
                OutlinedTextField(value = barcode, onValueChange = { barcode = it }, label = { Text("Code-barres (optionnel)") }, singleLine = true)
                OutlinedTextField(value = price, onValueChange = { price = it }, label = { Text("Prix unitaire (FCFA)") }, singleLine = true)
                OutlinedTextField(value = quantity, onValueChange = { quantity = it }, label = { Text("Quantité en stock") }, singleLine = true)
                OutlinedTextField(value = minStock, onValueChange = { minStock = it }, label = { Text("Seuil d'alerte stock bas") }, singleLine = true)
            }
        },
        confirmButton = {
            TextButton(onClick = {
                val newProduct = Product(
                    id = product?.id ?: 0,
                    name = name.trim(),
                    category = category.trim(),
                    barcode = barcode.trim().ifBlank { null },
                    unitPrice = price.toDoubleOrNull() ?: 0.0,
                    quantity = quantity.toIntOrNull() ?: 0,
                    minStock = minStock.toIntOrNull() ?: 5
                )
                if (newProduct.name.isNotBlank()) onSave(newProduct)
            }) {
                Text("Enregistrer")
            }
        },
        dismissButton = {
            Row {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text("Supprimer", color = RedAlert)
                    }
                }
                TextButton(onClick = onDismiss) {
                    Text("Annuler")
                }
            }
        }
    )
}
