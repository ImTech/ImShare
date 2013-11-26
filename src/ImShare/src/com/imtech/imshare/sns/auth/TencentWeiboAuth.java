/**
 * Author:Xiaoyuan
 * Date: Nov 25, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.sns.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.imtech.imshare.sns.SNSSetting;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AuthRet.AuthRetState;
import com.imtech.imshare.utils.Log;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.tencent.weibo.sdk.android.api.util.Util;
import com.tencent.weibo.sdk.android.component.Authorize;
import com.tencent.weibo.sdk.android.component.sso.AuthHelper;
import com.tencent.weibo.sdk.android.component.sso.OnAuthListener;
import com.tencent.weibo.sdk.android.component.sso.WeiboToken;

/**
 * Tencent Weibo Auth implements
 * @author Xiaoyuan
 *
 */
public class TencentWeiboAuth extends AuthBase{
	
	final static String TAG = "SNS_TencentWeiboAuth";
	 
	
	@Override
	public SnsType getSnsType() {
		return SnsType.TENCENT_WEIBO;
	}

	@Override
	public void auth(Context appCtx, final Activity activity) {
		Log.d(TAG, "auth");
		long appid = Long.valueOf(Util.getConfig().getProperty("APP_KEY"));
		String app_secket = Util.getConfig().getProperty("APP_KEY_SEC");
		AuthHelper.register(activity, appid, app_secket, new OnAuthListener() {

			//如果当前设备没有安装腾讯微博客户端，走这里
			@Override
			public void onWeiBoNotInstalled() {
//				Toast.makeText(activity, "onWeiBoNotInstalled", 1000)
//						.show();
				Intent i = new Intent(activity, Authorize.class);
				activity.startActivityForResult(i, 0);
			}

			//如果当前设备没安装指定版本的微博客户端，走这里
			@Override
			public void onWeiboVersionMisMatch() {
//				Toast.makeText(activity, "onWeiboVersionMisMatch",
//						1000).show();
				Intent i = new Intent(activity,Authorize.class);
				activity.startActivityForResult(i, 0);
			}

			//如果授权失败，走这里
			@Override
			public void onAuthFail(int result, String err) {
				AuthRet ret = new AuthRet(AuthRetState.FAILED);
				ret.getBundle().putString(AuthRet.KEY_ERROR_ERROR_MESSAGE, err);
				mListener.onAuthFinished(getSnsType(), ret);
				Toast.makeText(activity, "failed result : " + result, 1000)
						.show();
			}

			//授权成功，走这里
			//授权成功后，所有的授权信息是存放在WeiboToken对象里面的，可以根据具体的使用场景，将授权信息存放到自己期望的位置，
			//在这里，存放到了applicationactivity中
			@Override
			public void onAuthPassed(String name, WeiboToken token) {
				Toast.makeText(activity, "passed", 1000).show();
				//
//				Util.saveSharePersistent(activity, "ACCESS_TOKEN", token.accessToken);
//				Util.saveSharePersistent(activity, "EXPIRES_IN", String.valueOf(token.expiresIn));
//				Util.saveSharePersistent(activity, "OPEN_ID", token.openID);
////				Util.saveSharePersistent(activity, "OPEN_KEY", token.omasKey);
//				Util.saveSharePersistent(activity, "REFRESH_TOKEN", "");
////				Util.saveSharePersistent(activity, "NAME", name);
////				Util.saveSharePersistent(activity, "NICK", name);
//				Util.saveSharePersistent(activity, "CLIENT_ID", Util.getConfig().getProperty("APP_KEY"));
//				Util.saveSharePersistent(activity, "AUTHORIZETIME",
//						String.valueOf(System.currentTimeMillis() / 1000l));
				
				AuthRet ret = new AuthRet(AuthRetState.SUCESS);
				ret.token = new AccessToken(getSnsType(), token.openID, token.accessToken, token.expiresIn, -1);
				mToken = ret.token;
				mListener.onAuthFinished(getSnsType(), ret);
			}
		});
		
		AuthHelper.auth(activity, "");
	}

	@Override
	public AccessToken getAccessToken() {
		return mToken;
	}

	@Override
	public void logout(Context appCtx, Activity activity) {
	}

	@Override
	public void checkActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "checkActivityResult");
		if (mToken == null) {
			// canceled here
			AuthRet ret = new AuthRet(AuthRetState.CANCELED);
			mListener.onAuthFinished(getSnsType(), ret);
		}
	}

}
