package com.randomname.vkjokes.Models;

import android.database.Cursor;
import android.os.Parcel;
import android.os.Parcelable;

import com.randomname.vkjokes.SQLite.VkJokesOpenHelper;
import com.vk.sdk.api.model.VKApiPost;
import com.vk.sdk.api.model.VKApiVideo;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WallPostModel implements Parcelable {

    private String text;
    private ArrayList<String> postPhotos;
    private int type;
    private int id;
    private int commentsCount;
    private int likeCount;
    private String date;
    private boolean alreadyLiked;
    private boolean canPost;
    private int fromId;

    public WallPostModel() {
    }

    public WallPostModel(Cursor c) {
        text = c.getString(c.getColumnIndex(VkJokesOpenHelper.COLUMN_TEXT));
        postPhotos = convertStringToArrayList(c.getString(c.getColumnIndex(VkJokesOpenHelper.COLUMN_POST_PHOTOS)));
        type = c.getInt(c.getColumnIndex(VkJokesOpenHelper.COLUMN_TYPE));
        id = c.getInt(c.getColumnIndex(VkJokesOpenHelper.COLUMN_POST_ID));
        commentsCount = c.getInt(c.getColumnIndex(VkJokesOpenHelper.COLUMN_COMMENTS_COUNT));
        likeCount = c.getInt(c.getColumnIndex(VkJokesOpenHelper.COLUMN_LIKE_COUNT));
        date = c.getString(c.getColumnIndex(VkJokesOpenHelper.COLUMN_DATE));
        alreadyLiked = intToBoolean(c.getInt(c.getColumnIndex(VkJokesOpenHelper.COLUMN_ALREADY_LIKED)));
        canPost = intToBoolean(c.getInt(c.getColumnIndex(VkJokesOpenHelper.COLUMN_CAN_POST)));
        fromId = c.getInt(c.getColumnIndex(VkJokesOpenHelper.COLUMN_FROM_ID));
    }

    public WallPostModel(Parcel in) {
        text = in.readString();
        postPhotos = (ArrayList<String>) in.readSerializable();
        type = in.readInt();
        id = in.readInt();
        commentsCount = in.readInt();
        likeCount = in.readInt();
        date = in.readString();
        alreadyLiked = in.readByte() != 0;
        canPost = in.readByte() != 0;
        fromId = in.readInt();
    }

    private ArrayList<String> convertStringToArrayList(String input){
        String separator = "__,__";

        String[] array = input.split(separator);
        List<String> newList = Arrays.asList(array);
        ArrayList<String> output = new ArrayList();
        output.addAll(newList);

        return output;
    }

    private boolean intToBoolean(int input) {
        return input > 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final WallPostModel other = (WallPostModel) obj;
        if (this.id != other.id) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 53 * hash + this.id;
        /*hash = 53 * hash + (this.text != null ? this.text.hashCode() : 0);
        hash = 53 * hash + (this.postPhotos != null ? this.postPhotos.hashCode() : 0);
        hash = 53 * hash + (this.date != null ? this.date.hashCode() : 0);
        hash = 53 * hash + this.type;
        hash = 53 * hash + this.id;
        hash = 53 * hash + this.commentsCount;
        hash = 53 * hash + this.likeCount;
        hash = 53 * hash + this.fromId;
        hash = 53 * hash + (alreadyLiked ? 1 : 0);
        hash = 53 * hash + (canPost ? 1 : 0);*/
        return hash;
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

    public boolean getAlreadyLiked() {
        return alreadyLiked;
    }

    public void setAlreadyLiked(boolean alreadyLiked) {
        this.alreadyLiked = alreadyLiked;
    }

    public boolean getCanPost() {
        return canPost;
    }

    public void setCanPost(boolean canPost) {
        this.canPost = canPost;
    }

    public int getFromId() {
        return fromId;
    }

    public void setFromId(int fromId) {
        this.fromId = fromId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(text);
        dest.writeSerializable(postPhotos);
        dest.writeInt(type);
        dest.writeInt(id);
        dest.writeInt(commentsCount);
        dest.writeInt(likeCount);
        dest.writeString(date);
        dest.writeByte((byte) (alreadyLiked ? 1 : 0));
        dest.writeByte((byte) (canPost ? 1 : 0));
        dest.writeInt(fromId);
    }

    public static final Parcelable.Creator<WallPostModel> CREATOR = new Parcelable.Creator<WallPostModel>() {
        public WallPostModel createFromParcel(Parcel in) {
            return new WallPostModel(in);
        }

        public WallPostModel[] newArray(int size) {
            return new WallPostModel[size];
        }
    };
}
