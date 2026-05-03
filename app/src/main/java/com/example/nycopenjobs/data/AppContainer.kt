package com.example.nycopenjobs.data
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.util.Log
import com.example.nycopenjobs.api.AppRemoteApis
import com.example.nycopenjobs.api.NycOpenDataApi
import com.example.nycopenjobs.util.TAG
import kotlinx.serialization.json.Json
import okhttp3.MediaType
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory

interface AppContainer {
    val appRepository: AppRepository
}

class DefaultAppContainer(private val context: Context): AppContainer {

    override val appRepository: AppRepository by lazy {
        Log.i(TAG, "initializing app repository")
        AppRepositoryImpl(
            AppRemoteApis().getNycOpenDataApi(),
            AppPreferences(context).getSharedPreferences(),
            LocalDatabase.getDatabase(context).jobPostDao()
        )
    }


}



