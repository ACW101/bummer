package edu.brandeis.cs.bummer.Models;

/**
 * Created by ACW on 12/11/2017.
 */

public class User {
    private String email;
    private String name;
    private long posts;
    private long explored;
    private long views;
    private String profile_photo;

    @Override
    public String toString() {
        return "User{" +
                "email='" + email + '\'' +
                ", name='" + name + '\'' +
                ", posts=" + posts +
                ", explored=" + explored +
                ", views=" + views +
                ", profile_photo='" + profile_photo + '\'' +
                '}';
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getPosts() {
        return posts;
    }

    public void setPosts(long posts) {
        this.posts = posts;
    }

    public long getExplored() {
        return explored;
    }

    public void setExplored(long explored) {
        this.explored = explored;
    }

    public long getViews() {
        return views;
    }

    public void setViews(long views) {
        this.views = views;
    }

    public String getProfile_photo() {
        return profile_photo;
    }

    public void setProfile_photo(String profile_photo) {
        this.profile_photo = profile_photo;
    }

    public User() {

    }

    public User(String email, String name, long posts, long explored, long views, String profile_photo) {

        this.email = email;
        this.name = name;
        this.posts = posts;
        this.explored = explored;
        this.views = views;
        this.profile_photo = profile_photo;
    }
}
