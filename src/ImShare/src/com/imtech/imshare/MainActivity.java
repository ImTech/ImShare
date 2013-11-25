package com.imtech.imshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.core.auth.IAuthService;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.sns.share.WeiboShare;

public class MainActivity extends Activity implements OnClickListener{
    final static String TAG = "Share#MainActivity";
    EditText mEdMessage;
    Button mBtnPost;
    Button mBtnWeibo;
    IAuthService mAuthService;
    WeiboShare mShare = new WeiboShare();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdMessage = (EditText) findViewById(R.id.editMessage);
        mBtnPost = (Button) findViewById(R.id.btnPost);
        mBtnWeibo = (Button) findViewById(R.id.btnWeibo);
        
        mBtnPost.setOnClickListener(this);
        mBtnWeibo.setOnClickListener(this);
        
        mAuthService = AuthService.getInstance();
        mAuthService.addAuthListener(new AuthListener());
        mShare.setListener(new ShareListener());
    }
    
    class AuthListener implements IAuthListener {

        @Override
        public void onAuthFinished(SnsType snsType, AuthRet ret) {
            Toast.makeText(MainActivity.this, "finished:" + ret.state, Toast.LENGTH_SHORT).show();
        }
        
    }
    
    class ShareListener implements IShareListener {

        @Override
        public void onShareFinished(ShareRet ret) {
        	Toast.makeText(MainActivity.this, "share ret:" + ret.state, Toast.LENGTH_SHORT).show();
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
        if (v.getId() == R.id.btnWeibo) {
            mAuthService.auth(SnsType.WEIBO, getApplicationContext(), MainActivity.this);
        } else if (v.getId() == R.id.btnPost) {
            AccessToken token = mAuthService.getAccessToken(SnsType.WEIBO);
        	if (token == null) {
        		Toast.makeText(this, "auth first", Toast.LENGTH_SHORT).show();
        		shakeView(mBtnWeibo);
        		return;
        	}
        	ShareObject obj = new ShareObject();
        	obj.text = mEdMessage.getText().toString();
        	mShare.share(getApplicationContext(), this, token, obj);
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	super.onActivityResult(requestCode, resultCode, data);
    	mAuthService.checkActivityResult(requestCode, resultCode, data);
    }
}
