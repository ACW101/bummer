package edu.brandeis.cs.bummer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import edu.brandeis.cs.bummer.Utils.BottomNavigationHelper;

public class MainActivity extends AppCompatActivity {

    private Context mContext = MainActivity.this;
    private static final int ACTIVITY_NUM = 0;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView imageView = findViewById(R.id.ImageView);
        imageView.setImageResource(R.drawable.jp_monkey);
        setupBottomNavigationView();
    }


    private void setupBottomNavigationView(){

        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);

        BottomNavigationHelper.setupBottomNavigation(bottomNavigationViewEx);
        BottomNavigationHelper.enableNavigation(mContext, this, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }


    public void MapsHome(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        startActivity(intent);
    }


}
