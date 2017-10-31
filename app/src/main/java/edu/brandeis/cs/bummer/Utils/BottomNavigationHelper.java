package edu.brandeis.cs.bummer.Utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import edu.brandeis.cs.bummer.MainActivity;
import edu.brandeis.cs.bummer.PostActivity.PostActivity;
import edu.brandeis.cs.bummer.Profile.ProfileActivity;
import edu.brandeis.cs.bummer.R;

/**
 * Created by yancheng on 2017/10/31.
 */

public class BottomNavigationHelper {
    private static final String TAG = "BottomNavigationHelper";
    public static void setupBottomNavigation(BottomNavigationViewEx ex) {
        Log.d(TAG, "setupBottomNavigation: ");
        ex.enableAnimation(false);
        ex.enableItemShiftingMode(false);
        ex.enableShiftingMode(false);
        ex.setTextVisibility(false);


    }
    public static void enableNavigation(final Context context, final Activity callingActivity, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_map:
                        Intent intent1 = new Intent(context, MainActivity.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;



                    case R.id.ic_post:
                        Intent intent3 = new Intent(context,PostActivity.class);//ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;



                    case R.id.ic_profile:
                        Intent intent5 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 4
                        context.startActivity(intent5);
                        callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }


                return false;
            }
        });
    }
}
