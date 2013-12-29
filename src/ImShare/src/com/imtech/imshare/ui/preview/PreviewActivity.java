/**
 * douzifly @2013-12-29
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.ui.preview;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.imtech.imshare.R;

/**
 * @author douzifly
 *
 */
public class PreviewActivity extends FragmentActivity{
    
    final static String EXTRA_IMG_PATH = "image_path";
    final static String EXTRA_SHOW_DELETE = "show_delete";
    PreviewFragment mFragmnet;
    
  
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_preview);
        mFragmnet = (PreviewFragment) getSupportFragmentManager().findFragmentById(R.id.previewFragment);
        handleIntent(getIntent());
    }
    
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }
    
    void handleIntent(Intent i) {
        if (i == null) return;
        String imagePath = i.getStringExtra(EXTRA_IMG_PATH);
        if (imagePath == null) return;
        mFragmnet.setImagePath(imagePath);
        
        boolean showDelete = i.getBooleanExtra(EXTRA_SHOW_DELETE, true);
        mFragmnet.setShowDelete(showDelete);
    }
    
    public final static void showImage(Context context, String path, boolean showDelete) {
        Intent intent = new Intent(context, PreviewActivity.class);
        intent.putExtra(EXTRA_IMG_PATH, path);
        intent.putExtra(EXTRA_SHOW_DELETE, showDelete);
        context.startActivity(intent);
    }
}
