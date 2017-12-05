package edu.brandeis.cs.bummer.Details;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

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
    TextView mText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);
        imageView = (ImageView)findViewById(R.id.image_info);
        mFavorite = (ImageView)findViewById(R.id.love_button);
        mComment = (ImageView)findViewById(R.id.comment_button);
        mText = (TextView)findViewById(R.id.description);

        URL url = null;
        String[] result = getIntent().getExtras().getStringArray("MarkerURL");

        try {
            assert result != null;
            url = new URL(result[0]);
            String message = result[1];
//                    bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
            Glide.with(mContext).asBitmap().load(url).into(imageView);
            mText.setText(message);
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
