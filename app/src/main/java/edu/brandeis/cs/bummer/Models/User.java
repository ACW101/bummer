package edu.brandeis.cs.bummer.Models;

/**
 * Created by ACW on 12/11/2017.
 */

public class User {
    private String name;
    private String user_id;
    private String email;
    private String username;
    private String profile_photo;

    public User(String user_id, String email, String name, String profile_photo) {
        this.user_id = user_id;
        this.username = username;
        this.name = name;
        this.email = email;
        this.profile_photo = profile_photo;
    }

    // empty constructor for firebase
    public User() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

}
