package com.example.nycopenjobs.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.ui.widgets.AppTopBar


@Composable
fun DetailScreen(
    jobId: Int,
    navController: NavHostController,
    viewModel: HomeScreenViewModel
) {
    var job by remember {mutableStateOf<JobPost?>(null) }

    var isFavorite = viewModel.isFavorite(jobId)

    // Get job details by jobId
    LaunchedEffect(jobId) {
        val fetchedJob = viewModel.getJobById(jobId)
        job = fetchedJob
        isFavorite = viewModel.isFavorite(jobId)
    }
    Column(modifier = Modifier.fillMaxSize()) {
        // Top Bar with Back and Favorite icon
        job?.let {
            AppTopBar(
                title = it.businessTitle,
                showBackButton = true,
                onBackClick = { navController.popBackStack() },
                isFavorite = isFavorite,
                onFavoriteClick = {viewModel.toggleFavorites(jobId)}
            )
        }

        // Job details
        job?.let { JobDetailsSection(jobPost = it) }
    }
}

@Composable
fun JobDetailsSection(jobPost: JobPost) {
    // display job details text
    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
    ) {
        Text("Job Overview", style = MaterialTheme.typography.titleMedium)
        Spacer(Modifier.height(8.dp))
        Row {
            Text("Job ID: ${jobPost.jobId}", style = MaterialTheme.typography.labelSmall)
            Text(" Posting Date: ${jobPost.postingDate}", style = MaterialTheme.typography.labelSmall)
        }
        Spacer(modifier = Modifier.size(15.dp))
        DetailedRow("Job Title", jobPost.businessTitle)
        DetailedRow("Career Level", jobPost.careerLevel)
        DetailedRow("Salary Range", jobPost.salaryFrequency)
        DetailedRow("Job Category", jobPost.jobCategory)
        DetailedRow("Work Location",jobPost.agencyLocation)
        Spacer(modifier = Modifier.size(15.dp))
        //DetailedRow("Division", jobPost.divisionWorkUnit}")

        Text("Job Description: ${jobPost.jobDescription}", style = MaterialTheme.typography.bodySmall)
        Spacer(modifier = Modifier.size(8.dp))
        Text(jobPost.jobDescription, style = MaterialTheme.typography.bodyMedium, lineHeight = 20.sp)
    }
}

@Composable
fun DetailedRow(label: String, value: String) {
    Column(modifier = Modifier.padding(vertical = 6.dp)) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        Text(value, style = MaterialTheme.typography.bodySmall)
    }
}