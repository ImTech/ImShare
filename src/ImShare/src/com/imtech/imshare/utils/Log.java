/**
 * Author:Xiaoyuan
 * Date: Nov 21, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.utils;


/**
 * 
 * @author Xiaoyuan
 *
 */
public class Log {
	public static void d(String TAG, String msg) {
		android.util.Log.d(TAG, msg);
	}
	
	public static void e(String TAG, String msg) {
		android.util.Log.e(TAG, msg);
	}
	
	public static void i(String TAG, String msg) {
	    android.util.Log.i(TAG, msg);
	}
}
