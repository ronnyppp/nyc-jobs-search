package com.example.nycopenjobs.data

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.nycopenjobs.api.NycOpenDataApi
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.util.TAG
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext

interface AppRepository {

    fun getScrollPosition(): Int
    fun setScrollPosition(position: Int)
    suspend fun getFavoriteJobs(): List<JobPost>
    suspend fun getJobPostings(): List<JobPost>
    suspend fun getJobPost(jobId: Int): JobPost
    suspend fun addJobToFavorites(jobId: Int)
    suspend fun removeJobFromFavorites(jobId: Int)
}

class AppRepositoryImpl(
    private val remoteApi: NycOpenDataApi,
    private val sharedPreferences: SharedPreferences,
    private val dao: JobPostDao
) : AppRepository {
    private val scrollPositionKey = "scroll_position"
    private val offsetKey = "offset"
    private var offset = sharedPreferences.getInt(offsetKey, 0)
    private var totalJobs = 0

    private fun updateOffset() {
        offset += (totalJobs - offset)
        Log.i(TAG, "offset: $offset")
        sharedPreferences.edit().putInt(offsetKey, offset).apply()
    }
    private fun updateTotalJobs(newTotal: Int) {
        totalJobs = newTotal
        Log.i(TAG, "total jobs: $totalJobs")
    }
    override suspend fun getJobPostings(): List<JobPost> {
        Log.i(TAG, "getting job postings")
        updateOffset()
        val localData = dao.getAll().first()
        updateTotalJobs(localData.size)
        // fetch more jobs when local data exhausted
        if (offset == totalJobs) {
            Log.i(TAG, "getting job posting via API")
            val jobs = remoteApi.getJobPostings(offset)
            Log.i(TAG, "API returned ${jobs.size} jobs. Updating local database.")

            // update or insert new jobs in local database
            dao.upsert(jobs)

            val updatedJobs = dao.getAll().first()
            updateTotalJobs(updatedJobs.size)

            Log.i(TAG, "returning updated jobs from API")
            return updatedJobs
        }
        Log.i(TAG, "returning local data")
        return localData
    }

    override suspend fun getJobPost(jobId: Int) : JobPost{
        Log.i(TAG, "getting job post id $jobId")
        return withContext(Dispatchers.IO) {
            dao.get(jobId)  // Perform database query on background thread
        }
    }
    override fun getScrollPosition(): Int {
        val scrollPosition = sharedPreferences.getInt(scrollPositionKey, 0)
        Log.i(TAG, "scroll position: $scrollPosition")
        return scrollPosition
    }

    override fun setScrollPosition(position: Int) {
        Log.i(TAG, "setting scroll position: $position")
        sharedPreferences.edit().putInt(scrollPositionKey, position).apply()
    }
    // Get all favorite job posts
    override suspend fun getFavoriteJobs(): List<JobPost> {
        return dao.getFavorites()
    }

    // Mark a job post as a favorite
    override suspend fun addJobToFavorites(id: Int) {
        dao.addFavorite(id)
    }

    // Remove a job post from favorites
    override suspend fun removeJobFromFavorites(id: Int) {
        dao.removeFavorite(id)
    }

}


