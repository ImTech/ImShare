/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-3
 */
package com.imtech.imshare.utils;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.BitmapFactory.Options;

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
	
}
