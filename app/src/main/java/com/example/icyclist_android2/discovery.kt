package com.example.icyclist_android2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 这是一个负责展示骑行动态的 Activity，通过 RecyclerView 来显示用户发布的骑行帖子，并允许添加和删除动态。
 */
class discovery : AppCompatActivity() {
    private lateinit var discoveryRecyclerView: RecyclerView
    private lateinit var discoveryAdapter: DiscoveryAdapter
    private val ridePosts: MutableList<RidePostResponseDTO> = mutableListOf()

    companion object {
        const val REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_discovery)

        discoveryRecyclerView = findViewById(R.id.discoveryRecyclerView)
        discoveryRecyclerView.layoutManager = LinearLayoutManager(this)


        // 获取当前用户名
        val sharedPreferences = getSharedPreferences("MyAppPreferences", Context.MODE_PRIVATE)
        val currentUsername = sharedPreferences.getString("username", "默认用户名") ?: "默认用户名"
        // 在初始化适配器之前添加日志
        Log.d("Discovery", "Initializing discoveryAdapter...")
        // 初始化适配器
        discoveryAdapter = DiscoveryAdapter(ridePosts, currentUsername) { ridePost ->
            onDeleteRidePost(ridePost)
        }

        // 在初始化适配器之后添加日志
        Log.d("Discovery", "discoveryAdapter initialized successfully.")
        discoveryRecyclerView.adapter = discoveryAdapter

        // 加载骑行动态
        loadRidePosts()

        val addButton = findViewById<Button>(R.id.addButton)
        addButton.setOnClickListener {
            val intent = Intent(this, insert::class.java) // 确保你的目标活动类是正确的
            startActivityForResult(intent, REQUEST_CODE)
        }

        // 跳转按钮
        val disbutton1 = findViewById<Button>(R.id.disbutton1)
        val disbutton2 = findViewById<Button>(R.id.disbutton2)
        val disbutton4 = findViewById<Button>(R.id.disbutton4)
        disbutton1.setOnClickListener {
            val intent1 = Intent(this, MainActivity::class.java)
            startActivity(intent1)
        }
        disbutton2.setOnClickListener {
            val intent2 = Intent(this, sport::class.java)
            startActivity(intent2)
        }
        disbutton4.setOnClickListener{
            val intent4 = Intent(this,ViewRideActivity::class.java)
            startActivity(intent4)
        }
    }

    private fun loadRidePosts() {
        val apiService = RetrofitClient.ridePostApi

        apiService.getAllRidePosts().enqueue(object : Callback<List<RidePostResponseDTO>> {
            override fun onResponse(call: Call<List<RidePostResponseDTO>>, response: Response<List<RidePostResponseDTO>>) {
                if (response.isSuccessful) {
                    response.body()?.let { posts ->
                        ridePosts.clear()
                        ridePosts.addAll(posts)
                        discoveryAdapter.notifyDataSetChanged() // 刷新适配器
                    }
                } else {
                    Log.e("Discovery", "Error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<List<RidePostResponseDTO>>, t: Throwable) {
                Log.e("Discovery", "Failed: ${t.message}")
            }
        })
    }

    private fun onDeleteRidePost(ridePost: RidePostResponseDTO) {
        val apiService = RetrofitClient.ridePostApi

        // 调用删除 API，这里假设你已经在 RidePostApi 中实现了 deleteRidePost 方法
        apiService.deleteRidePost(ridePost.ridePost.userId).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    // 从列表中移除已删除的动态
                    ridePosts.remove(ridePost)
                    discoveryAdapter.notifyDataSetChanged() // 刷新适配器
                } else {
                    Log.e("Discovery", "Error deleting ride post: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Log.e("Discovery", "Failed to delete ride post: ${t.message}")
            }
        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // 刷新 RecyclerView
            loadRidePosts() // 重新加载骑行动态
        }
    }
}