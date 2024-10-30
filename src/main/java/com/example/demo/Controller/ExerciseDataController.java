package com.example.demo.Controller;

import com.example.demo.entity.ExerciseData;
import com.example.demo.service.ExerciseDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 *用于处理前端请求
 */
@RestController
@RequestMapping("/exercise")
public class ExerciseDataController {
    private final ExerciseDataService exerciseDataService;

    @Autowired
    public ExerciseDataController(ExerciseDataService exerciseDataService) {
        this.exerciseDataService = exerciseDataService;
    }

    @PostMapping("/save")
    public void saveExerciseData(@RequestBody ExerciseData exerciseData) {
        exerciseDataService.saveExerciseData(exerciseData);
    }

    @GetMapping("/records/{username}")
    public ResponseEntity<List<ExerciseData>> getExerciseData(@PathVariable String username) {
        List<ExerciseData> records = exerciseDataService.getExerciseDataByUsername(username);
        for (ExerciseData data : records) {
            System.out.println("Username: " + data.getUsername());
            System.out.println("Total Duration: " + data.getTotalDuration());
            System.out.println("Path Coordinates: " + data.getPathCoordinates());
        }
        return ResponseEntity.ok(records);
    }
}
