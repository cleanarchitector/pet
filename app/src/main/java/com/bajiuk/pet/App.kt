package com.bajiuk.pet

import android.app.Application
import com.bajiuk.pet.bash.model.BashApi
import com.bajiuk.pet.bash.model.BashManager
import com.bajiuk.pet.bash.model.BashManagerImpl
import com.bajiuk.pet.bash.viewmodel.BashViewModel
import com.jakewharton.retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        val httpClient = OkHttpClient.Builder()

        if (BuildConfig.DEBUG) {
            val logging = HttpLoggingInterceptor()
            logging.level = HttpLoggingInterceptor.Level.BODY
            httpClient.addInterceptor(logging)
        }

        val retrofit = Retrofit.Builder()
                .baseUrl("http://umorili.herokuapp.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(httpClient.build())
                .build()

        val bashApi = retrofit.create(BashApi::class.java)
        bashManager = BashManagerImpl(bashApi)
        bashViewModel = BashViewModel(bashManager)
    }

    lateinit var bashManager : BashManager
        private set
    lateinit var bashViewModel : BashViewModel
        private set
}