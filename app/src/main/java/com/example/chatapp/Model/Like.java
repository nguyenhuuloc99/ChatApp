package com.example.chatapp.Model;

public class Like {
    private String id_post;
    private String id_user;
    public Like() {
    }
    public Like(String id_post, String id_user) {
        this.id_post = id_post;
        this.id_user = id_user;
    }

    public String getId_post() {
        return id_post;
    }

    public void setId_post(String id_post) {
        this.id_post = id_post;
    }
    public String getId_user() {
        return id_user;
    }
    public void setId_user(String id_user) {
        this.id_user = id_user;
    }
}
