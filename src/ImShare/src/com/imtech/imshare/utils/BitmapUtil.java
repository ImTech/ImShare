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
import android.net.Uri;
import android.provider.MediaStore;

import java.io.ByteArrayOutputStream;

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
}
