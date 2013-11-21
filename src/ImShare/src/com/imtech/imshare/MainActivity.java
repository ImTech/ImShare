package com.imtech.imshare;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.imtech.imshare.sns.auth.QQAuth;

public class MainActivity extends Activity {
    final static String TAG = "Share#MainActivity";
    EditText mEdMessage;
    Button mBtnPost;
    QQAuth mQQAuth;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mEdMessage = (EditText) findViewById(R.id.editMessage);
        mBtnPost = (Button) findViewById(R.id.btnPost);
        
        mBtnPost.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
               
            }
        });
    }
    
    

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

}
