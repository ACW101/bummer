package edu.brandeis.cs.bummer;

import android.content.Context;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import edu.brandeis.cs.bummer.Auth.SigninActivity;
import edu.brandeis.cs.bummer.Utils.BaseActivity;
import edu.brandeis.cs.bummer.Utils.BottomNavigationHelper;

import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends FragmentActivity implements OnMapReadyCallback {
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 0;
    private GoogleMap mMap;

    private FirebaseUser currentUser;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private Context mContext = MainActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Buttons
        setupBottomNavigationView();
    }

    @Override
    public void onStart() {
        super.onStart();
        this.currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "current user = " + currentUser.getUid());
        } else {
            Log.d(TAG, "user not logged in, navigating to sign in page");
            Intent intent = new Intent(this, SigninActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }


    private void setupBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);

        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(
                new LatLng(42.366998, -71.258864), 16));

        mMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromResource(R.mipmap.bummer))
                .anchor(0.0f, 1.0f) // Anchors the marker on the bottom left
                .position(new LatLng(42.366998, -71.258864)));

        // Add a marker in Sydney and move the camera
//        LatLng waltham = new LatLng(42.366998, -71.258864);
//        mMap.addMarker(new MarkerOptions().position(waltham).title("Marker in Waltham"));
//        mMap.moveCamera(CameraUpdateFactory.newLatLng(waltham));
    }


    private void post(){





    }
}
