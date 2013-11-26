package com.imtech.imshare.ui;

import android.app.Activity;
import android.content.Intent;
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
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.sns.share.WeiboShare;
import com.imtech.imshare.utils.Log;

public class TestActivity extends Activity implements OnClickListener{
    final static String TAG = "Share#MainActivity";
    private static final int PHOTO_REQUEST_GALLERY = 12;// 从相册中选择
//    IAuthService mAuthService;
    WeiboShare mShare = new WeiboShare();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button btn = (Button)findViewById(R.id.share);
        btn.setOnClickListener(this);
        
//        mAuthService = AuthService.getInstance();
//        mAuthService.loadCachedTokens(this);
//        mAuthService.addAuthListener(new AuthListener());
//        mShare.setListener(new ShareListener());
    }
    
    class AuthListener implements IAuthListener {

        @Override
        public void onAuthFinished(SnsType snsType, AuthRet ret) {
            Toast.makeText(TestActivity.this, "finished:" + ret.state, Toast.LENGTH_SHORT).show();
        }
        
    }
    
    class ShareListener implements IShareListener {

        @Override
        public void onShareFinished(ShareRet ret) {
        	Toast.makeText(TestActivity.this, "share ret:" + ret.state, Toast.LENGTH_SHORT).show();
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
    	switch(v.getId()){
    	case R.id.share:
    		gotoSelectPic();
    		break;
    	}
    }
    
    private void gotoSelectPic(){
    	IAuthService authService = AuthService.getInstance();
    	authService.auth(SnsType.TENCENT_WEIBO, getApplicationContext(), this);
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
//    	mAuthService.checkActivityResult(requestCode, resultCode, data);
    	String path = data != null ? data.getDataString() : null;
    	Log.d(TAG, "onActivityResult path: " + path);
    }
}
