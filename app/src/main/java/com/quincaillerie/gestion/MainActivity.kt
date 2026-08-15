package com.quincaillerie.gestion

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.quincaillerie.gestion.data.AppDatabase
import com.quincaillerie.gestion.ui.AppNavigation
import com.quincaillerie.gestion.ui.theme.QuincaillerieAppTheme
import com.quincaillerie.gestion.viewmodel.ProductViewModel
import com.quincaillerie.gestion.viewmodel.SaleViewModel
import com.quincaillerie.gestion.viewmodel.ViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val database = AppDatabase.getDatabase(applicationContext)
        val factory = ViewModelFactory(database.productDao(), database.saleDao())

        setContent {
            QuincaillerieAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val productViewModel: ProductViewModel = viewModel(factory = factory)
                    val saleViewModel: SaleViewModel = viewModel(factory = factory)

                    AppNavigation(productViewModel, saleViewModel)
                }
            }
        }
    }
}
