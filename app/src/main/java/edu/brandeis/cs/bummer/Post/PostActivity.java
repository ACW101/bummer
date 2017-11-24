package edu.brandeis.cs.bummer.Post;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import edu.brandeis.cs.bummer.Profile.ProfileActivity;
import edu.brandeis.cs.bummer.R;


/**
 * Created by CaiweiWu on 2017/11/23.
 */

public class PostActivity extends AppCompatActivity {
    private Context mContext = PostActivity.this;
    private static final int ACTIVITY_NUM = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
    }

}
