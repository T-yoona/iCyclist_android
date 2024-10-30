package com.example.icyclist_android2

import android.graphics.Color
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.amap.api.maps.AMap
import com.amap.api.maps.MapView
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.PolylineOptions
import retrofit2.Call

/**
 * ”发现“界面，展示各个用户的骑行动态
 */
class ViewRideActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var rideRecordAdapter: RideRecordAdapter
    private val rideRecords = mutableListOf<RideRecord>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_ride)

        recyclerView = findViewById(R.id.recyclerView)
        rideRecordAdapter = RideRecordAdapter(rideRecords,getSharedPreferences("app_preferences", MODE_PRIVATE))
        recyclerView.adapter = rideRecordAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)
        // 从 SharedPreferences 获取用户名
        val username = getUsername() // 您需要实现这个方法以获取用户名


        // 从后端获取骑行记录
        fetchRideRecords(username)


    }

    private fun getUsername(): String {
        // 从 SharedPreferences 或其他来源获取用户名
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPreferences.getString("username", "") ?: ""
    }
    private fun fetchRideRecords(username: String) {
        RetrofitClient.exerciseApi.getRideRecords(username).enqueue(object : retrofit2.Callback<List<RideRecord>> {
            override fun onResponse(call: Call<List<RideRecord>>, response: retrofit2.Response<List<RideRecord>>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        rideRecords.clear()
                        rideRecords.addAll(it)
                        rideRecordAdapter.notifyDataSetChanged()
                    }
                } else {
                    Toast.makeText(this@ViewRideActivity, "获取骑行记录失败", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<RideRecord>>, t: Throwable) {
                Toast.makeText(this@ViewRideActivity, "网络请求失败: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

}