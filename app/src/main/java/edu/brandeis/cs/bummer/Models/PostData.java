package edu.brandeis.cs.bummer.Models;

/**
 * Created by ACW on 27/11/2017.
 */

public class PostData {
    private String imageURL;

    public PostData(String imageURL) {

        this.imageURL = imageURL;
    }

    public PostData() {
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    @Override
    public String toString() {
        return "PostData{" +
                "imageURL='" + imageURL + '\'' +
                '}';
    }
}
