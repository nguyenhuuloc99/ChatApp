package com.example.chatapp.Model;

import java.io.Serializable;

public class Post implements Serializable {
    private String id_post;
    private String description;
    private String id_user;
    private String imageURL;

    public Post(String id_post, String description, String id_user, String imageURL) {
        this.id_post = id_post;
        this.description = description;
        this.id_user = id_user;
        this.imageURL = imageURL;
    }

    public Post() {
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId_user() {
        return id_user;
    }

    public void setId_user(String id_user) {
        this.id_user = id_user;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }
}
