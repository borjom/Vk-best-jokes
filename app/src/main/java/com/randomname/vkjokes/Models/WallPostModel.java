package com.randomname.vkjokes.Models;

import java.util.ArrayList;

public class WallPostModel {

    private String text;
    private ArrayList<String> postPhotos;
    private int type;

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
}
