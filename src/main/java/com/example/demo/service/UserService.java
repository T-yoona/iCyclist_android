package com.example.demo.service;


import com.example.demo.entity.User;
import com.example.demo.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    @Autowired
    private UserMapper userMapper;

    public void register(User user) {
        userMapper.register(user);
    }

    public User login(String username, String password) {
        return userMapper.login(username, password);
    }
    // 新增方法：根据用户名获取用户 ID
    public Integer getUserIdByUsername(String username) {
        User user = userMapper.findByUsername(username);
        return user != null ? user.getId() : null; // 返回用户 ID，如果用户不存在则返回 null
    }

    // 新增方法：检查用户名是否存在
    public boolean usernameExists(String username) {
        return userMapper.countByUsername(username) > 0;
    }


}

