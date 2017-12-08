package edu.brandeis.cs.bummer.Profile;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import de.hdodenhof.circleimageview.CircleImageView;
import edu.brandeis.cs.bummer.Details.InfoActivity;
import edu.brandeis.cs.bummer.Models.PostData;
import edu.brandeis.cs.bummer.Post.ImageUploadInfo;
import edu.brandeis.cs.bummer.R;
import edu.brandeis.cs.bummer.Utils.BottomNavigationHelper;
import edu.brandeis.cs.bummer.Utils.GridViewHelper;

/**
 * Created by yancheng on 2017/10/31.
 */

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "ProfileActivity";
    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 2;

    DatabaseReference databaseReference;
    private GridView gridView;
    private ImageView menu;
    private FirebaseAuth mAuth;
    private CircleImageView profile_image;
    private TextView post_count_tv;

    List<ImageUploadInfo> imageUploadInfoList = new ArrayList<>();



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        gridView = (GridView) findViewById(R.id.gridView);
        post_count_tv = (TextView) findViewById(R.id.tvPosts);

        menu = (ImageView) findViewById(R.id.profileMenu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth = FirebaseAuth.getInstance();
                mAuth.signOut();
            }
        });

        profile_image = (CircleImageView) findViewById(R.id.profile_image);
        Resources res = ProfileActivity.this.getResources();
        Bitmap bmp= BitmapFactory.decodeResource(res, R.drawable.ic_default_profile_image);
        profile_image.setImageBitmap(bmp);

        //setupBottomNavigationView();
        setupToolbar();
        setupBottomNavigationView();
        getImages();
    }


    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.profileToolBar);
        setSupportActionBar(toolbar);


        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Log.d(TAG, "onMenuItemClick: clicked menu item: " + item);

                switch (item.getItemId()) {
                    case R.id.profileMenu:
                        Log.d(TAG, "onMenuItemClick: Navigating to Profile Prefences");
                }

                return false;
            }
        });
    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView() {
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }




/*    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.profile_menu, menu);
        return true;


    }*/

    private void getImages() {
        String Database_Path = "user_photos";
        databaseReference = FirebaseDatabase.getInstance().getReference(Database_Path);
        Query query = databaseReference
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){
                    ImageUploadInfo imageUploadInfo = new ImageUploadInfo();
                    Bitmap tmpbitmap;
                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                    try {
                        imageUploadInfo.imageName = objectMap.get("imageName").toString();
                        imageUploadInfo.imageURL = objectMap.get("imageURL").toString();
                        imageUploadInfoList.add(imageUploadInfo);
                        // Getting selected image into Bitmap.

                    }catch(NullPointerException e){
                        Log.e(TAG, "onDataChange: NullPointerException: " + e.getMessage() );
                    }

                }

                Log.e(TAG, "onDataChange: "+ "imageUploadList.len = " + imageUploadInfoList.size() );
                ArrayList<String> imgUrls = new ArrayList<String>();
                for(int i = 0; i < imageUploadInfoList.size(); i++){
                    imgUrls.add(PostData.toThumbURL(imageUploadInfoList.get(i).getImageURL()));
                }
                GridViewHelper adapter = new GridViewHelper(mContext,R.layout.layout_circle,
                        "", imgUrls);
                gridView.setAdapter(adapter);

                post_count_tv.setText(String.valueOf(imageUploadInfoList.size()));

                gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        //mOnGridImageSelectedListener.onGridImageSelected(imageUploadInfoList.get(position), ACTIVITY_NUM);
                        Intent intent = new Intent(ProfileActivity.this, InfoActivity.class);
                        String[] data = new String[2];

                        data[0] = imageUploadInfoList.get(position).getImageURL();
                        data[1] = imageUploadInfoList.get(position).getImageName();
                        intent.putExtra("MarkerURL",data);
                        startActivity(intent);
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: query cancelled.");
            }


        });
    }

}
