package com.example.nycopenjobs.data

import android.content.Context
import android.util.Log
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.Upsert
import com.example.nycopenjobs.model.JobPost
import com.example.nycopenjobs.util.TAG
import kotlinx.coroutines.flow.Flow

@Dao
interface JobPostDao {
    // getAll()
    @Query("SELECT * FROM JobPost ORDER BY postingLastUpdated DESC")
    fun getAll(): Flow<List<JobPost>>
    // getOne()
    @Query("SELECT * FROM JobPost WHERE jobId = :id")
    fun get(id: Int): JobPost

    // add two queries for favorite
    // Get all favorite job posts (assuming you have an 'isFavorite' field)
    @Query("SELECT * FROM JobPost WHERE favorite = 1 ORDER BY postingLastUpdated DESC")
    suspend fun getFavorites(): List<JobPost>

    // Mark a job post as a favorite (by updating the 'isFavorite' field)
    @Query("UPDATE JobPost SET favorite = 1 WHERE jobId = :id")
    suspend fun addFavorite(id: Int)

    // Remove a job post from favorites (by setting 'isFavorite' to false)
    @Query("UPDATE JobPost SET favorite = 0 WHERE jobId = :id")
    suspend fun removeFavorite(id: Int)

    // update or insert job post
    @Upsert(entity = JobPost::class)
    suspend fun upsert(jobPostings: List<JobPost>)

}

@Database(entities = [JobPost::class], version = 1, exportSchema = false)
abstract class LocalDatabase : RoomDatabase() {

    // we use a DAO (Data Access Object)
    abstract fun jobPostDao(): JobPostDao

    companion object {
        private const val DATABASE = "local_database"

        @Volatile
        private var Instance: LocalDatabase? = null

        fun getDatabase(context: Context): LocalDatabase {
            Log.i(TAG, "getting database")
            return Instance ?: synchronized(this) {
                Room.databaseBuilder(context, LocalDatabase::class.java, DATABASE)
                    .fallbackToDestructiveMigration().build().also {Instance = it}
            }
        }
    }
}