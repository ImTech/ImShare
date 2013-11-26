package com.imtech.imshare.ui;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.Toast;

import com.imtech.imshare.R;
import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.core.auth.IAuthService;
import com.imtech.imshare.core.share.IShareService;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.sns.share.ShareObject.Image;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.utils.Log;

public class TestActivity extends Activity implements OnClickListener {
    final static String TAG = "Share#MainActivity";
    private static final int PHOTO_REQUEST_GALLERY = 12;// 从相册中选择
    IAuthService mAuthService = AuthService.getInstance();
    {
        mAuthService.addAuthListener(new AuthListener());
    }
    IShareService mShareSeivce = ShareService.sharedInstance();
    {
        mShareSeivce.addListener(new ShareListener());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button) findViewById(R.id.share);
        btn.setOnClickListener(this);

        mAuthService.loadCachedTokens(this);
    }

    class AuthListener implements IAuthListener {

        @Override
        public void onAuthFinished(SnsType snsType, AuthRet ret) {
            Toast.makeText(TestActivity.this, "finished:" + ret.state,
                    Toast.LENGTH_SHORT).show();
        }

    }

    class ShareListener implements IShareListener {

        @Override
        public void onShareFinished(final ShareRet ret) {
            TestActivity.this.runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    Toast.makeText(TestActivity.this, "share ret:" + ret.state,
                            Toast.LENGTH_SHORT).show();
                }
            });

        }

    }

    private void shakeView(View v) {
        TranslateAnimation a = new TranslateAnimation(0, 30, 0, 30);
        a.setRepeatCount(3);
        a.setRepeatMode(Animation.REVERSE);
        a.setDuration(100);
        v.startAnimation(a);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.share:
            if (mAuthService.getAccessToken(SnsType.WEIBO) == null) {
                Toast.makeText(TestActivity.this, "先授权", Toast.LENGTH_SHORT)
                        .show();
                mAuthService.auth(SnsType.WEIBO, getApplicationContext(),
                        TestActivity.this);
                return;
            }
            gotoSelectPic();
            break;
        }
    }

    private void gotoSelectPic() {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*");
        startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_REQUEST_GALLERY) {
//            String path = data != null ? data.getDataString() : null;
            // 11-26 23:42:18.192: D/SNS_WeiboShare(4295):
            // pic:content://media/external/images/media/9678
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String path = cursor.getString(columnIndex);
            cursor.close();
            Log.d(TAG, "path:" + path);
            ShareObject obj = new ShareObject();
            obj.text = "分享测试";
//            obj.images.add(new Image("", path));
            mShareSeivce.share(getApplicationContext(), TestActivity.this, obj,
                    SnsType.WEIBO);
            return;
        }
        mAuthService.checkActivityResult(requestCode, resultCode, data);
    }
}
