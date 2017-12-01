package edu.brandeis.cs.bummer.Auth;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import edu.brandeis.cs.bummer.MainActivity;
import edu.brandeis.cs.bummer.R;

public class EmailVerificationActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "EmailVeriActivity";
    private Context mContext;

    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]
    private FirebaseUser currentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verification);

        mContext = this;
        mAuth = FirebaseAuth.getInstance();

        // set onClickListener
        Button resendBtn = (Button) findViewById(R.id.resend_btn);
        Button checkVeri = (Button) findViewById(R.id.check_verification);
        resendBtn.setOnClickListener(this);
        checkVeri.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        reloadUser();
        super.onStart();
    }

    protected void reloadUser() {
        mAuth.getCurrentUser()
                .reload()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "onSuccess");
                        currentUser = mAuth.getCurrentUser();
                        if (currentUser.isEmailVerified()) {
                            Log.d(TAG, "verified");
                            startActivity(new Intent(mContext, MainActivity.class));
                        }
                    }
                });
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

    @Override
    public void onBackPressed() {
        mAuth.signOut();
        super.onBackPressed();
    }

    @Override
    public void onClick(View view) {
        Log.d(TAG, "onClick");
        int i = view.getId();
        if (i == R.id.resend_btn) {
            Log.d(TAG, "onClick: email_veri_btn clicked");
            sendEmailVerification();
        }
        if (i == R.id.check_verification) {
            reloadUser();
        }
    }
}
