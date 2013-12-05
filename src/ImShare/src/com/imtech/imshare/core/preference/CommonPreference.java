/*
 * 文件名: CloudFilePreference.java
 * 版    权：深圳市快播科技有限公司
 * 描    述: 
 * 创建人: 胡启明
 * 创建时间:2012-7-19
 * 
 * 修改人：
 * 修改时间:
 * 修改内容：[修改内容]
 */
package com.imtech.imshare.core.preference;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * @author 胡启明
 * @date 2012-7-19
 */
public class CommonPreference {
	/**
	 * 程序是否是第一次运行 boolean
	 */
	public static final int TYPE_APP_FIRST_LAUNCH = 0x0001;
	/**
	 * 是否显示位置信息
	 */
	public static final int TYPE_LOCATE = 0x0002;

	private static final String KEY_PREFERENCE = "CommonPreference";
	private static final String KEY_APP_FIRST_LAUNCH = "app_first_launch";
	private static final String KEY_LOCATE = "locate";

	private static SharedPreferences preferences;

	public static void beginTransaction(Context context) {
		preferences = context.getSharedPreferences(KEY_PREFERENCE, 0);
	}

	public static void endTrancation() {
		preferences = null;
	}

	private static SharedPreferences getPreference(Context context) {
		SharedPreferences sp = null;
		if (preferences != null) {
			sp = preferences;
		} else {
			sp = context.getSharedPreferences(KEY_PREFERENCE, 0);
		}
		return sp;
	}

	public static boolean setString(Context context, int type, String value) {
		if (context == null) {
			return false;
		}
		String key = getKey(type);
		if (key == null) {
			return false;
		}
		SharedPreferences sp = getPreference(context);
		return sp.edit().putString(key, value).commit();
	}

	public static boolean setInt(Context context, int type, int value) {
		if (context == null) {
			return false;
		}
		String key = getKey(type);
		if (key == null) {
			return false;
		}
		SharedPreferences sp = getPreference(context);
		return sp.edit().putInt(key, value).commit();
	}

	public static boolean setBoolean(Context context, int type, boolean value) {
		if (context == null) {
			return false;
		}
		String key = getKey(type);
		if (key == null) {
			return false;
		}
		SharedPreferences sp = getPreference(context);
		return sp.edit().putBoolean(key, value).commit();
	}

	public static boolean setFloat(Context context, int type, float value) {
		if (context == null) {
			return false;
		}
		String key = getKey(type);
		if (key == null) {
			return false;
		}
		SharedPreferences sp = getPreference(context);
		return sp.edit().putFloat(key, value).commit();
	}

	public static boolean setLong(Context context, int type, long value) {
		if (context == null) {
			return false;
		}
		String key = getKey(type);
		if (key == null) {
			return false;
		}
		SharedPreferences sp = getPreference(context);
		return sp.edit().putLong(key, value).commit();
	}

	public static long getLong(Context context, int type, long defaultValue) {
		if (context == null) {
			return defaultValue;
		}
		String key = getKey(type);
		if (key == null) {
			return defaultValue;
		}
		SharedPreferences sp = getPreference(context);
		return sp.getLong(key, defaultValue);
	}

	public static float getFloat(Context context, int type, float defaultValue) {
		if (context == null) {
			return defaultValue;
		}
		String key = getKey(type);
		if (key == null) {
			return defaultValue;
		}
		SharedPreferences sp = getPreference(context);
		return sp.getFloat(key, defaultValue);
	}

	public static int getInt(Context context, int type, int defaultValue) {
		if (context == null) {
			return defaultValue;
		}
		String key = getKey(type);
		if (key == null) {
			return defaultValue;
		}
		SharedPreferences sp = getPreference(context);
		return sp.getInt(key, defaultValue);
	}

	public static String getString(Context context, int type, String def) {
		if (context == null) {
			return def;
		}
		String key = getKey(type);
		if (key == null) {
			return def;
		}
		SharedPreferences sp = getPreference(context);
		return sp.getString(key, def);
	}

	public static boolean getBoolean(Context context, int type, boolean defaultValue) {
		if (context == null) {
			return false;
		}
		String key = getKey(type);
		if (key == null) {
			return false;
		}
		SharedPreferences sp = getPreference(context);
		return sp.getBoolean(key, defaultValue);
	}

	private static String getKey(int type) {
		switch (type) {
		case TYPE_APP_FIRST_LAUNCH:
			return KEY_APP_FIRST_LAUNCH;
		case TYPE_LOCATE:
			return KEY_LOCATE;
		}
		return null;
	}

}
