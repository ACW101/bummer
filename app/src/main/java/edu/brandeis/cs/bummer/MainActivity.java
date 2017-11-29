package edu.brandeis.cs.bummer;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBufferResponse;
import com.google.android.gms.location.places.PlaceDetectionClient;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.Logger;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.brandeis.cs.bummer.Auth.SigninActivity;
import edu.brandeis.cs.bummer.Models.PostData;
import edu.brandeis.cs.bummer.Utils.BottomNavigationHelper;
import edu.brandeis.cs.bummer.Utils.LocationData;
import edu.brandeis.cs.bummer.Utils.MapDataHelper;
import edu.brandeis.cs.bummer.Utils.models.PlaceInfo;


public class MainActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.OnConnectionFailedListener{

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
    private static final String TAG = "MainActivity";
    private static final int ACTIVITY_NUM = 0;
    private static final String Fine_Location = Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String Coarse_Location = Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final int Location_Permission_Request_Code = 1234;
    private static final float DEFAULT_ZOOM = 15f;
    private static final double CURRENT_LATTITUTE = 42.3669;
    private static final double CURRENT_LONGTITUTE = -71.2583;
    private static final LatLngBounds LAT_LNG_BOUNDS = new LatLngBounds(
            new LatLng(-40, -168), new LatLng(71, 168));

    //widgets
    private AutoCompleteTextView mSearchText;
    private ImageView mGps;

    private FirebaseUser currentUser;
    private Boolean mLocationPermissionGranted = false;
    private GoogleMap mMap;
    private FusedLocationProviderClient mFusedLocationClient;
    private PlaceAutocompleteAdapter mPlaceAutocompleteAdapter;
    private GeoDataClient mGeoDataClient;
    private PlaceDetectionClient mPlaceDetectionClient;
    private PlaceInfo mPlace;

    private FirebaseAuth mAuth;
    private Context mContext = MainActivity.this;
    private MapDataHelper mapDataHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mSearchText = (AutoCompleteTextView)findViewById(R.id.input_search);
        mGps = (ImageView)findViewById(R.id.ic_gps);
        getLocationPermission();

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

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: map is ready");
        mMap = googleMap;

