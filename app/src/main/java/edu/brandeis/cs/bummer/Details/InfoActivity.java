package edu.brandeis.cs.bummer.Details;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;

import java.net.MalformedURLException;
import java.net.URL;

import edu.brandeis.cs.bummer.MainActivity;
import edu.brandeis.cs.bummer.R;

public class InfoActivity extends AppCompatActivity {

    ImageView imageView;
    private Context mContext = InfoActivity.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        imageView = (ImageView)findViewById(R.id.image_info);
    }

    @Override
    public void startActivityForResult(Intent intent, int requestCode){
        super.startActivityForResult(new Intent(this, MainActivity.class), 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1){
            if (resultCode == RESULT_OK){

                URL url = null;
                Bitmap bmp = null;
                try {
                    url = new URL(getIntent().getExtras().getString("MarkerURL"));
//                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
                    Glide.with(mContext).asBitmap().load(url).into(new SimpleTarget<Bitmap>() {
                        @Override
                        public void onResourceReady(Bitmap resource, Transition<? super Bitmap> transition) {
                            imageView.setImageBitmap(resource);
                        }
                    });
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
