package com.imtech.imshare.ui.myshare;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import com.imtech.imshare.utils.Log;

/**
 * Created by douzifly on 14-1-2.
 */
public class ChooseTakePic implements IChoose {
    final static String TAG = "ChooseTakePic";
    @Override
    public void choose(Activity context, int reqCode, Bundle extra) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机
        Uri savePath = extra.getParcelable(MediaStore.EXTRA_OUTPUT);
        if (savePath != null) {
            Log.d(TAG, "savePath:" + savePath);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, savePath);
        }
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        context.startActivityForResult(intent, reqCode);
    }
}
