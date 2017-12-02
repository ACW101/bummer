package edu.brandeis.cs.bummer.Utils;

import android.content.Context;
import android.location.Location;
import android.renderscript.Sampler;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import edu.brandeis.cs.bummer.MainActivity;
import edu.brandeis.cs.bummer.Models.PostData;

/**
 * Created by ACW on 27/11/2017.
 */

public class MapDataHelper {
    private static final String TAG = "MapDataHelper";
    private Context mContext;
    private double lat, lon;
    private String latLonBinName;
    private LocationData locationData;

    // firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public MapDataHelper(Context mContext) {
        Log.d(TAG, "MapDataHelper: constructor");
        this.mContext = mContext;
        setupFirebase();
    }

    public void updateLocation(double lat, double lon) {
        Log.d(TAG, "updateLatLon: lat=" + lat + " lon=" + lon );
        this.lat = lat;
        this.lon = lon;
        this.latLonBinName = toLatLonBin(lat, lon);
        Log.d(TAG, "binname: " + this.latLonBinName);

        Log.d(TAG, "updateLocation: getting new data");
        getMapData();
    }

    /*
     * Get the String representation in DB of LatLon
     */
    public static String toLatLonBin(double lat, double lon) {
        double newLat = Math.round(lat * 10000) / 10000.0;
        double newLon = Math.round(lon * 10000) / 10000.0;
        return (newLat + "x" + newLon).replace('.', '_');
    }

    public void setupFirebase() {
        Log.d(TAG, "MapDataHelper: setupFirebaseAuth: setting up firebase.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseHelper = new FirebaseHelper(mContext);

        // add connection listener
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Toast.makeText(mContext, "connected to firebase", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(mContext, "disconnect from firebase", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });
    }

    private void getMapData() {
        mRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: data change");
                // get the photos from lonLatBinName
                if (latLonBinName != null) {
                    Log.d(TAG, "onDataChange: getting location data from firebase" +  latLonBinName);
                    locationData = mFirebaseHelper.getLocationData(dataSnapshot, latLonBinName);
                    MainActivity ma = (MainActivity) mContext;
                    ma.updateMarker(locationData);
                }
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: cancelled" + databaseError);
            }
        });
    }
}
