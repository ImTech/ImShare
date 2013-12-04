/**
 * Author:Xiaoyuan
 * Date: Nov 20, 2013
 */
package com.imtech.imshare.sns.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.imtech.imshare.sns.SnsType;

/**
 * 第三方验证接口
 * @author douzifly
 *
 */
public interface IAuth {
	
	SnsType getSnsType();

	/*
	 * 开始授权
	 */
	void auth(Context appCtx, Activity activity);
	
	void setCacheToken(Context ctx, AccessToken token);
	
	AccessToken getAccessToken();
	
	void logout(Context appCtx, Activity activity);

	/**
	 * 设置监听
	 * @param l
	 */
	void setListener(IAuthListener l);
	
	/**
	 * 调用放onActivityResult时调用该函数 
	 */
	void checkActivityResult(int requestCode, int resultCode, Intent data);
}
