package com.example.icyclist_android2

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 骑行记录api,插入骑行数据post，查看骑行数据并返回get
 */

interface ExerciseApi {
    @POST("exercise/save")
    fun saveExerciseData(@Body exerciseData: ExerciseData): Call<Void>

    @GET("exercise/records/{username}")
    fun getRideRecords(@Path("username") username: String): Call<List<RideRecord>>
}