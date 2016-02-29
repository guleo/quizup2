package com.example.frank.activity;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.example.frank.test.R;
import com.example.frank.ui.RoundImageView;
import java.io.FileNotFoundException;

/**
 * Created by frank on 2016/2/6.
 */
public class UserHeadActivity extends Activity implements View.OnClickListener {

    private boolean isChanged = false;
    private Button mButton;
    private RoundImageView mHeadImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_head);
        initView();
    }

    private void initView() {
        mHeadImage  = (RoundImageView) findViewById(R.id.image_head);
        mHeadImage.setOnClickListener(this);
        mButton = (Button) findViewById(R.id.image_post);
        mButton.setOnClickListener(this);
        mButton.setClickable(isChanged);
    }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.image_head:
                    Intent intent = new Intent(Intent.ACTION_PICK,
                            android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(intent, 1);
                    break;
                case R.id.image_post:break;
            }
        }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //if (requestCode == RESULT_OK)
        if (data!=null && data.getData()!=null){
            isChanged = true;
            mButton.setClickable(isChanged);
                Uri uri = data.getData();

            ContentResolver cr = this.getContentResolver();
            try {
                Log.d("image","上传成功");
                Bitmap b = BitmapFactory.decodeStream(cr.openInputStream(uri));
                mHeadImage.setImageBitmap(b);
                mHeadImage.invalidate();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}

