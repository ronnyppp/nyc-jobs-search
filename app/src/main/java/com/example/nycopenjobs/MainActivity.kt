package com.example.nycopenjobs

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.nycopenjobs.ui.screens.DetailScreen
import com.example.nycopenjobs.ui.screens.FavoritesScreen
import com.example.nycopenjobs.ui.screens.HomeScreen
import com.example.nycopenjobs.ui.screens.HomeScreenViewModel
import com.example.nycopenjobs.ui.theme.NYCOpenJobsTheme
import com.example.nycopenjobs.ui.widgets.AppNavBar

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            NYCOpenJobsTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val navController = rememberNavController()  // Create NavController instance
                    val viewModel: HomeScreenViewModel by viewModels { HomeScreenViewModel.Factory }

                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currRoute = navBackStackEntry?.destination?.route

                    val mainRoutes = listOf("home","favorites")
                    Scaffold(bottomBar = {
                        if (currRoute in mainRoutes) {
                            AppNavBar(navController = navController)
                        }
                    }) { innerPadding ->
                    // Setup NavHost for managing navigation between screens
                    NavHost(navController = navController, startDestination = Screen.Home.route,
                        modifier = Modifier.padding(innerPadding)) {
                        composable("home") {
                            // HomeScreen composable, pass navController to handle navigation
                            HomeScreen(
                                viewModel = viewModel,
                                navController = navController,
                                modifier = Modifier.fillMaxSize()
                            )
                        }
                        composable("favorites") {
                            FavoritesScreen(
                                navController = navController,
                                jobPostViewModel = viewModel
                            )
                        }
                        composable("detail/{jobId}") { backStackEntry ->
                            val jobId = backStackEntry.arguments?.getString("jobId")?.toInt() ?: 0
                            DetailScreen(
                                jobId = jobId,
                                navController = navController,
                                viewModel
                            )
                        }
                    }
                }}
            }
        }
    }
}

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object Favorites : Screen("favorites")
}