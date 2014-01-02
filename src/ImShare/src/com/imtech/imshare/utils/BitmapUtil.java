/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-3
 */
package com.imtech.imshare.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 *
 */
public class BitmapUtil {
	private static final String TAG = "BitmapUtil";
	
	public static byte[] decodeBitmapToByte(String path) {
		// 把 bitmap 转换为 byteArray , 用于发送请求
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if(bitmap != null){
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, 60, baos);
			byte[] buff = baos.toByteArray();
			bitmap.recycle();
			return buff;
		}
		
		return null;
	}
	
	public static Bitmap decodeFile(String path, int width){
		Log.d(TAG, "width: " + width);
		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opt);
		Log.d(TAG, "bmp width: " + opt.outWidth + " bmp height: " + opt.outHeight);
		int sample = 1;
		if(width > 0){
			sample = (int)((float)opt.outWidth / (float)width);
			Log.d(TAG, "sample: " + sample);
		}
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = sample > 0 ? sample : 1;
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		return BitmapFactory.decodeFile(path, opt);
	}
	
	public static String getImagePathByUri(Context ctx, Uri uri){
		ContentResolver cr = ctx.getContentResolver();
		Cursor cursor = cr.query(uri, null, null, null, null);
		String path = null;
		if (cursor.moveToFirst()) {
			int column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
			path = cursor.getString(column);
		}
		cursor.close();
		return path;
	}


    public static Bitmap createScaledBitmap(Bitmap src, int dstWidth, int dstHeight, Config config) {
        Bitmap bitmap = Bitmap.createBitmap(dstWidth, dstHeight, config);
        Canvas canvas = new Canvas(bitmap);
        Paint p = new Paint();
        p.setAntiAlias(true);
        final Rect rect = new Rect(0, 0, src.getWidth(), src.getHeight());
        final RectF rectF = new RectF(0, 0, dstWidth, dstHeight);
        canvas.drawBitmap(bitmap, rect, rectF, p);
        return bitmap;
    }

    public static void scaleAndSave(Bitmap source, int maxWidth, String savePath) throws IOException {
        final int oldW = source.getWidth();
        final int oldH = source.getHeight();
        Bitmap scaled = null;
        if (maxWidth >= oldW) {
            scaled = source;
            Log.d(TAG, "no need scale, w:" + oldW);
        } else {
            float scale = (float)maxWidth / (float)oldW;
            int h = (int) (oldH * scale);
            Log.d(TAG, "scaleAndSave form w:" + oldW + " h:" + oldH + " to w:" + maxWidth + " h:" + h);
            scaled = createScaledBitmap(source, maxWidth, h, Config.ARGB_8888);
        }
        File f = new File(savePath);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        FileOutputStream fos = new FileOutputStream(savePath);
        scaled.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        fos.flush();
        fos.close();
    }

    public int getImageOrientation(String path) {
         File imageFile = new File(path);
        ExifInterface exif = null;
        try {
            exif = new ExifInterface(
                    imageFile.getAbsolutePath());
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        int orientation = exif.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_NORMAL);
        int rotate = 0;
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_270:
                rotate = 270;
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                rotate = 180;
                break;
            case ExifInterface.ORIENTATION_ROTATE_90:
                rotate = 90;
                break;
        }

        Log.d(TAG, "Exif orientation: " + orientation + " rotate:" + rotate);
        return rotate;
    }
}
