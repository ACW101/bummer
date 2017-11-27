package edu.brandeis.cs.bummer.Profile;


import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toolbar;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;
import edu.brandeis.cs.bummer.Models.User;
import edu.brandeis.cs.bummer.Models.UserData;
import edu.brandeis.cs.bummer.R;
import edu.brandeis.cs.bummer.Utils.FirebaseHelper;

/**
 * Created by ACW on 15/11/2017.
 */

public class ProfileFragment extends Fragment {
    private static final String TAG = "ProfileFragment";
    private Context mContext;

    // firebase stuff
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mRef;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // UI refs
//    private CircleImageView mProfilePhoto;
    private TextView mPosts;
    private TextView mFollowers;
    private TextView mFollowing;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        mContext = getActivity();
        setupFirebaseAuth();

        // TextViews
//        mDisplayName = (TextView) view.findViewById(R.id.display_name);
//        mUsername = (TextView) view.findViewById(R.id.username);
//        mWebsite = (TextView) view.findViewById(R.id.website);
//        mDescription = (TextView) view.findViewById(R.id.description);
//        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mPosts = (TextView) view.findViewById(R.id.tvPosts);
        mFollowers = (TextView) view.findViewById(R.id.tvFollowers);
        mFollowing = (TextView) view.findViewById(R.id.tvFollowing);
//        mProgressBar = (ProgressBar) view.findViewById(R.id.profileProgressBar);
//        gridView = (GridView) view.findViewById(R.id.gridView);
//        toolbar = (Toolbar) view.findViewById(R.id.profileToolBar);
//        profileMenu = (ImageView) view.findViewById(R.id.profileMenu);
//        bottomNavigationView = (BottomNavigationViewEx) view.findViewById(R.id.bottomNavViewBar);
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onStop() {
        super.onStop();
    }

    //firebase methods
    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth() {
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");

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

                //retrieve user information from the database
                setProfileData(mFirebaseHelper.getUserData(dataSnapshot));
                //retrieve images for the user in question
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void setProfileData(UserData userData) {
        mPosts.setText(String.valueOf(userData.getPosts()));
    }
}
