package com.imtech.imshare;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.auth.WeiboAuth;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.sns.share.WeiboShare;

public class MainActivity extends Activity implements OnClickListener{
    final static String TAG = "Share#MainActivity";
    EditText mEdMessage;
    Button mBtnPost;
    Button mBtnWeibo;
    WeiboAuth mAuth = new WeiboAuth();
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
        
        mAuth.setListener(new AuthListener());
        mShare.setListener(new ShareListener());
    }
    
    class AuthListener implements IAuthListener {

        @Override
        public void onAuthFinished(AuthRet ret) {
            Toast.makeText(MainActivity.this, "finished:" + ret.state, Toast.LENGTH_SHORT).show();
        }
        
    }
    
    class ShareListener implements IShareListener {

        @Override
        public void onShareFinished(ShareRet ret) {
        }
        
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnWeibo) {
            mAuth.auth(getApplicationContext(), MainActivity.this);
        } else if (v.getId() == R.id.btnPost) {
            mShare.share(null);
        }
    }
}
