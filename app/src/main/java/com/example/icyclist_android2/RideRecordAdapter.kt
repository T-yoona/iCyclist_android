package com.example.icyclist_android2

import android.content.SharedPreferences
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

/**
 * RecyclerView 适配器，用于显示骑行记录
 *
 * @property rideRecords 骑行记录的列表
 * @property sharedPreferences 用于获取用户名的 SharedPreferences
 */

class RideRecordAdapter (private val rideRecords: List<RideRecord>,private val sharedPreferences: SharedPreferences) : RecyclerView.Adapter<RideRecordAdapter.RideRecordViewHolder>() {

    class RideRecordViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        private val durationTextView: TextView = itemView.findViewById(R.id.durationTextView)
        private val pathCoordinatesTextView: TextView = itemView.findViewById(R.id.pathCoordinatesTextView)

        fun bind(rideRecord: RideRecord,username:String) {
            usernameTextView.text = "用户名: $username"//从登录获取用户名
            durationTextView.text = "时长: ${rideRecord.totalDuration}秒"
            // 解析路径坐标
            val coordinatesList = rideRecord.pathCoordinates.split(";")
            // 只取起点和终点
            val startCoordinate = coordinatesList.firstOrNull()
            val endCoordinate = coordinatesList.lastOrNull()

            val formattedCoordinates = buildString {
                startCoordinate?.let { coordinate ->
                    val latLng = coordinate.split(",")
                    append("起点: 纬度: ${latLng[0]}, 经度: ${latLng[1]}\n")
                }
                endCoordinate?.let { coordinate ->
                    val latLng = coordinate.split(",")
                    append("终点: 纬度: ${latLng[0]}, 经度: ${latLng[1]}\n")
                }
            }

            pathCoordinatesTextView.text = "路径坐标:\n$formattedCoordinates"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RideRecordViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_ride_record, parent, false)
        return RideRecordViewHolder(view)
    }

    override fun onBindViewHolder(holder: RideRecordViewHolder, position: Int) {
        // 从 SharedPreferences 获取用户名
        val username = sharedPreferences.getString("username", "") ?: ""
        holder.bind(rideRecords[position],username)
    }

    override fun getItemCount(): Int {
        return rideRecords.size
    }
}