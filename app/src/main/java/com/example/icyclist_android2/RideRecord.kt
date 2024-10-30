package com.example.icyclist_android2

/**
 * 骑行记录数据实体类
 */

data class RideRecord( val username: String,
                       val totalDuration: Int,
                       val pathCoordinates: String // 这里可以存储路径坐标
)
