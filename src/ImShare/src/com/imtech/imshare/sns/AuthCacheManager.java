/**
 * Author:Xiaoyuan
 * Date: Nov 20, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.sns;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import com.qvod.player.core.proxy.Log;
import com.qvod.player.utils.StringUtils;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 认证缓存管理
 * @author Xiaoyuan
 *
 */
public class AuthCacheManager {

	final static String TAG = "3rdAuth_AuthCacheManager";
	
	private DefaultStaoreImpl store = new DefaultStaoreImpl();
	
	/**
	 * 存储一个认证缓存, 如果同样的type已经存在，那么覆盖
	 * @param context
	 * @param type
	 * @param uid
	 * @param accessToken
	 * @param expired
	 */
	public void put(Context context, int type, String uid, String accessToken, long expireWhen) {
		Log.d(TAG, "put type:" + type + " uid:" + uid + " accessToken:" + accessToken + " expired:" + expireWhen);
		store.put(context, type, uid, accessToken, expireWhen);
	}
	
	/**
	 * 获取一个认证缓存，如果没有或者已经失效，那么返回null
	 * @param context
	 * @param type
	 * @return
	 */
	public AuthCache get(Context context, int type) {
		Log.d(TAG, "get type:" + type);
		AuthCache cache = store.get(context, type);
		if (cache == null) return null;
		if (System.currentTimeMillis() > cache.expireWhen) {
			Log.e(TAG, "expired");
			store.remove(context, type);
			return null;
		}
		return cache;
	}
	
	public void remove(Context context, int type) {
		Log.d(TAG, "remove type:" + type);
		store.remove(context, type);
	}
	
	/**
	 * 表示一个认证缓存
	 * @author Xiaoyuan
	 *
	 */
	public static class AuthCache {
		public int type;
		public String accessToken;
		public String uid;
		public long expireWhen;
		
		@Override
		public String toString() {
			return "type:" + type + " accessToken:" + accessToken + " uid:" + uid
					+ " expiredWhen:" + expireWhen;
		}
	}
	
	static class DefaultStaoreImpl {
		
		final static String SP_NAME = "auth_cache";
		final static String KEY_TYPE = "type";
		final static String KEY_UID = "uid";
		final static String KEY_ACCESS_TOKEN = "access_token";
		final static String KEY_EXPIRE_WHEN = "expire_when";
		
		private SharedPreferences getSp(Context context, int type) {
			String spName = SP_NAME + type;
			Log.d(TAG, "spName:" + spName);
			SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
			return sp;
		}
		
		public void put(Context context, int type, String uid, String accessToken, long expireWhen) {
			Log.d(TAG, "DefaultStaoreImpl put type:" + type + " uid:" + uid + " accessToken:" + accessToken + " expired:" + expireWhen);
			SharedPreferences sp = getSp(context, type);
			Editor editor = sp.edit();
			editor.putString(KEY_UID, uid);
			editor.putString(KEY_ACCESS_TOKEN, accessToken);
			editor.putLong(KEY_EXPIRE_WHEN, expireWhen);
			editor.commit();
		}
		
		public AuthCache get(Context context, int type) {
			Log.d(TAG, "DefaultStaoreImpl get type:" + type);
			SharedPreferences sp = getSp(context, type);
			String uid = sp.getString(KEY_UID, null);
			String token = sp.getString(KEY_ACCESS_TOKEN, null);
			long expireWhen = sp.getLong(KEY_EXPIRE_WHEN, -1);
			if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(token) || expireWhen == -1) {
				Log.e(TAG, "cant find cache");
				return null;
			}
			AuthCache cache = new AuthCache();
			cache.type = type;
			cache.uid = uid;
			cache.accessToken = token;
			cache.expireWhen = expireWhen;
			return cache;
		}
		
		public void remove(Context context, int type) {
			Log.d(TAG, "DefaultStaoreImpl remove:" + type);
			SharedPreferences sp = getSp(context, type);
			Editor ed = sp.edit();
			ed.clear();
			ed.commit();
		}
	}
}
