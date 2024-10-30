package com.example.icyclist_android2

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView

/**
 * 用户信息的接收、登出确认对话框、以及不同按钮的跳转
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val adRecyclerView: RecyclerView = findViewById(R.id.adRecyclerView)
        val adAdapter = AdAdapter()
        adRecyclerView.layoutManager = LinearLayoutManager(this)
        adRecyclerView.adapter = adAdapter

        // 接收用户信息
      //  val userId = intent.getIntExtra("USER_ID", -1)
        val userNickname = intent.getStringExtra("USER_NICKNAME")

        if (/*userId != -1 && */userNickname != null) {
            val nicknameTextView = findViewById<TextView>(R.id.nicknameTextView)
           // val idTextView = findViewById<TextView>(R.id.idTextView)
            nicknameTextView.text = userNickname
            //idTextView.text = userId.toString()
        } else {
            // 用户未登录，跳转到登录页面
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()  // 关闭当前活动
        }

        val logoutButton = findViewById<Button>(R.id.logoutButton)

        logoutButton.setOnClickListener {
            // 确认对话框
            AlertDialog.Builder(this).apply {
                setTitle("确认退出")
                setMessage("您确定要退出应用程序吗？")
                setPositiveButton("确定") { dialog, which ->
                    logout()
                }
                setNegativeButton("取消") { dialog, which ->
                    dialog.dismiss()
                }
                create()
                show()
            }
        }


        //跳转按钮
        val button2 = findViewById<Button>(R.id.button2)
        val button3 = findViewById<Button>(R.id.button3)
        val button4 =findViewById<Button>(R.id.button4)
        button2.setOnClickListener {
            val intent1 = Intent(this,sport::class.java)
            startActivity(intent1)
        }
        button3.setOnClickListener {
            val intent2 = Intent(this,discovery::class.java)
            startActivity(intent2)
        }
        button4.setOnClickListener{
            val intent3 = Intent(this, ViewRideActivity::class.java)
            startActivity(intent3)
        }
    }


    private fun logout() {
        // 关闭所有活动，退出应用程序
        finishAffinity()
    }
}