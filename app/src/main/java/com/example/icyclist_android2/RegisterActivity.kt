package com.example.icyclist_android2

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * 实现了用户注册功能，并进行了基本的输入验证和网络请求处理。
 */
class RegisterActivity : AppCompatActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        nicknameEditText = findViewById(R.id.regnicknameEditText)
        passwordEditText = findViewById(R.id.regpasswordEditText)
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.progressBar)

        registerButton.setOnClickListener {
            val username = nicknameEditText.text.toString()
            val password = passwordEditText.text.toString()
            Log.d("RegisterRequest", "Username: $username")


            if (validateInput(username, password)) {
                registerUser(username, password)
            }
        }
    }

    // Function to validate user input
    private fun validateInput(nickname: String, password: String): Boolean {
        return when {
            nickname.isEmpty() -> {
                nicknameEditText.error = "昵称不能为空"
                false
            }
            password.isEmpty() -> {
                passwordEditText.error = "密码不能为空"
                false
            }
            password.length < 6 -> {
                passwordEditText.error = "密码至少需要6个字符"
                false
            }
            else -> true
        }
    }

    private fun registerUser(nickname: String, password: String) {

        progressBar.visibility = ProgressBar.VISIBLE  // Show progress

        val newUser = User(username = nickname, password = password)
        RetrofitClient.userApi.register(newUser).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                progressBar.visibility = ProgressBar.GONE  // Hide progress

                if (response.isSuccessful) {
                    Toast.makeText(this@RegisterActivity, "注册成功", Toast.LENGTH_SHORT).show()
                    finish()  // Close registration activity
                } else {
                    Toast.makeText(this@RegisterActivity, "注册失败: ${response.message()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                progressBar.visibility = ProgressBar.GONE  // Hide progress
                Toast.makeText(this@RegisterActivity, "网络错误，请重试: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })


    }
}