package com.example.nycopenjobs.ui.screens

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.nycopenjobs.NYCOpenJobsApp
import com.example.nycopenjobs.data.AppRepository
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.util.TAG
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.io.IOException

sealed interface HomeScreenUIState {
    data class Success(val data: List<JobPost>) : HomeScreenUIState
    data object Error : HomeScreenUIState
    data object Loading : HomeScreenUIState
    data object Ready : HomeScreenUIState
}

class HomeScreenViewModel(private val appRepository: AppRepository) : ViewModel() {

    var uiState: HomeScreenUIState by mutableStateOf(HomeScreenUIState.Ready)
        private set

    private val _favoriteUiState = MutableStateFlow<HomeScreenUIState>(HomeScreenUIState.Ready)
    val favoriteUiState: StateFlow<HomeScreenUIState> = _favoriteUiState

    var favoriteJobs: List<JobPost> by mutableStateOf(emptyList())

    var searchQuery by mutableStateOf("")
        private set

    init {
        getJobPostings()
        getFavoriteJobs()
    }
    fun getJobPostings()  {
        viewModelScope.launch {
            uiState = HomeScreenUIState.Loading
            uiState = try {
                HomeScreenUIState.Success(appRepository.getJobPostings())
            } catch(e: IOException){
                e.message?.let { Log.e(TAG, it)}
                HomeScreenUIState.Error
            } catch(e: HttpException){
                e.message?.let { Log.e(TAG, it)}
                HomeScreenUIState.Error
            }
        }
    }
    // Fetch a single job by jobId
    suspend fun getJobById(jobId: Int): JobPost {
        return appRepository.getJobPost(jobId)
    }
    fun getScrollPosition(): Int{
        return appRepository.getScrollPosition()
    }
    fun setScrollPosition(position: Int) {
        appRepository.setScrollPosition(position)
    }
    // Fetch favorite jobs for the favorites screen
    fun getFavoriteJobs() {
        viewModelScope.launch {
            _favoriteUiState.value = HomeScreenUIState.Loading // Use the mutable StateFlow here
            try {
                val favorites = appRepository.getFavoriteJobs() // Fetch only favorites
                favoriteJobs = favorites
                _favoriteUiState.value = HomeScreenUIState.Success(favorites) // Update with the favorite jobs
            } catch (e: IOException) {
                e.message?.let { Log.e(TAG, it) }
                _favoriteUiState.value = HomeScreenUIState.Error // Update state on error
            } catch (e: HttpException) {
                e.message?.let { Log.e(TAG, it) }
                _favoriteUiState.value = HomeScreenUIState.Error // Update state on error
            }
        }
    }
    fun updateSearchQuery(query: String) {
        searchQuery = query
    }
    fun filterJobs(jobs: List<JobPost>): List<JobPost> {
        if (searchQuery.isBlank()) return jobs

        return jobs.filter {
            it.businessTitle.contains(searchQuery, ignoreCase = true) ||
                    it.agency.contains(searchQuery, ignoreCase = true) ||
                    it.jobCategory.contains(searchQuery, ignoreCase = true)
        }
    }

    // Add job to favorites
    fun addJobToFavorites(jobId: Int) {
        viewModelScope.launch {
            try {
                appRepository.addJobToFavorites(jobId)
                getFavoriteJobs() // Refresh the list of favorite jobs
            } catch (e: Exception) {
                Log.e(TAG, "Error adding job to favorites: ${e.message}")
            }
        }
    }

    // Remove job from favorites
    fun removeJobFromFavorites(jobId: Int) {
        viewModelScope.launch {
            try {
                appRepository.removeJobFromFavorites(jobId)
                getFavoriteJobs() // Refresh the list of favorite jobs
            } catch (e: Exception) {
                Log.e(TAG, "Error removing job from favorites: ${e.message}")
            }
        }
    }
    fun toggleFavorites(jobId: Int) {
      if(isFavorite(jobId))
          removeJobFromFavorites(jobId)
      else addJobToFavorites(jobId)
    }
    fun isFavorite(jobId: Int): Boolean {
        return favoriteJobs.any {it.jobId == jobId}
    }


    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                Log.i(TAG, "view model factory: getting get app container")
                val application = checkNotNull(extras[APPLICATION_KEY]) as NYCOpenJobsApp
                val appContainer = application.container
                return HomeScreenViewModel(appContainer.appRepository) as T
            }
        }
    }
}





