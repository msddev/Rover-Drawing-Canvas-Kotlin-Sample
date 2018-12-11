package com.mkdev.roverapp.api

import android.content.Context
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import timber.log.Timber
import java.util.concurrent.TimeUnit

class Controller {

    companion object {
        private val BASE_URL = "https://roverapi.reev.ca"
        private var retrofit: Retrofit? = null

        fun getClient(context: Context): Retrofit {
            return retrofit?.let {
                it
            } ?: run {
                Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create())
                    .baseUrl(BASE_URL)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(
                        OkHttpClient.Builder()
                            .writeTimeout(30, TimeUnit.SECONDS)
                            .readTimeout(30, TimeUnit.SECONDS)
                            .addInterceptor {
                                val request = it.request()
                                val response = it.proceed(request)

                                when (response.code()) {
                                    400 -> {
                                        Timber.d("400 - Bad Request")
                                    }
                                    401 -> {
                                        Timber.d("401 - Unauthorized - refresh token")
                                    }
                                    403 -> {
                                        Timber.d("403 - Forbidden")
                                    }
                                    404 -> {
                                        Timber.d("404 - Not Found - URL")
                                    }
                                    405 -> {
                                        Timber.d("405 - Method Not Allowed - DELETE - GET")
                                    }
                                    500 -> {
                                        Timber.d("500 - Internal Server ErrorT")
                                    }
                                    else -> {
                                        Timber.d("Server error - ${response.message()}")
                                    }
                                }

                                response
                            }.build())
                    .build()
            }
        }
    }
}