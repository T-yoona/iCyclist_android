package com.example.demo.service;

import com.example.demo.entity.ExerciseData;
import com.example.demo.mapper.ExerciseDataMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 用于调用 Mapper 方法
 */

@Service
public class ExerciseDataService {
    private final ExerciseDataMapper exerciseDataMapper;

    @Autowired
    public ExerciseDataService(ExerciseDataMapper exerciseDataMapper) {
        this.exerciseDataMapper = exerciseDataMapper;
    }

    public void saveExerciseData(ExerciseData exerciseData) {
        exerciseDataMapper.insertExerciseData(exerciseData.getUsername(), exerciseData.getTotalDuration(), exerciseData.getPathCoordinates());
    }

    public List<ExerciseData> getExerciseDataByUsername(String username) {
        return exerciseDataMapper.selectExerciseDataByUsername(username);
    }
}
