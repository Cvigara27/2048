package com.cifpfbmoll.a2048.Database;

import android.content.Intent;

public class ScoreObject {

    private Integer id;
    private String username;
    private Integer score;
    private String date;
    private String time;

    public ScoreObject(Integer id, String username, Integer score, String date, String time) {
        this.id = id;
        this.username = username;
        this.score = score;
        this.date = date;
        this.time = time;
    }

    public ScoreObject(){

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

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
}
