package com.example.icyclist_android2

data class RidePost(
    val userId: Int,
    val title: String,
    val content: String,
    val imageUrl: String?, // 可选，用户可以不上传图片
    val distance: Float,
    val duration: String // 格式为 "HH:mm:ss"
)

data class RidePostResponse(
    val success: Boolean,
    val message: String
)