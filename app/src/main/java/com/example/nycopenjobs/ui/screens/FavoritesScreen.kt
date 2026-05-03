package com.example.nycopenjobs.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavController
import com.example.nycopenjobs.model.JobPost
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import com.example.nycopenjobs.Screen
import com.example.nycopenjobs.ui.widgets.AppTopBar


@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FavoritesScreen(
    jobPostViewModel: HomeScreenViewModel,
    navController: NavController,
) {
    // Get favorite jobs from ViewModel
    val favoriteJobsState by jobPostViewModel.favoriteUiState.collectAsState()

    // Filter favorite jobs
    val favoriteJobsList = if (favoriteJobsState is HomeScreenUIState.Success) {
        (favoriteJobsState as HomeScreenUIState.Success).data
    } else {
        emptyList<JobPost>()
    }

    Scaffold(
        topBar = { AppTopBar(title = "Favorites", showBackButton = false, onBackClick = null) },
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            // Show the list of favorite jobs
            FavJobListings(
                jobs = favoriteJobsList,
                navController = navController
            )
        }
    }

}

@Composable
fun FavJobListings(
    jobs: List<JobPost>,
    navController: NavController,
) {
    if(jobs.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                "Add a favorite job here",
                color = Color.Gray, textAlign = TextAlign.Center
            )
        }
    }else {
    LazyColumn {
        items(jobs) { jobPost ->
            JobCard(jobPost, navController)
        }
    }}
}
@Composable
fun FavoriteNavBar(
    onFavoriteClick: () -> Unit,
    onHomeClick: () -> Unit,
    selectedTab: String
) {
    NavigationBar {
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Home, contentDescription = "Home") },
            label = { Text("Home") },
            selected = selectedTab == "home",
            onClick = onHomeClick
        )
        NavigationBarItem(
            icon = { Icon(Icons.Filled.Favorite, contentDescription = "Favorites") },
            label = { Text("Favorites") },
            selected = selectedTab == "favorites",
            onClick = onFavoriteClick
        )
    }
}
