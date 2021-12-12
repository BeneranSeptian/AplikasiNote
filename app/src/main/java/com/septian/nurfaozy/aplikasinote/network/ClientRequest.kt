package com.septian.nurfaozy.aplikasinote.network


import com.septian.nurfaozy.aplikasinote.BuildConfig
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ClientRequest {

    private var retrofit: Retrofit? = null
    private const val BASE_URL = "https://maps.googleapis.com"

    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                val builder = OkHttpClient.Builder()
                builder.retryOnConnectionFailure(false)
                builder.interceptors().add(HttpLoggingInterceptor().apply {
                    level =
                        if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
                })
                val client = builder.build()

                retrofit = Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(client)
                    .build()
            }
            return retrofit
        }
}