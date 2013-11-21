/**
 * Author:Xiaoyuan
 * Date: Nov 20, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.sns.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

/**
 * 第三方验证接口
 * @author douzifly
 *
 */
public interface IAuth {

	/*
	 * 开始授权
	 */
	void auth(Context appCtx, Activity activity);
	
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
