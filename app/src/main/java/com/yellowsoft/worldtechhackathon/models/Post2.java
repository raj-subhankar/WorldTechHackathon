package com.yellowsoft.worldtechhackathon.models;

/**
 * Created by subhankar on 11/21/2016.
 */
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class Post2 {
    @SerializedName("user")
    private User user;
    @SerializedName("_id")
    private String id;
    @SerializedName("imageUrl")
    private String imageUrl;
    @SerializedName("postBody")
    private String postBody;
    @SerializedName("likesCount")
    private Integer likesCount;
    @SerializedName("likes")
    private ArrayList<String> likes;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getPostBody() {
        return postBody;
    }

    public void setPostBody(String postBody) {
        this.postBody = postBody;
    }

    public Integer getLikesCount() {
        return likesCount;
    }

    public void setLikesCount(Integer likesCount) {
        this.likesCount = likesCount;
    }

    public ArrayList<String> getLikes() {
        return likes;
    }

    public void setLikes(ArrayList<String> likes) {
        this.likes = likes;
    }

    public void addLike(String user) {likes.add(user);}

    public void removeLike(String user) {likes.remove(user);}
}

