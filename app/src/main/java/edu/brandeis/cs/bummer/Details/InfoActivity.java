package edu.brandeis.cs.bummer.Details;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.net.MalformedURLException;
import java.net.URL;

import edu.brandeis.cs.bummer.R;

public class InfoActivity extends AppCompatActivity {

    ImageView imageView;
    private Context mContext = InfoActivity.this;
    private static final String TAG = "InfoActivity";
    ImageView mFavorite;
    ImageView mComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        imageView = (ImageView)findViewById(R.id.image_info);
        mFavorite = (ImageView)findViewById(R.id.love_button);
        mComment = (ImageView)findViewById(R.id.comment_button);

        URL url = null;
        try {
            url = new URL(getIntent().getExtras().getString("MarkerURL"));
//                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Glide.with(mContext).asBitmap().load(url).into(imageView);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }


        mFavorite.setOnClickListener(new View.OnClickListener() {
            int count = 0;
            @Override
            public void onClick(View view) {
                count++;
                if (count % 2 == 1){
                    Log.d(TAG, "onClick: my favorite");
                    mFavorite.setImageResource(R.drawable.ic_loved_sign);
                } else {
                    mFavorite.setImageResource(R.drawable.ic_love);
                }

            }
        });



    }

}
