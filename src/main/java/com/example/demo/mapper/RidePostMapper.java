package com.example.demo.mapper;

import com.example.demo.entity.RidePost;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface RidePostMapper {

    // 插入骑行动态
    @Insert("INSERT INTO ride_posts (user_id, title, content, image_url, distance, duration) " +
            "VALUES (#{userId}, #{title}, #{content}, #{imageUrl}, #{distance}, #{duration})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    void insertRidePost(RidePost ridePost);

    // 获取所有骑行动态
    @Select("SELECT * FROM ride_posts ORDER BY created_at DESC")
    List<RidePost> getAllRidePosts();


    // 根据用户 ID 获取骑行动态
    @Select("SELECT * FROM ride_posts WHERE user_id = #{userId} ORDER BY created_at DESC")
    List<RidePost> getRidePostsByUserId(int userId);

    // 根据动态 ID 删除骑行动态
    @Delete("DELETE FROM ride_posts WHERE id = #{id}")
    void deleteRidePost(int id);
}
