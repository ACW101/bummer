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

import edu.brandeis.cs.bummer.MainActivity;
import edu.brandeis.cs.bummer.Models.PostData;

/**
 * Created by ACW on 27/11/2017.
 */

public class MapDataHelper {
    private static final String TAG = "MapDataHelper";
    private Context mContext;
    private double lat, lon;
    private String lonLatBinName;
    private LocationData locationData;

    // firebase stuff
    private FirebaseAuth mAuth;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mRef;
    private FirebaseAuth.AuthStateListener mAuthListener;

    public MapDataHelper(Context mContext, double lat, double lon) {
        Log.d(TAG, "MapDataHelper: constructor");
        this.lat = lat;
        this.lon = lon;
        this.lonLatBinName = toLatLonBin(lat, lon);
        Log.d(TAG, "binname: " + lonLatBinName);
        this.mContext = mContext;
        setupFirebase();
    }

    public static String toLatLonBin(double lat, double lon) {
        return String.format("%.4fx%.4f", lon, lat).replace('.', '_');
    }

    public void setupFirebase() {
        Log.d(TAG, "MapDataHelper: setupFirebaseAuth: setting up firebase.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();
        mFirebaseHelper = new FirebaseHelper(mContext);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };
        // add AuthListener
        mAuth.addAuthStateListener(mAuthListener);

        mRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: data change");
                // get the photos from lonLatBinName
                locationData = mFirebaseHelper.getLocationData(dataSnapshot, lonLatBinName);
                MainActivity ma = (MainActivity) mContext;
                ma.updateMarker(locationData);
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.d(TAG, "onCancelled: cancelled" + databaseError);
            }
        });

        // add connection listener
        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addListenerForSingleValueEvent(new ValueEventListener() {
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
}
