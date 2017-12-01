package edu.brandeis.cs.bummer.Utils;

import java.util.ArrayList;

import edu.brandeis.cs.bummer.Models.PostData;

/**
 * Created by ACW on 27/11/2017.
 */

public class LocationData {
    private String location;
    private ArrayList<PostData> posts = new ArrayList<>();

    public LocationData(String location) {
        this.location = location;
    }

    public ArrayList<PostData> getPosts() {
        return posts;
    }

    public void append(PostData data) {
        this.posts.add(data);
    }

    public boolean isEmpty() {
        return posts.isEmpty();
    }
}
