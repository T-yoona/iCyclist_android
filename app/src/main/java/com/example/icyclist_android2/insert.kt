package com.example.icyclist_android2

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.amap.api.maps.MapFragment
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

/**
 * 这是插入骑行动态界面
 * 点击发布按钮或者长按结束暂停骑行则会跳转到该界面，获取从sport活动中的骑行距离和时长
 * 可上传图片，发布骑行动态
 * 将骑行动态连接到后端ride_post数据库存储
 */

class insert : AppCompatActivity() {
    private lateinit var uploadImageButton: Button
    private lateinit var imageView: ImageView
    private lateinit var rideStatsTextView: TextView
    private lateinit var titleEditText: EditText
    private lateinit var contentEditText: EditText
    private lateinit var postButton: Button

    private var selectedImageUri: Uri? = null  // 保存选择的图片Uri
    private val PICK_IMAGE_REQUEST = 1  // 请求码

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_insert)

        // 绑定视图
        uploadImageButton = findViewById(R.id.uploadImageButton)
        imageView = findViewById(R.id.imageView)
        rideStatsTextView = findViewById(R.id.rideStatsTextView)
        titleEditText = findViewById(R.id.titleEditText)
        contentEditText = findViewById(R.id.contentEditText)
        postButton = findViewById(R.id.postButton)




        // 设置骑行数据 (从上一个活动传递的骑行数据)
        val rideDistance = intent.getStringExtra("RIDE_DISTANCE") ?: "0公里"
        val rideDuration = intent.getStringExtra("RIDE_DURATION") ?: "00:00:00"
        rideStatsTextView.text = "骑行距离：$rideDistance | 时长：$rideDuration"

        Log.d("RIDE_STATS", "Distance: ${rideDistance} | Duration: $rideDuration")

        // 上传图片按钮点击事件
        uploadImageButton.setOnClickListener {
            selectImageFromGallery()
        }

        // 发布按钮点击事件
        postButton.setOnClickListener {
            val title = titleEditText.text.toString().trim()
            val content = contentEditText.text.toString().trim()

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(this, "标题和内容不能为空", Toast.LENGTH_SHORT).show()
            } else {
                // 发布骑行动态
                postRideDetails(title, content, selectedImageUri, rideDistance, rideDuration)
            }
        }
    }

    // 选择图片的方法
    private fun selectImageFromGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    private fun postRideDetails(title: String, content: String, imageUri: Uri?, rideDistance: String, rideDuration: String) {
        val username = getUsername() // 从 SharedPreferences 获取用户名
        val apiService1 = RetrofitClient.userApi
        val apiService2 = RetrofitClient.ridePostApi

        // 使用 Retrofit 调用 API 获取用户 ID
        apiService1.getUserId(username).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful && response.body() != null) {
                    try {
                        // 获取并打印返回的响应内容
                        val responseBody = response.body()!!.string()
                        Log.d("InsertActivity", "Response Body: $responseBody")

                        // 使用 JSONObject 解析 JSON 响应
                        val jsonObject = JSONObject(responseBody)
                        val userId = jsonObject.getInt("userId")

                        // 创建 RidePost 对象
                        val ridePost = RidePost(
                            userId = userId, // 直接使用从响应中获取的 userId
                            title = title,
                            content = content,
                            imageUrl = imageUri?.toString() ?: "",  // 可以上传到服务器并获取 URL
                            distance = rideDistance.toFloatOrNull() ?: 0.0f,  // 将字符串转换为 float
                            duration = rideDuration
                        )

                        // 发送 POST 请求
                        apiService2.createRidePost(ridePost).enqueue(object : Callback<RidePostResponse> {
                            override fun onResponse(call: Call<RidePostResponse>, response: Response<RidePostResponse>) {
                                if (response.isSuccessful) {
                                    Toast.makeText(this@insert, "发布成功", Toast.LENGTH_SHORT).show()
                                    finish()  // 可以选择关闭当前 Activity
                                } else {
                                    Toast.makeText(this@insert, "发布失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                                }
                            }

                            override fun onFailure(call: Call<RidePostResponse>, t: Throwable) {
                                Toast.makeText(this@insert, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
                            }
                        })
                    } catch (e: Exception) {
                        Toast.makeText(this@insert, "解析用户 ID 失败: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this@insert, "获取用户 ID 失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(this@insert, "网络错误: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }


    private fun getUsername(): String {
        val sharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        return sharedPreferences.getString("username", "") ?: ""
    }

    // 处理选择图片的结果
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK && data != null) {
            selectedImageUri = data.data
            try {
                // 将选择的图片显示在ImageView中
                val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                imageView.setImageBitmap(bitmap)
                imageView.visibility = ImageView.VISIBLE
            } catch (e: IOException) {
                e.printStackTrace()
                Toast.makeText(this, "无法加载图片", Toast.LENGTH_SHORT).show()
            }
        }
    }


}