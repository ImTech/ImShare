package com.imtech.imshare.ui.myshare;

import android.app.Activity;
import android.content.Intent;
import android.provider.MediaStore;

/**
 * Created by douzifly on 14-1-2.
 */
public class ChooseTakePic implements IChoose {
    @Override
    public void choose(Activity context, int reqCode) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);//调用android自带的照相机
        context.startActivityForResult(intent, reqCode);
    }
}
