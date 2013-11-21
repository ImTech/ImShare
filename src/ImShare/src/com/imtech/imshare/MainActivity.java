package com.imtech.imshare;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.imtech.imshare.Share.ShareListener;
import com.imtech.imshare.utils.Util;

public class MainActivity extends Activity {
    final static String TAG = "Share#MainActivity";
    EditText mEdMessage;
    Button mBtnPost;
    Share mShare;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdMessage = (EditText) findViewById(R.id.editMessage);
        mBtnPost = (Button) findViewById(R.id.btnPost);
        
        mShare = new Share();
        mShare.init(getApplicationContext());
        
        mBtnPost.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if (!mShare.getTencent().isSessionValid()) {
                    Toast.makeText(MainActivity.this, "Login first", Toast.LENGTH_SHORT);
                    mShare.login(MainActivity.this);
                } else {
                    Util.showProgressDialog(MainActivity.this, "share", "processing");
                    mShare.post(MainActivity.this, mEdMessage.getText().toString());
                }
            }
        });
        
        mShare.setListener(new ShareListener() {
            
            @Override
            public void onLoign(boolean sucess) {
                Log.d(TAG, "onLogin:" + sucess);
                Toast.makeText(MainActivity.this, "Login result:" + sucess, Toast.LENGTH_SHORT).show();
                mBtnPost.setText("分享");
            }
            
            @Override
            public void onShare(final boolean sucess) {
                MainActivity.this.runOnUiThread(new Runnable() {
                    
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this, "分享状态:" + sucess, Toast.LENGTH_SHORT).show();
                        Util.dismissDialog();
                    }
                });
               
            }
        });
        
        if (!mShare.getTencent().isSessionValid()) {
            mBtnPost.setText("先登录");
        } 
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
