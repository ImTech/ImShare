/**
 * douzifly @2013-12-30
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.ui.preview;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

/**
 * @author douzifly
 *
 */
public class TextPreviewActivity extends Activity{
    
    public final static String EXTRA_TEXT = "text";
    
    TextView mTextView;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mTextView = new TextView(this);
        mTextView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        mTextView.setGravity(Gravity.CENTER);
        mTextView.setTextSize(20);
        mTextView.setBackgroundColor(Color.WHITE);
        mTextView.setTextColor(Color.BLACK);
        mTextView.setPadding(10, 10, 10, 10);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            mTextView.setTextIsSelectable(true);
        }
        setContentView(mTextView);
        handleIntent(getIntent());
        mTextView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    
    void handleIntent(Intent intent) {
        String text = intent.getStringExtra(EXTRA_TEXT);
        if (text == null) text = "";
        mTextView.setText(text);
    }
    
    public static void showText(Context context, String text) {
        Intent i = new Intent(context, TextPreviewActivity.class);
        i.putExtra(EXTRA_TEXT, text);
        context.startActivity(i);
    }


}
