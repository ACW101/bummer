package edu.brandeis.cs.bummer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    private static final String TAG = "MainActivity";
    private static final int SIGN_IN = 7;
    private FirebaseUser currentUser;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        // Buttons
        findViewById(R.id.sign_out_button).setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        this.currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            Log.d(TAG, "current user = " + currentUser.getUid());
        } else {
            signIn();
        }
    }

    private void signIn() {
        startActivityForResult(new Intent(this, SigninActivity.class), SIGN_IN);
    }

    private void signOut() {
        mAuth.signOut();
        signIn();
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.sign_out_button) {
            signOut();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "in ok");
                Toast.makeText(this, "ok", Toast.LENGTH_SHORT).show();
            }
            if (resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(this, "canceled", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
