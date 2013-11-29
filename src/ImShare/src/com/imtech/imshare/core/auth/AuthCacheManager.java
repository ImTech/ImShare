/**
 * Author:Xiaoyuan
 * Date: Nov 20, 2013
 */
package com.imtech.imshare.core.auth;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.utils.Log;
import com.imtech.imshare.utils.StringUtils;

/**
 * 认证缓存管理
 * @author Xiaoyuan
 *
 */
public class AuthCacheManager {

	final static String TAG = "SNS_AuthCacheManager";
	
	private DefaultStaoreImpl store = new DefaultStaoreImpl();
	
	/**
	 * 存储一个认证缓存, 如果同样的type已经存在，那么覆盖
	 * @param context
	 * @param type
	 * @param uid
	 * @param accessToken
	 * @param expired
	 */
	public void put(Context context, SnsType type, String uid, String accessToken, long expireWhen, long expireIn) {
		Log.d(TAG, "put type:" + type + " uid:" + uid + " accessToken:" + accessToken + " expired:" + expireWhen);
		store.put(context, type, uid, accessToken, expireWhen, expireIn);
	}
	
	/**
	 * 获取一个认证缓存，如果没有或者已经失效，那么返回null
	 * @param context
	 * @param type
	 * @return
	 */
	public AuthCache get(Context context, SnsType type) {
		Log.d(TAG, "get type:" + type);
		AuthCache cache = store.get(context, type);
		if (cache == null || cache.token == null) {
		    Log.i(TAG, "no cache");
		    return null;
		}
		if (System.currentTimeMillis() > cache.token.expires_when) {
			Log.e(TAG, "expired , current:" + System.currentTimeMillis() + " when:" + cache.token.expires_when);
			store.remove(context, type);
			return null;
		}
		return cache;
	}
	
	public void remove(Context context, SnsType type) {
		Log.d(TAG, "remove type:" + type);
		store.remove(context, type);
	}
	
	/**
	 * 表示一个认证缓存
	 * @author Xiaoyuan
	 *
	 */
	public static class AuthCache {
		public AccessToken token;
		
		@Override
		public String toString() {
		    if (token == null) {
		        return "[AuthCache null]";
		    }
		    return "[AuthCache token:" + token + "]";
		}
	}
	
	static class DefaultStaoreImpl {
		
		final static String SP_NAME = "auth_cache";
		final static String KEY_TYPE = "type";
		final static String KEY_UID = "uid";
		final static String KEY_ACCESS_TOKEN = "access_token";
		final static String KEY_EXPIRE_WHEN = "expire_when";
		final static String KEY_EXPIRE_IN = "expire_in";
		
		private SharedPreferences getSp(Context context, SnsType type) {
			String spName = SP_NAME + type;
			Log.d(TAG, "spName:" + spName);
			SharedPreferences sp = context.getSharedPreferences(spName, Context.MODE_PRIVATE);
			return sp;
		}
		
		public void put(Context context, SnsType type, String uid, String accessToken, long expireWhen, long expireIn) {
			Log.d(TAG, "DefaultStaoreImpl put type:" + type + " uid:" + uid + " accessToken:" + accessToken + " expired:" + expireWhen);
			SharedPreferences sp = getSp(context, type);
			Editor editor = sp.edit();
			editor.putString(KEY_UID, uid);
			editor.putString(KEY_ACCESS_TOKEN, accessToken);
			editor.putLong(KEY_EXPIRE_WHEN, expireWhen);
			editor.putLong(KEY_EXPIRE_IN, expireIn);
			editor.commit();
		}
		
		public AuthCache get(Context context, SnsType type) {
			Log.d(TAG, "DefaultStaoreImpl get type:" + type);
			SharedPreferences sp = getSp(context, type);
			String uid = sp.getString(KEY_UID, null);
			String token = sp.getString(KEY_ACCESS_TOKEN, null);
			long expireWhen = sp.getLong(KEY_EXPIRE_WHEN, -1);
			long expireIn = sp.getLong(KEY_EXPIRE_IN, -1);
			if (StringUtils.isEmpty(uid) || StringUtils.isEmpty(token) || expireWhen == -1) {
				Log.e(TAG, "cant find cache");
				return null;
			}
			AuthCache cache = new AuthCache();
			cache.token = new AccessToken(type, uid, token, expireIn, expireWhen);
			return cache;
		}
		
		public void remove(Context context, SnsType type) {
			Log.d(TAG, "DefaultStaoreImpl remove:" + type);
			SharedPreferences sp = getSp(context, type);
			Editor ed = sp.edit();
			ed.clear();
			ed.commit();
		}
	}
}
