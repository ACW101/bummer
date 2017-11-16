package edu.brandeis.cs.bummer.Utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import edu.brandeis.cs.bummer.Models.User;
import edu.brandeis.cs.bummer.Models.UserData;
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

    public void addUser(String email, String name, String profile_photo) {
        if (mAuth.getCurrentUser() != null) {
            Log.d(TAG, "addUser: user is signed in");
            userID = mAuth.getCurrentUser().getUid();
        } else {
            Log.d(TAG, "addUser: user not signed in");
            Toast.makeText(mContext, "Error creating user", Toast.LENGTH_SHORT).show();
            return;
        }
        // create a new user in db
        User newUser = new User(userID, email, name, profile_photo);
        Log.d(TAG, "userID: " + userID);
        mRef.child(mContext.getString(R.string.dbname_users))
                .child(userID)
                .setValue(newUser);
    }

    public UserData getUserData(DataSnapshot data) {
        Log.d(TAG, "FirebaseHelper: getUserData: getting user's data from database");
        UserData userData = null;

        for (DataSnapshot ds : data.getChildren()) {
             if (ds.getKey().equals(mContext.getString(R.string.dbname_usersData))) {
                 Log.d(TAG, "FirebaseHelper: getUserInfo, getting userData");
                 userData = ds.child(userID).getValue(UserData.class);
             }
        }
        if (userData == null) {
            Log.e(TAG, "FirebaseHelper: getUserData: error getting user data");
        }
        return userData;
    }
}
