/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-3
 */
package com.imtech.imshare.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.provider.MediaStore;

/**
 *
 */
public class BitmapUtil {
	
	private static final String TAG = "BitmapUtil";
	
	/**
	 * 把 bitmap 转换为 byteArray
	 */
	public static byte[] decodeBitmapToByte(String path, int quality) {
		Bitmap bitmap = BitmapFactory.decodeFile(path);
		if (bitmap != null) {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(Bitmap.CompressFormat.JPEG, quality, baos);
			byte[] buff = baos.toByteArray();
			bitmap.recycle();
			return buff;
		}

		return null;
	}

	public static Bitmap decodeFile(String path, int width) {
		Log.d(TAG, "width: " + width);
		Options opt = new Options();
		opt.inJustDecodeBounds = true;
		BitmapFactory.decodeFile(path, opt);
		Log.d(TAG, "bmp width: " + opt.outWidth + " bmp height: "
				+ opt.outHeight);
		int sample = 1;
		if (width > 0) {
			sample = (int) ((float) opt.outWidth / (float) width);
			Log.d(TAG, "sample: " + sample);
		}
		opt.inJustDecodeBounds = false;
		opt.inSampleSize = sample > 0 ? sample : 1;
		opt.inPreferredConfig = Config.RGB_565;
		opt.inPurgeable = true;
		return BitmapFactory.decodeFile(path, opt);
	}

	public static int getRotate(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(
					ExifInterface.TAG_ORIENTATION,
					ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}
	
	public static Bitmap rotateBitmap(Bitmap bitmap, int rotate){
		if(bitmap == null)
			return null ;
		
		int w = bitmap.getWidth();
		int h = bitmap.getHeight();

		// Setting post rotate to 90
		Matrix mtx = new Matrix();
		mtx.postRotate(rotate);
		return Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, true);
	}

	public static String getImagePathByUri(Context ctx, Uri uri) {
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
	
	public static boolean checkRotateAndSave(String originPath, String savePath) {
		int degree = getRotate(originPath);
		if (degree != 0) {
				Bitmap rotated = rotateBitmap(decodeFile(originPath, -1), degree);
				return saveBmpToJpg(rotated, savePath, 100);
		}
		return false;
	}
	
	/**
	 * 保存图片到指定位置
	 * 
	 * @param context
	 * @param bmp
	 * @param fileName
	 * @return
	 */
	public static boolean saveBmpToJpg(Bitmap bmp, String filePath, int quality) {
		if (filePath == null || "".equals(filePath)) {
			return false;
		}

		int index = filePath.lastIndexOf("/");
		if (index == -1) {
			return false;
		}

		String prePath = filePath.substring(0, index);

		try {
			File dir = new File(prePath);
			if (!dir.exists()) {
				dir.mkdirs();
			}

			File file = new File(filePath);
			if (file.exists()) {
				file.delete();
			}
			file.createNewFile();

			FileOutputStream fileOut = new FileOutputStream(filePath, true);
			bmp.compress(Bitmap.CompressFormat.JPEG, quality, fileOut);
			fileOut.flush();
			fileOut.close();

			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return false;
	}
	
	public static boolean scaleAndSave(String originPath, Options opt, int maxWidth, String savePath, boolean rotate)
			throws IOException {
		Log.d(TAG, "scaleAndSave rotate:" + rotate + " originPath:" + originPath 
				+ " savePath:" + savePath + " maxWidth:" + maxWidth);
		if (opt == null) {
			 opt = new Options();
			 opt.inJustDecodeBounds = true;
		     BitmapFactory.decodeFile(originPath, opt);
		}
		final int oldW = opt.outWidth;
//		final int oldH = opt.outHeight;
		Bitmap scaled = null;
		if (oldW > maxWidth) {
//			float scale = (float) maxWidth / (float) oldW;
//			int h = (int) (oldH * scale);
//			Log.d(TAG, "scaleAndSave form w:" + oldW + " h:" + oldH + " to w:"
//					+ maxWidth + " h:" + h);
			Options opts = new Options();
			opts.inJustDecodeBounds = false;
			opts.inSampleSize = (int)((float) oldW / (float) maxWidth);
			scaled = BitmapFactory.decodeFile(originPath, opts);
			int degree = getRotate(originPath);
			if (rotate) {
				Log.d(TAG, "degree:" + degree);
				if (degree != 0) {
					scaled = rotateBitmap(scaled, degree);
				}
			}
		} else {
			return false;
		}
		File f = new File(savePath);
		if (!f.getParentFile().exists()) {
			f.getParentFile().mkdirs();
		}
		FileOutputStream fos = new FileOutputStream(savePath);
		scaled.compress(Bitmap.CompressFormat.JPEG, 100, fos);
		fos.flush();
		fos.close();
		return true;
	}

//	public static void scaleAndSave(Bitmap source, int maxWidth, String savePath, boolean rotate, int degree)
//			throws IOException {
//		Log.d(TAG, "scaleAndSave rotate:" + rotate + " savePath:" + savePath + " maxWidth:" + maxWidth);
//		final int oldW = source.getWidth();
//		final int oldH = source.getHeight();
//		Bitmap scaled = null;
//		if (maxWidth >= oldW) {
//			scaled = source;
//			Log.d(TAG, "no need scale, w:" + oldW);
//		} else {
//			float scale = (float) maxWidth / (float) oldW;
//			int h = (int) (oldH * scale);
//			Log.d(TAG, "scaleAndSave form w:" + oldW + " h:" + oldH + " to w:"
//					+ maxWidth + " h:" + h);
//			scaled = Bitmap.createScaledBitmap(source, maxWidth, h, false);
//		}
//		File f = new File(savePath);
//		if (!f.getParentFile().exists()) {
//			f.getParentFile().mkdirs();
//		}
//		FileOutputStream fos = new FileOutputStream(savePath);
//		scaled.compress(Bitmap.CompressFormat.JPEG, 100, fos);
//		fos.flush();
//		fos.close();
//		if (rotate) {
//			Log.d(TAG, "degree:" + degree);
//			if (degree != 0) {
//				Bitmap rotated = rotateBitmap(source, degree);
//				scaleAndSave(rotated, maxWidth, savePath, false, 0);
//			}
//		}
//	}

	public int getImageOrientation(String path) {
		File imageFile = new File(path);
		ExifInterface exif = null;
		try {
			exif = new ExifInterface(imageFile.getAbsolutePath());
		} catch (IOException e) {
			e.printStackTrace();
			return 0;
		}
		int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
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