        if (mLocationPermissionGranted) {
            getDeviceLocation();
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.setMyLocationEnabled(true);
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            init();

        }
    }

    private boolean isNetworkConnected() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        return cm.getActiveNetworkInfo() != null;
    }


    public void init(){
        Log.d(TAG, "init: initializing");

        mGeoDataClient = Places.getGeoDataClient(this, null);

        // Construct a PlaceDetectionClient.
        mPlaceDetectionClient = Places.getPlaceDetectionClient(this, null);

        mSearchText.setOnItemClickListener(mAutocompleteClickListener);

        mPlaceAutocompleteAdapter = new PlaceAutocompleteAdapter(this, mGeoDataClient,
                LAT_LNG_BOUNDS, null);

        mSearchText.setAdapter(mPlaceAutocompleteAdapter);

        mSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if(actionId == EditorInfo.IME_ACTION_SEARCH
                        || actionId == EditorInfo.IME_ACTION_DONE
                        || keyEvent.getAction() == KeyEvent.ACTION_DOWN
                        || keyEvent.getAction() == KeyEvent.KEYCODE_ENTER){
                    //execute our method for searching
                    geoLocate();
                }

                return false;
            }
        });

        mGps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick: clicked gps icon");
                getDeviceLocation();
            }
        });

        hideSoftKeyboard();
    }

    public void geoLocate(){
        Log.d(TAG, "getDeviceLocation: getting the devices current location");

        String searchString = mSearchText.getText().toString();

        Geocoder geocoder = new Geocoder(MainActivity.this);
        List<Address> list = new ArrayList<>();
        try{
            list = geocoder.getFromLocationName(searchString, 1);
        }catch (IOException e){
            Log.e(TAG,"geoLocation: IOException: " + e.getMessage());

        }

        if(list.size() > 0){
            Address address = list.get(0);

            Log.d(TAG, "geoLocate: found a location: " + address.toString());

//           Toast.makeText(this, address.toString(), Toast.LENGTH_SHORT).show();
            moveCamera(new LatLng(address.getLatitude(), address.getLongitude()),
                    DEFAULT_ZOOM, address.getAddressLine(0));
        }
    }

    public void getDeviceLocation(){
        Log.d(TAG,"getDeviceLocation: getting the devices current location");
         mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
         try{
             if(mLocationPermissionGranted){
                 Task location = mFusedLocationClient.getLastLocation();
                 location.addOnCompleteListener(new OnCompleteListener() {
                     @Override
                     public void onComplete(@NonNull Task task) {
                         if (task.isSuccessful()) {
                             Log.d(TAG, "onComplete: found location");
//                             Location currentLocation = (Location)task.getResult();
                             moveCamera(new LatLng(CURRENT_LATTITUTE, CURRENT_LONGTITUTE),
                                     DEFAULT_ZOOM, "My Location");

                             // get image informations
                             mapDataHelper = new MapDataHelper(mContext, CURRENT_LATTITUTE, CURRENT_LONGTITUTE);

                         } else {
                             Log.d(TAG, "onComplete: current location is null");
                             Toast.makeText(MainActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                         }
                     }
                 });
             }

         }catch(SecurityException e){
             Log.e(TAG,"getDeviceLocation: SecurityException: " + e.getMessage());
         }
     }

     public void moveCamera(LatLng latLng, float zoom, String title){
         Log.d(TAG,"moveCamera: moving to: lat: " + latLng.latitude + ", lng: " + latLng.longitude);
         mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoom));

         if(!title.equals("My Location")) {
             MarkerOptions options = new MarkerOptions()
                     .position(latLng)
                     .title(title);
             mMap.addMarker(options);
         }
            hideSoftKeyboard();
     }

     private void initMap(){
         // Obtain the SupportMapFragment and get notified when the map is ready to be used.
         Log.d(TAG,"Initializing Map");
         SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
         mapFragment.getMapAsync(this);
     }



    private void setupBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);

        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    public void getLocationPermission() {
        Log.d(TAG,"Getting location permissions");
        String[] permissions = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                                        Manifest.permission.ACCESS_COARSE_LOCATION};
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), Fine_Location) == PackageManager.PERMISSION_GRANTED){
            if(ContextCompat.checkSelfPermission(this.getApplicationContext(), Coarse_Location) == PackageManager.PERMISSION_GRANTED){
                mLocationPermissionGranted = true;
                initMap();
            }else{
                ActivityCompat.requestPermissions(this,permissions,Location_Permission_Request_Code);
            }
        }else{
            ActivityCompat.requestPermissions(this,permissions,Location_Permission_Request_Code);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        Log.d(TAG,"onRequestPermissionsResult: called.");
        mLocationPermissionGranted = false;
        switch (requestCode){
            case Location_Permission_Request_Code: {
                if(grantResults.length > 0){
                    for(int i = 0; i < grantResults.length; i++){
                        if(grantResults[i]!= PackageManager.PERMISSION_GRANTED){
                            mLocationPermissionGranted = false;
                            Log.d(TAG,"onRequestPermissionsResult: permission failed.");
                            return;
                        }

                    }
                    mLocationPermissionGranted = true;
                    Log.d(TAG,"onRequestPermissionsResult: permission granted.");
                    initMap();
                }
            }
        }

    }

    private void hideSoftKeyboard(){
        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    /*
         --------------- google places API autocomplete suggestions ------------
     */
    private AdapterView.OnItemClickListener mAutocompleteClickListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l){
            hideSoftKeyboard();

            final AutocompletePrediction item = mPlaceAutocompleteAdapter.getItem(i);
            final String placeId = item.getPlaceId();

            Task<PlaceBufferResponse> placeResult = mGeoDataClient.getPlaceById(placeId);

            placeResult.addOnCompleteListener(mUpdatePlaceDetailsComplete);
        }
    };

    private OnCompleteListener<PlaceBufferResponse> mUpdatePlaceDetailsComplete = new OnCompleteListener<PlaceBufferResponse>() {
        @Override
        public void onComplete(@NonNull Task<PlaceBufferResponse> task) {
            if (task.isSuccessful()) {
                PlaceBufferResponse places = task.getResult();
                Place myPlace = places.get(0);

                try{
                    mPlace = new PlaceInfo();
                    mPlace.setName(myPlace.getName().toString());
                    mPlace.setAddress(myPlace.getAddress().toString());
//                    mPlace.setAttributions(myPlace.getAttributions().toString());
                    mPlace.setWebsiteUri(myPlace.getWebsiteUri());
                    mPlace.setPhoneNumber(myPlace.getPhoneNumber().toString());
                    mPlace.setRating(myPlace.getRating());
                    mPlace.setLatLng(myPlace.getLatLng());

                    Log.d(TAG, "OnComplete: " + mPlace.toString());

                }catch (NullPointerException e){
                    Log.e(TAG,"OnResult: " + e.getMessage());
                }

                moveCamera(new LatLng(myPlace.getViewport().getCenter().latitude,
                        myPlace.getViewport().getCenter().longitude), DEFAULT_ZOOM, mPlace.getName());
                places.release();


            } else {
                Log.e(TAG, "Place not found.");
            }
        }
    };

    public void updateMarker(LocationData locationData) {
        Log.d(TAG, "updating marker");
        for (PostData post : locationData.getPosts()) {
            Log.d(TAG, post.getImageURL());
        }
    }
}
