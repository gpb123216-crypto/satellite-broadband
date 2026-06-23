package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.lifecycleScope
import com.example.data.AppDatabase
import com.example.data.WifiRepository
import com.example.ui.PortalApp
import com.example.ui.WifiViewModel
import com.example.ui.WifiViewModelFactory
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize local Room DB and repository
        val database = AppDatabase.getDatabase(applicationContext, lifecycleScope)
        val repository = WifiRepository(database.customerDao(), database.notificationLogDao())
        
        // Setup ViewModel using Android factory injection
        val viewModel: WifiViewModel by viewModels {
            WifiViewModelFactory(application, repository)
        }

        setContent {
            MyApplicationTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    PortalApp(viewModel = viewModel)
                }
            }
        }
    }
}

