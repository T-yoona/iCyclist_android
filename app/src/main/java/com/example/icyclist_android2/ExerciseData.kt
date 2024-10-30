package com.example.icyclist_android2

import android.util.Log
import retrofit2.Call
import retrofit2.Response
import retrofit2.Callback

data class ExerciseData(    val username: String,
                            val totalDuration: Int,
                            val pathCoordinates: String
)
fun sendExerciseData(exerciseData: ExerciseData) {
    RetrofitClient.exerciseApi.saveExerciseData(exerciseData).enqueue(object : Callback<Void> {
        override fun onResponse(call: Call<Void>, response: Response<Void>) {
            if (response.isSuccessful) {
                Log.d("API Success", "Data saved successfully: ${response.code()}")
            } else {
                Log.e("API Error", "Error: ${response.code()} ${response.message()}")
            }
        }

        override fun onFailure(call: Call<Void>, t: Throwable) {
            Log.e("API Failure", "Error: ${t.message}")
        }
    })
}
