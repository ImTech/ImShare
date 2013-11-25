/**
 * Author:Xiaoyuan
 * Date: Nov 21, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.sns.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.imtech.imshare.sns.SNSSetting;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AuthRet.AuthRetState;
import com.imtech.imshare.utils.Log;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.auth.WeiboAuthListener;
import com.sina.weibo.sdk.auth.sso.SsoHandler;
import com.sina.weibo.sdk.exception.WeiboException;

/**
 * Weibo 授权实现
 * @author Xiaoyuan
 *
 */
public class WeiboAuth extends AuthBase{

	final static String TAG = "SNS_WeiboAuth";
	
	private SsoHandler mSsoHandler;
	private com.sina.weibo.sdk.auth.WeiboAuth mWeibo;
	private String mRedirectUrl = "https://api.weibo.com/oauth2/default.html";
	 /**
     * Scope 是 OAuth2.0 授权机制中 authorize 接口的一个参数。通过 Scope，平台将开放更多的微博
     * 核心功能给开发者，同时也加强用户隐私保护，提升了用户体验，用户在新 OAuth2.0 授权页中有权利
     * 选择赋予应用的功能。
     * 
     * 我们通过新浪微博开放平台-->管理中心-->我的应用-->接口管理处，能看到我们目前已有哪些接口的
     * 使用权限，高级权限需要进行申请。
     * 
     * 目前 Scope 支持传入多个 Scope 权限，用逗号分隔。
     * 
     * 有关哪些 OpenAPI 需要权限申请，请查看：http://open.weibo.com/wiki/%E5%BE%AE%E5%8D%9AAPI
     * 关于 Scope 概念及注意事项，请查看：http://open.weibo.com/wiki/Scope
     */
    public static final String SCOPE = 
            "email,direct_messages_read,direct_messages_write,"
            + "friendships_groups_read,friendships_groups_write,statuses_to_me_read,"
            + "follow_app_official_microblog," + "invitation_write";
    
    private AccessToken mToken;
    
    @Override
    public SnsType getSnsType() {
    	return SnsType.WEIBO;
    }
	
	private void init(Context appCtx, Activity activity) {
		Log.d(TAG, "init");
		if (mWeibo == null) {
			mWeibo = new com.sina.weibo.sdk.auth.WeiboAuth(activity,
					SNSSetting.WEIBO_APPKEY, mRedirectUrl, SCOPE);
			
		}
		if (mSsoHandler == null) {
			mSsoHandler = new SsoHandler(activity, mWeibo);
		}
	}
	
	@Override
	public void auth(Context appCtx, Activity activity) {
		Log.d(TAG, "auth");
		if (mListener == null) {
			throw new IllegalStateException("listener not set");
		}
		init(appCtx, activity);
		mSsoHandler.authorize(new AuthListener());
	}

	@Override
	public void logout(Context appCtx, Activity activity) {
		Log.d(TAG, "logout");
	}

	@Override
	public void checkActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "checkActivityResult");
		if (mSsoHandler != null) {
			mSsoHandler.authorizeCallBack(requestCode, resultCode, data);
		}
	}

	class AuthListener implements WeiboAuthListener {

		@Override
        public void onComplete(Bundle values) {
			Log.d(TAG, "onComplete");
            // 从 Bundle 中解析 Token
			Oauth2AccessToken oat = Oauth2AccessToken.parseAccessToken(values);
            AuthRet ret;
            if (oat.isSessionValid()) {
            	Log.d(TAG, "session valid");
            	ret = new AuthRet(AuthRetState.SUCESS);
            	ret.token = new AccessToken(oat.getToken(), oat.getExpiresTime());
            	mToken = ret.token;
				ret.getBundle().putString(AuthRet.KEY_ACCESS_TOKEN, oat.getToken());
				ret.getBundle().putLong(AuthRet.KEY_EXPIRES_WHEN, ret.token.expires_when);
				ret.getBundle().putString(AuthRet.KEY_UID, oat.getUid());
            } else {
                // 当您注册的应用程序签名不正确时，就会收到 Code，请确保签名正确
                String code = values.getString("code");
                Log.d(TAG, "session invalid code:" + code);
                ret = new AuthRet(AuthRetState.FAILED);
                ret.getBundle().putString(AuthRet.KEY_ERROR_CODE, code);
            }
            mListener.onAuthFinished(ret);
        }

        @Override
        public void onCancel() {
        	Log.d(TAG, "onCancel");
			AuthRet ret = new AuthRet(AuthRetState.CANCELED);
			mListener.onAuthFinished(ret);
        }

        @Override
        public void onWeiboException(WeiboException e) {
        	String errorMsg = e.getMessage();
        	Log.d(TAG, "onWeiboException, errorCode:" + errorMsg);
			AuthRet ret = new AuthRet(AuthRetState.FAILED);
			ret.getBundle().putString(AuthRet.KEY_ERROR_ERROR_MESSAGE, errorMsg);
			mListener.onAuthFinished(ret);
        }
		
	}

	@Override
	public AccessToken getAccessToken() {
		return mToken;
	}
}
