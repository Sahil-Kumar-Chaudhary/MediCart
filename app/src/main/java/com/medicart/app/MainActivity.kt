package com.medicart.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import com.medicart.app.data.MockPharmacyRepository
import com.medicart.app.data.PharmacyRepository
import com.medicart.app.navigation.MediCartNavGraph
import com.medicart.app.ui.theme.MediCartTheme

class MainActivity : ComponentActivity() {
    private lateinit var repository: PharmacyRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        repository = MockPharmacyRepository(applicationContext)

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
