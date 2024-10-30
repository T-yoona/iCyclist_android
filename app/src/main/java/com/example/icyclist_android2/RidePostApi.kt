package com.example.icyclist_android2

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * 定义了与骑行动态相关的基本 CRUD 操作。
 */
interface RidePostApi {
    /**
     * 创建新的骑行动态
     *
     * @param ridePost 包含骑行动态信息的对象
     * @return 创建的骑行动态的响应
     */
    @POST("/api/rideposts/create")
    fun createRidePost(@Body ridePost: RidePost): Call<RidePostResponse>

    /**
     * 获取所有骑行动态及其用户信息
     *
     * @return 包含所有骑行动态和用户名称的列表
     */
    @GET("/api/rideposts/allwithusers")
    fun getAllRidePosts(): Call<List<RidePostResponseDTO>> // 修改为适应返回的 DTO

    /**
     * 删除指定 ID 的骑行动态
     *
     * @param id 要删除的骑行动态的 ID
     * @return 空响应
     */

    @DELETE("/api/rideposts/delete/{id}")
    fun deleteRidePost(@Path("id") id: Int): Call<Void>

}