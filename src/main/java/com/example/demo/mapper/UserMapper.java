package com.example.demo.mapper;

import com.example.demo.entity.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
@Mapper
public interface UserMapper {
    @Insert("INSERT INTO users(username, password) VALUES(#{username}, #{password})")
    void register(User user);

    @Select("SELECT * FROM users WHERE username = #{username} AND password = #{password}")
    User login(String username, String password);

    @Select("SELECT * FROM users WHERE username = #{username}")
    User findByUsername(@Param("username") String username);

    @Select("SELECT COUNT(*) FROM users WHERE username = #{username}")
    int countByUsername(String username); // 新增方法

    // 根据用户 ID 获取用户名
    @Select("SELECT username FROM users WHERE id = #{userId}")
    String findUsernameById(int userId);
}
