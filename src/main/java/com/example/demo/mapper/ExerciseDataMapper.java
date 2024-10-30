package com.example.demo.mapper;

import com.example.demo.entity.ExerciseData;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 用于定义数据访问方法
 */
@Mapper
public interface ExerciseDataMapper {
    //插入骑行数据
    @Insert("INSERT INTO exercise_data(username, total_duration, path_coordinates) VALUES(#{username}, #{totalDuration}, #{pathCoordinates})")
    void insertExerciseData(@Param("username") String username,
                            @Param("totalDuration") Integer totalDuration,
                            @Param("pathCoordinates") String pathCoordinates);

    //获取骑行记录的方法
    @Select("SELECT * FROM exercise_data WHERE username = #{username}")
    List<ExerciseData> selectExerciseDataByUsername(@Param("username") String username);

}
