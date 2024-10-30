package com.example.demo.entity;

import com.example.demo.entity.RidePost;

public class RidePostResponseDTO {
    private RidePost ridePost;
    private String username;

    // 构造函数
    public RidePostResponseDTO(RidePost ridePost, String username) {
        this.ridePost = ridePost;
        this.username = username;
    }

    // Getters 和 Setters
    public RidePost getRidePost() {
        return ridePost;
    }

    public void setRidePost(RidePost ridePost) {
        this.ridePost = ridePost;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
