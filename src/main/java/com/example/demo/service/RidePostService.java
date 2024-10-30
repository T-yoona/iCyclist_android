package com.example.demo.service;

import com.example.demo.entity.RidePost;
import com.example.demo.mapper.RidePostMapper;
import com.example.demo.entity.RidePostResponseDTO;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class RidePostService {
    @Autowired
    private RidePostMapper ridePostMapper;
    @Autowired
    private UserMapper userMapper;

    // 发布新的骑行动态
    public void createRidePost(RidePost ridePost) {
        ridePostMapper.insertRidePost(ridePost);
    }

    // 获取所有骑行动态
    public List<RidePost> getAllRidePosts() {
        return ridePostMapper.getAllRidePosts();
    }

    // 获取某用户的骑行动态
    public List<RidePost> getRidePostsByUserId(int userId) {
        return ridePostMapper.getRidePostsByUserId(userId);
    }

    // 删除骑行动态
    public void deleteRidePost(int id) {
        ridePostMapper.deleteRidePost(id);
    }

    // 获取所有骑行动态并包含用户名
    public List<RidePostResponseDTO> getAllRidePostsWithUsername() {
        List<RidePost> ridePosts = ridePostMapper.getAllRidePosts();
        List<RidePostResponseDTO> ridePostResponseDTOList = new ArrayList<>();

        for (RidePost post : ridePosts) {
            // 根据 userId 查找用户名
            String username = userMapper.findUsernameById(post.getUserId());

            // 创建包含骑行动态和用户名的 DTO
            RidePostResponseDTO responseDTO = new RidePostResponseDTO(post, username);
            ridePostResponseDTOList.add(responseDTO);
        }

        return ridePostResponseDTOList;
    }

}
