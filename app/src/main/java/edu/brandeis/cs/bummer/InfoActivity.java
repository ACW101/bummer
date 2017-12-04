package edu.brandeis.cs.bummer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.IOException;
import java.net.URL;

public class InfoActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        imageView = (ImageView)findViewById(R.id.image_info);
    }



    @Override
    public void startActivityForResult(Intent intent, int requestCode) {
        super.startActivityForResult(new Intent(this,MainActivity.class), 1);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent){
        if (requestCode == 1){
            if(resultCode == RESULT_OK ){
                try {
                    URL url = new URL(intent.getExtras().getString("MarkerURL"));
                    Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    imageView.setImageBitmap(bmp);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
