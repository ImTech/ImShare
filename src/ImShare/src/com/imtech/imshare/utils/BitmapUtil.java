/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-3
 */
package com.imtech.imshare.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.net.Uri;
import android.provider.MediaStore;

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
	
	public static Bitmap decodeStream(InputStream is){
		Options opt = new Options();
		opt.inSampleSize = 16;
		return BitmapFactory.decodeStream(is, null, opt);
	}
	
	public static Bitmap decodeFile(String path, int sample){
		Options opt = new Options();
		opt.inSampleSize = sample;
		return BitmapFactory.decodeFile(path);
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
