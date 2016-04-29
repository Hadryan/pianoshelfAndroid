package com.pianoshelf.joey.pianoshelf.composition;

/**
 * Created by joey on 27/04/16.
 */
public class VideoInfo {
    private String title;
    private String youtube_id;
    private String date; // TODO trello
    private String grade; // TODO enum
    private Integer user; // who submitted this, can be null so we use Integer

    // Getters and Setters
    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getYoutube_id() {
        return youtube_id;
    }

    public void setYoutube_id(String youtube_id) {
        this.youtube_id = youtube_id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public Integer getUser() {
        return user;
    }

    public void setUser(Integer user) {
        this.user = user;
    }
}