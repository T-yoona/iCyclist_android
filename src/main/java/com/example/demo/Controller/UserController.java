package com.example.demo.Controller;


import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody User user) {
        if (userService.usernameExists(user.getUsername())) {
            return ResponseEntity.badRequest().body("用户名已存在");
        }
        userService.register(user);
        System.out.println("Received username: " + user.getUsername());
        System.out.println("Password: " + user.getPassword());
        return ResponseEntity.ok("Registration successful");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User loggedInUser = userService.login(user.getUsername(), user.getPassword());
        if (loggedInUser != null) {
            return ResponseEntity.ok(loggedInUser);
        } else {
            return ResponseEntity.status(401).body("Invalid credentials");
        }
    }

    @GetMapping("/id")
    public Map<String, Integer> getUserId(@RequestParam String username) {
        int userId = userService.getUserIdByUsername(username);

        // 创建一个 Map 来存储 JSON 响应
        Map<String, Integer> response = new HashMap<>();
        response.put("userId", userId);

        // 返回 JSON 对象
        return response;
    }
}