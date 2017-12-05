package edu.brandeis.cs.bummer.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.brandeis.cs.bummer.Models.PostData;
import edu.brandeis.cs.bummer.Models.User;
import edu.brandeis.cs.bummer.R;

/**
 * Created by ACW on 12/11/2017.
 */

public class FirebaseHelper {
    private Context mContext;
    private static final String TAG = "FirebaseHelper";
    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mRef;

    private String userID;

    public FirebaseHelper(Context context) {
        mAuth = FirebaseAuth.getInstance();
        mContext = context;
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

        if (mAuth.getCurrentUser() != null) {
            userID = mAuth.getCurrentUser().getUid();
        }
    }

    public void addUser(String email, String name) {
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "addUser: user is signed in");
            userID = mAuth.getCurrentUser().getUid();
        } else {
            Log.d(TAG, "addUser: user not signed in");
            Toast.makeText(mContext, "Error creating user", Toast.LENGTH_SHORT).show();
            return;
        }
        // create a new user in db
        User newUser = new User(email, name, 0,0,0,"");
        Log.d(TAG, "userID: " + userID);
        mRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(newUser);
    }

    public User getUser(DataSnapshot data) {
        Log.d(TAG, "FirebaseHelper: getUser: getting user's data from database");
        User user = null;

        for (DataSnapshot ds : data.getChildren()) {
             if (ds.getKey().equals(mContext.getString(R.string.dbname_users))) {
                 Log.d(TAG, "FirebaseHelper: getUserInfo, getting userData");
                 user = ds.child(userID).getValue(User.class);
             }
        }
        if (user == null) {
            Log.e(TAG, "FirebaseHelper: getUserData: error getting user data");
        }
        return user;
    }

    public LocationData getLocationData(DataSnapshot data, LatLng location, String[] binNames) {
        Log.d(TAG, "FirebaseHelper: getLocationData: getting location data from database");
        LocationData locationData = new LocationData(location);

        for (DataSnapshot ds : data.getChildren()) {
            if (ds.getKey().equals(mContext.getString(R.string.dbname_location))) {
                for (String binName : binNames) {
                    Log.d(TAG, "getLocationData: Getting bin " + binName);
                    DataSnapshot locationDataSnapshot = ds.child(binName);

                    if (locationDataSnapshot.exists()) {
                        Log.d(TAG, "getLocationData: Bin " + binName + " exists");
                        int counter = 1;
                        for (DataSnapshot childs : locationDataSnapshot.getChildren()) {
                            Log.d(TAG, "FirebaseHelper: getLocationData: getting PostData " + counter);
                            locationData.append(childs.getValue(PostData.class));
                            counter++;
                        }
                    } else {
                        Log.d(TAG, "getLocationData: Bin " + binName + " doesn't exists");
                    }
                }
            }
        }
        return locationData;
    }
}
