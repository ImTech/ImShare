/**
 * Author:Xiaoyuan
 * Date: Nov 25, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.sns.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.imtech.imshare.sns.SnsType;

/**
 * Tencent Weibo Auth implements
 * @author Xiaoyuan
 *
 */
public class TencentWeiboAuth extends AuthBase{
	
	@Override
	public SnsType getSnsType() {
		return null;
	}

	@Override
	public void auth(Context appCtx, Activity activity) {
	}

	@Override
	public AccessToken getAccessToken() {
		return null;
	}

	@Override
	public void logout(Context appCtx, Activity activity) {
	}

	@Override
	public void checkActivityResult(int requestCode, int resultCode, Intent data) {
	}

}
