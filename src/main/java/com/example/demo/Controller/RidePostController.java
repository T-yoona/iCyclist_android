package com.example.demo.Controller;

import com.example.demo.entity.RidePost;
import com.example.demo.entity.RidePostResponseDTO;
import com.example.demo.service.RidePostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/rideposts")
public class RidePostController {
    @Autowired
    private RidePostService ridePostService;

    // 发布新的骑行动态
    @PostMapping("/create")
    public ResponseEntity<Map<String, String>> createRidePost(@RequestBody RidePost ridePost) {
        // 检查接收到的 RidePost 对象
        System.out.println("Received RidePost: " + ridePost);

        ridePostService.createRidePost(ridePost);
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Ride post created successfully.");
        return ResponseEntity.ok(response);
    }

    // 获取所有骑行动态
    @GetMapping("/all")
    public List<RidePost> getAllRidePosts() {
        return ridePostService.getAllRidePosts();
    }


    // 获取所有骑行动态并包含用户名
    @GetMapping("/allwithusers")
    public List<RidePostResponseDTO> getAllRidePostsWithUsername() {
        return ridePostService.getAllRidePostsWithUsername();
    }

    // 获取某用户的骑行动态
    @GetMapping("/user/{userId}")
    public List<RidePost> getRidePostsByUserId(@PathVariable int userId) {
        return ridePostService.getRidePostsByUserId(userId);
    }

    // 删除骑行动态
    @DeleteMapping("/delete/{id}")
    public String deleteRidePost(@PathVariable int id) {
        ridePostService.deleteRidePost(id);
        return "骑行动态删除成功！";
    }

}
