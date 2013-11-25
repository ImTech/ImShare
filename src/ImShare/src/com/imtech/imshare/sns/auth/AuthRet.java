/**
 * Author:Xiaoyuan
 * Date: Nov 20, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.sns.auth;

import android.os.Bundle;


/**
 * 调用Auth服务返回的结果 
 * @author douzifly
 *
 */
public class AuthRet{
	
	public enum AuthRetState {
		SUCESS, 
		FAILED,
		CANCELED
	}
	
	// -------------------- KEY defines ----------------------
	
	public final static String KEY_ACCESS_TOKEN = "access_token";
	public final static String KEY_UID = "uid";
	public final static String KEY_ERROR_CODE = "error_code";
	public final static String KEY_ERROR_ERROR_MESSAGE = "error_message";
	public final static String KEY_ERROR_ERROR_DETAIL = "error_detail";
	
	/**
	 * 什么时候过期, 非时间间隔
	 */
	public final static String KEY_EXPIRES_WHEN = "expires_when";
	public final static String KEY_EXPIRES_IN = "expires_in";
	// -------------------------------------------------------
	
	/**
	 * 授权状态
	 */
	public AuthRetState state;

	private Bundle mData = new Bundle();
	
	public AccessToken token;
	
	public AuthRet(AuthRetState state) {
		this.state = state;
	}
	
	public Bundle getBundle() {
		return mData;
	}
	
	@Override
	public String toString() {
	    return "[AuthRet state:" + state + " token:" + token + "]"; 
	}
}
