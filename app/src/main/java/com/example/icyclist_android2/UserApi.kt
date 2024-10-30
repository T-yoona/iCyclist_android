package com.example.icyclist_android2

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface UserApi {
    /**
     * 用户注册
     *
     * @param user 用户信息
     * @return 返回响应体
     */
    @POST("/api/user/register")
    fun register(@Body user: User): Call<ResponseBody>

    /**
     * 用户登录
     *
     * @param user 用户信息
     * @return 返回登录用户信息
     */

    @POST("/api/user/login")
    fun login(@Body user: User): Call<User>

    /**
     * 根据用户名获取用户 ID
     *
     * @param username 用户名
     * @return 返回响应体，包含用户 ID
     */
    @GET("/api/user/id")
    fun getUserId(@Query("username") username: String): Call<ResponseBody>


}