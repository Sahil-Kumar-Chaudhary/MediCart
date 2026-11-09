package com.medicart.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.navigation.MediCartNavGraph
import com.medicart.app.ui.theme.MediCartTheme

class MainActivity : ComponentActivity() {
    private val repository = MockPharmacyRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MediCartTheme {
                val navController = rememberNavController()
                MediCartNavGraph(
                    navController = navController,
                    repository = repository
                )
            }
        }
    }
}
