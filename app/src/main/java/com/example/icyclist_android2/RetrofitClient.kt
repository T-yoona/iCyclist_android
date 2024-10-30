package com.example.icyclist_android2

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory

/**
 * 这是一个api实例
 * 注意更换url:终端 ipconfig 查看ipv4
 */
object RetrofitClient {
//    private const val BASE_URL = "http://10.234.53.60:8080/" // 替换为您的后端 URL
      private const val BASE_URL = "http://10.234.100.57:8080/"
    val loggingInterceptor = LoggingInterceptor()
    val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor) // 添加 LoggingInterceptor
        .build()

    private val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .addConverterFactory(ScalarsConverterFactory.create()) // 添加这一行
            .build()
    }

    val exerciseApi: ExerciseApi by lazy {
        retrofit.create(ExerciseApi::class.java)
    }

    val userApi: UserApi by lazy {
        retrofit.create(UserApi::class.java)
    }



    val ridePostApi: RidePostApi by lazy {
        retrofit.create(RidePostApi::class.java)
    }


}