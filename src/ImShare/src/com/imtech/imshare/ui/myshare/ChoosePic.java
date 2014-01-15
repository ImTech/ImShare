package com.imtech.imshare.ui.myshare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

/**
 * Created by douzifly on 14-1-2.
 */
public class ChoosePic implements IChoose {

    @Override
    public void choose(Activity context, int reqCode, Bundle extra) {
        Intent intent = new Intent(Intent.ACTION_PICK, null);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        intent.putExtra(MediaStore.Images.Media.ORIENTATION, 0);
        try {
            context.startActivityForResult(intent, reqCode);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(context, "无法选择照片,没有图库应用?", Toast.LENGTH_SHORT).show();
        }
    }
}
