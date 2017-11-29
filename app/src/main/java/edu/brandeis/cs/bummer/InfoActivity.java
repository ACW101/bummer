package edu.brandeis.cs.bummer;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoActivity extends AppCompatActivity {

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        imageView = (ImageView)findViewById(R.id.image_info);
        try {
            URL url = new URL("https://firebasestorage.googleapis.com/v0/b/bummer-ec3a6.appspot.com/o/100%2Fpuppy-1.jpg?alt=media&token=b34e8353-e7e9-46a7-8a23-11f27dd8c6ef");
            Bitmap bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            imageView.setImageBitmap(bmp);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
