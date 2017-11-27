package edu.brandeis.cs.bummer.Profile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import edu.brandeis.cs.bummer.R;
import edu.brandeis.cs.bummer.Utils.BottomNavigationHelper;

/**
 * Created by yancheng on 2017/10/31.
 */

public class ProfileActivity extends AppCompatActivity{
    private Context mContext = ProfileActivity.this;
    private static final int ACTIVITY_NUM = 4;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setupBottomNavigationView();
    }
    private void setupBottomNavigationView(){

//        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
//
//        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
//        BottomNavigationHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
//        Menu menu = bottomNavigationViewEx.getMenu();
//        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
//        menuItem.setChecked(true);
    }
}
