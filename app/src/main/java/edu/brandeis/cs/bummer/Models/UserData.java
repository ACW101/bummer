package edu.brandeis.cs.bummer.Models;

/**
 * Created by ACW on 15/11/2017.
 */

public class UserData {
    public UserData() {
    }

    public UserData(String name, long posts, long explored, long views) {

        this.name = name;
        this.posts = posts;
        this.explored = explored;
        this.views = views;
    }

    private String name;
    private long posts;
    private long explored;
    private long views;

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

    @Override
    public String toString() {
        return "UserData{" +
                "name='" + name + '\'' +
                ", posts=" + posts +
                ", explored=" + explored +
                ", views=" + views +
                '}';
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
}
