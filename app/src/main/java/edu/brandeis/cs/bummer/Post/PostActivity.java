package edu.brandeis.cs.bummer.Post;

import android.content.Context;
import android.view.View;
import java.lang.Object;
import android.content.Intent;
import android.graphics.Bitmap;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.*;

import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.util.Log;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.ImageView;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.android.gms.tasks;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.ByteArrayOutputStream;
import com.google.firebase.storage.*;
import edu.brandeis.cs.bummer.Utils.BottomNavigationHelper;
import edu.brandeis.cs.bummer.Profile.ProfileActivity;
import edu.brandeis.cs.bummer.R;



/*post button
*
* posted
*
*
* current location photo
*
*
* connect to firebase photos
*
*
*
* */


/**
 * Created by CaiweiWu on 2017/11/23.
 */

public class PostActivity extends AppCompatActivity {

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private static final String TAG = "PostActivity";
    private Context mContext = PostActivity.this;
    private static final int ACTIVITY_NUM = 2;
    private Button share_btn;
    private ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        imageView = (ImageView) findViewById(R.id.image);
    }


    // Create a storage reference from our app
    StorageReference storageRef = storage.getReference();

    // Create a reference to "mountains.jpg"
    StorageReference mountainsRef = storageRef.child("headshot.png");

    // Create a reference to 'images/mountains.jpg'
    StorageReference mountainImagesRef = storageRef.child("res/drawable/headshot.png");

    // While the file names are the same, the references point to different files
//    mountainsRef.getName().equals(mountainImagesRef.getName());    // true
//    mountainsRef.getPath().equals(mountainImagesRef.getPath());    // false

    imageView.setDrawingCacheEnabled(true);
    imageView.buildDrawingCache();
    Bitmap bitmap = imageView.getDrawingCache();
    ByteArrayOutputStream baos = new ByteArrayOutputStream();
    bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
    byte[] data = baos.toByteArray();

    UploadTask uploadTask = mountainsRef.putBytes(data);
    uploadTask.addOnFailureListener(new OnFailureListener() {
        @Override
        public void onFailure(@NonNull Exception exception) {
            // Handle unsuccessful uploads
        }
    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
        @Override
        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
            // taskSnapshot.getMetadata() contains file metadata such as size, content-type, and download URL.
            Uri downloadUrl = taskSnapshot.getDownloadUrl();
        }
    });







//
//    private void setupBottomNavigationView(){
//        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
//        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
//        BottomNavigationHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
//    }



}
