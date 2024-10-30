package com.example.icyclist_android2

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * 实现了基本的登录功能，使用了 Retrofit 进行 API 请求，处理了用户的输入验证、登录和错误反馈。
 */
class LoginActivity : AppCompatActivity() {

    private lateinit var nicknameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var registerButton: Button
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        nicknameEditText = findViewById(R.id.nicknameEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        registerButton = findViewById(R.id.registerButton)
        progressBar = findViewById(R.id.progressBar)

        loginButton.setOnClickListener {
            val nickname = nicknameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (validateInput(nickname, password)) {
                loginUser(nickname, password)
            }

        }

        registerButton.setOnClickListener {
            // 跳转到注册页面
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
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
            else -> true
        }
    }

    private fun loginUser(nickname: String, password: String) {
        progressBar.visibility = ProgressBar.VISIBLE  // Show progress

        val user = User(username = nickname, password = password)
        RetrofitClient.userApi.login(user).enqueue(object : Callback<User> {
            override fun onResponse(call: Call<User>, response: Response<User>) {
                progressBar.visibility = ProgressBar.GONE  // Hide progress

                if (response.isSuccessful) {

                    val loggedInUser = response.body() // 获取服务器返回的用户信息
                    if (loggedInUser != null) {
                        saveUsername(loggedInUser.username)

                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                       // intent.putExtra("USER_ID", loggedInUser.id) // 假设返回的对象有 ID
                        intent.putExtra("USER_NICKNAME", loggedInUser.username)
                        startActivity(intent)
                        finish()  // 关闭登录页面
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "用户名或密码错误", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<User>, t: Throwable) {
                progressBar.visibility = ProgressBar.GONE  // Hide progress
                Toast.makeText(this@LoginActivity, "网络错误，请重试: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun saveUsername(username: String) {
        val sharedPreferences: SharedPreferences = getSharedPreferences("app_preferences", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("username", username)
        editor.apply() // 提交更改
    }
}