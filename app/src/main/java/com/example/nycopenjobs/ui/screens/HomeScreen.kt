package com.example.nycopenjobs.ui.screens

import androidx.benchmark.traceprocessor.Row
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.nycopenjobs.R
import com.example.nycopenjobs.Screen
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.ui.widgets.AppNavBar
import com.example.nycopenjobs.ui.widgets.AppTopBar
import com.example.nycopenjobs.util.LoadingSpinner
import com.example.nycopenjobs.util.ToastMessage
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce

@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel,
    modifier: Modifier = Modifier,
    navController: NavController
) {
    val jobs = when (val state = viewModel.uiState) {
        is HomeScreenUIState.Success -> state.data
        else -> emptyList()
    }
    Scaffold (
        topBar = { AppTopBar(title = "NYC Open Jobs", showBackButton = false, onBackClick = null) }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
        OutlinedTextField(
            value = viewModel.searchQuery,
            onValueChange = { viewModel.updateSearchQuery(it) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            placeholder = { Text("Search jobs...") },
            singleLine = true,
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = null)
            }
        )
        when (val uiState = viewModel.uiState) {
            is HomeScreenUIState.Loading -> LoadingSpinner()

            is HomeScreenUIState.Success -> {
                val filteredJobs = viewModel.filterJobs(jobs)
                JobListings(
                    filteredJobs,
                    {
                        viewModel.getJobPostings()
                    },
                    { scrollPosition ->
                        viewModel.setScrollPosition(scrollPosition)
                    },
                    viewModel.getScrollPosition(),
                    modifier.fillMaxSize(),
                    navController
                )
            }

            is HomeScreenUIState.Error -> ToastMessage(stringResource(R.string.job_listing_not_available_at_this_time))
            else -> ToastMessage(stringResource(R.string.job_listing_loaded))
        }}
    }
}

@OptIn(FlowPreview::class)
@Composable
fun JobListings(
    jobs: List<JobPost>,
    loadMoreData: () -> Unit,
    updateScrollPosition: (Int) -> Unit,
    scrollPosition: Int,
    modifier: Modifier,
    navController: NavController
){
    val firstVisibleIndex = if (scrollPosition > jobs.size) 0 else scrollPosition
    val listState: LazyListState = rememberLazyListState(firstVisibleIndex)

    LazyColumn(modifier = modifier, listState) {
        items(jobs) { jobPost: JobPost ->
            JobCard(jobPost, navController)
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index}
            .debounce(timeoutMillis = 500L)
            .collect {lastVisibleItemIndex ->
                updateScrollPosition(listState.firstVisibleItemIndex)
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= jobs.size - 1) {
                    loadMoreData()
                }
            }
    }
}

@Composable
fun JobCard(
    jobPost: JobPost,
    navController: NavController
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clickable {
                navController.navigate("detail/${jobPost.jobId}")
            },
        shape = RoundedCornerShape(10.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = jobPost.agency, style = MaterialTheme.typography.labelMedium,
                    color = Color.Gray,
                    modifier = Modifier.weight(1f)
                )
            }
            Box(
                modifier = Modifier
                    .background(
                        color = Color.LightGray,
                        shape = RoundedCornerShape(10.dp)
                    )
                    .padding(8.dp)
            )
            {
                Text(
                    text = jobPost.careerLevel,
                    style = MaterialTheme.typography.labelSmall,
                    color = Color.Black
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = jobPost.businessTitle,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}






