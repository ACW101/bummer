package edu.brandeis.cs.bummer.Auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import edu.brandeis.cs.bummer.Utils.BaseActivity;
import edu.brandeis.cs.bummer.R;
import edu.brandeis.cs.bummer.Utils.FirebaseHelper;

public class SignupActivity extends BaseActivity implements View.OnClickListener {

    private static final String TAG = "EmailPassword";

    // UI instance
    private EditText mEmailField, mPasswordField, mPasswordConfirmField, mNameField;

    // user input Strings
    private String email, password, passwordConfirm, name;

    // activity context
    private Context mContext;

    //

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private FirebaseHelper mFirebaseHelper;
    private DatabaseReference mRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        mContext = SignupActivity.this;
        mFirebaseHelper = new FirebaseHelper(mContext);

        // Buttons
        findViewById(R.id.email_sign_up_button).setOnClickListener(this);
        findViewById(R.id.sign_in_textview).setOnClickListener(this);

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);
        mPasswordConfirmField = findViewById(R.id.field_confirm_password);
        mNameField = findViewById(R.id.field_name);



        initFirebase();
    }

    @Override
    protected void onStart() {
        super.onStart();
//        mAuth.addAuthStateListener(mAuthListener);
    }

    private void createAccount() {
        Log.d(TAG, "createAccount:" + email);
        email = mEmailField.getText().toString();
        password = mPasswordField.getText().toString();
        passwordConfirm = mPasswordConfirmField.getText().toString();
        name = mNameField.getText().toString();
        if (!validateForm()) {
            return;
        }

        showProgressDialog();

        // [START create_user_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up success
                            Log.d(TAG, "createUserWithEmail:success");
                            sendEmailVerification();
                            Intent returnIntent = new Intent();
                            setResult(Activity.RESULT_OK, returnIntent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(SignupActivity.this, "Authentication failed." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [START_EXCLUDE]
                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END create_user_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }
        if (!TextUtils.equals(password, passwordConfirm)) {
            mPasswordConfirmField.setError("Passwords do not match.");
            valid = false;
        } else {
            mPasswordConfirmField.setError(null);
        }
        if (TextUtils.isEmpty(name)) {
            mNameField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        return valid;
    }

    @Override
    public void onClick(View view) {
        int i = view.getId();
        if (i == R.id.email_sign_up_button) {
            createAccount();
        } else if (i == R.id.sign_in_textview) {
            startActivity(new Intent(mContext, SigninActivity.class));
        }
    }

    private void sendEmailVerification() {
        // Send verification email
        // [START send_email_verification]
        Log.d(TAG, "sendEmailVeri: start sending email");
        final FirebaseUser user = mAuth.getCurrentUser();
        user.sendEmailVerification()
                .addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "sendEmailVer: send email veri success");
                            mFirebaseHelper.addUser(email, name);
                            Toast.makeText(mContext,
                                    "Verification email sent to " + user.getEmail(),
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            Log.e(TAG, "sendEmailVerification", task.getException());
                            Toast.makeText(mContext,
                                    "Failed to send verification email." + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }
                        // [END_EXCLUDE]
                    }
                });
        // [END send_email_verification]
    }

    private void initFirebase() {
        Log.d(TAG, "initFirebase: set firebase instance and listeners");

        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mRef = mFirebaseDatabase.getReference();

//        mAuthListener = new FirebaseAuth.AuthStateListener() {
//            @Override
//            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
//                FirebaseUser user = firebaseAuth.getCurrentUser();
//
//                if (user != null) {
//                    Log.d(TAG, "onAuthStateChanged: user is signed in " + user.getUid());
//                    mRef.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            Log.d(TAG, "onDataChange: dataChange");
//                            mFirebaseHelper.addUser(email, name);
//                        }
//
//                        @Override
//                        public void onCancelled(DatabaseError databaseError) {
//                            Log.d(TAG, "onDataChange: cancelled");
//                        }
//                    });
//                } else {
//                    Log.d(TAG, "onAuthStateChanged: user is signed out");
//                }
//            }
//        };
    }
}
