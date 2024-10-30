package com.example.icyclist_android2

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

/**
 * 这是一个适配器，使用了 RidePostResponseDTO 来表示骑行动态的每个数据项，并正确地处理了用户名、图片、骑行标题、内容以及点击事件的逻辑
 */
class DiscoveryAdapter(
    private val ridePosts: List<RidePostResponseDTO>,  // 使用 RidePostResponseDTO
    private val currentUsername: String,
    private val onDeleteClick: (RidePostResponseDTO) -> Unit // 更新为 RidePostResponseDTO
) : RecyclerView.Adapter<DiscoveryAdapter.RidePostViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RidePostViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.status_item, parent, false)

        return RidePostViewHolder(view)
    }

    override fun onBindViewHolder(holder: RidePostViewHolder, position: Int) {
        val ridePostResponse = ridePosts[position]

        // 设置用户名
        holder.usernameTextView.text = ridePostResponse.username

        // 设置骑行标题和内容
        holder.titleTextView.text = ridePostResponse.ridePost.title
        holder.contentTextView.text = ridePostResponse.ridePost.content

        // 设置骑行数据
        holder.statsTextView.text = "距离: ${ridePostResponse.ridePost.distance}公里 | 时长: ${ridePostResponse.ridePost.duration}"

        // 加载图片
        Glide.with(holder.itemView.context)
            .load(ridePostResponse.ridePost.imageUrl)
            .placeholder(R.drawable.img)  // 加载中的占位图
            .error(R.drawable.img_1)  // 加载失败时的替代图
            .into(holder.rideImageView)

        // 点击事件（删除逻辑）
        holder.itemView.setOnClickListener {
            if (ridePostResponse.username == currentUsername) {
                showDeleteConfirmationDialog(holder.itemView.context, ridePostResponse, onDeleteClick)
            }else {
                Log.d("Discovery", "Clicked on a post that does not belong to the current user.")
            }
        }
    }

    override fun getItemCount(): Int = ridePosts.size

    class RidePostViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val usernameTextView: TextView = itemView.findViewById(R.id.usernameTextView)
        val titleTextView: TextView = itemView.findViewById(R.id.titleTextView)
        val contentTextView: TextView = itemView.findViewById(R.id.contentTextView)
        val rideImageView: ImageView = itemView.findViewById(R.id.rideImageView)
        val statsTextView: TextView = itemView.findViewById(R.id.statsTextView)
    }

    private fun showDeleteConfirmationDialog(context: Context, ridePost: RidePostResponseDTO, onDeleteClick: (RidePostResponseDTO) -> Unit) {
        AlertDialog.Builder(context)
            .setTitle("删除骑行动态")
            .setMessage("确定要删除这条骑行动态吗？")
            .setPositiveButton("确定") { _, _ ->
                // 用户点击了“确定”，调用删除回调
                onDeleteClick(ridePost) // 传递 RidePostResponseDTO
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
