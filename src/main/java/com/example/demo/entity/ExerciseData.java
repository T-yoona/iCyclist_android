package com.example.demo.entity;


public class ExerciseData {
    private Integer id;
    private String username;
    private Integer totalDuration;
    private String pathCoordinates;
// 无参构造函数
    public ExerciseData() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public Integer getTotalDuration() {
        return totalDuration;
    }

    public void setTotalDuration(Integer totalDuration) {
        this.totalDuration = totalDuration;
    }

    public String getPathCoordinates() {
        return pathCoordinates;
    }

    public void setPathCoordinates(String pathCoordinates) {
        this.pathCoordinates = pathCoordinates;
    }
}
