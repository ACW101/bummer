package edu.brandeis.cs.bummer.Models;

import android.util.Log;

/**
 * Created by ACW on 27/11/2017.
 */

public class PostData {
    private static final String TAG = "PostData";
    private String imageURL;

    public String getImageName() {
        return imageName;
    }

    public void setImageName(String imageName) {
        this.imageName = imageName;
    }

    private String imageName;

    public PostData(String imageURL, String imageName) {
        this.imageURL = imageURL;
        this.imageName = imageName;
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

    public static String toThumbURL(String url) {
        int i = url.indexOf("%2F");
        String thumbURL = url.substring(0, i + 3) + "thumb_" + url.substring(i + 3);
        return thumbURL;
    }
}
