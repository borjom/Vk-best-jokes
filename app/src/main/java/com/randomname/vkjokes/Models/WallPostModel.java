package com.randomname.vkjokes.Models;

import java.util.ArrayList;

public class WallPostModel {

    private String text;
    private ArrayList<String> postPhotos;
    private int type;
    private int id;
    private int commentsCount;
    private int likeCount;
    private String date;

    public WallPostModel() {
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public ArrayList<String> getPostPhotos() {
        return postPhotos;
    }

    public void setPostPhotos(ArrayList<String> postPhotos) {
        this.postPhotos = postPhotos;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCommentsCount() {
        return commentsCount;
    }

    public void setCommentsCount(int commentsCount) {
        this.commentsCount = commentsCount;
    }

    public int getLikeCount() {
        return likeCount;
    }

    public void setLikeCount(int likeCount) {
        this.likeCount = likeCount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
