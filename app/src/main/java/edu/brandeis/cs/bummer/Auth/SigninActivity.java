package edu.brandeis.cs.bummer.Auth;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.MainThread;
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

import edu.brandeis.cs.bummer.MainActivity;
import edu.brandeis.cs.bummer.R;
import edu.brandeis.cs.bummer.Utils.BaseActivity;



public class SigninActivity extends BaseActivity implements
        View.OnClickListener {

    private static final String TAG = "EmailPassword";
    private static final int SIGN_UP = 8;
    private static Context mContext;

    private EditText mEmailField;
    private EditText mPasswordField;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private FirebaseUser currentUser;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin);

        mContext = this;

        // Buttons onclick listeners
        findViewById(R.id.email_sign_in_button).setOnClickListener(this);
        findViewById(R.id.sign_up_textview).setOnClickListener(this);
        findViewById(R.id.forget_password_textview).setOnClickListener(this);

        // Views
        mEmailField = findViewById(R.id.field_email);
        mPasswordField = findViewById(R.id.field_password);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    public void onAuthStateChange() {
        this.currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "onAuthStateChange: current user = " + currentUser.getUid());
            // direct to email verify if not verified
            if (currentUser.isEmailVerified()) {
                startActivity(new Intent(mContext, MainActivity.class));
            } else {
                startActivity(new Intent(mContext, EmailVerificationActivity.class));
            }
        }
    }


    // [START on_start_check_user]
    @Override
    public void onStart() {
        super.onStart();
        onAuthStateChange();
    }
    // [END on_start_check_user]

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            onAuthStateChange();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SigninActivity.this, "Authentication failed: " + task.getException(),
                                    Toast.LENGTH_SHORT).show();
                        }

                        hideProgressDialog();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }

    private boolean validateForm() {
        boolean valid = true;

        String email = mEmailField.getText().toString();
        if (TextUtils.isEmpty(email)) {
            mEmailField.setError("Required.");
            valid = false;
        } else {
            mEmailField.setError(null);
        }

        String password = mPasswordField.getText().toString();
        if (TextUtils.isEmpty(password)) {
            mPasswordField.setError("Required.");
            valid = false;
        } else {
            mPasswordField.setError(null);
        }

        return valid;
    }

    private void sendPasswordResetEmail() {
        final String email = mEmailField.getText().toString();
        if (email.length() == 0) {
            Toast.makeText(mContext, getString(R.string.forget_password_empty_toast), Toast.LENGTH_LONG).show();
            return;
        }
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(SigninActivity.this, getString(R.string.forget_password_sent) + email, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(SigninActivity.this, "fail sending reset email: " + task.getException(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.email_sign_in_button) {
            Log.d(TAG, "onClick: email_sign_in_button: starting to sign in");
            signIn(mEmailField.getText().toString(), mPasswordField.getText().toString());
        } else if (i == R.id.sign_up_textview) {
            Log.d(TAG, "onClick: email_sign_up_button: start sign-up intent");
            startActivity(new Intent(SigninActivity.this, SignupActivity.class));
        } else if (i == R.id.forget_password_textview) {
            Log.d(TAG, "onClick: forget_password_clicked: send reset email");
            sendPasswordResetEmail();
        }
    }
}