package com.imtech.imshare.core.auth;

import java.util.Hashtable;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.util.Log;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.IAuth;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.auth.QQAuth;
import com.imtech.imshare.sns.auth.WeiboAuth;
import com.imtech.imshare.sns.auth.AuthRet.AuthRetState;

/**
 * 提供各平台的认证服务和授权管理
 * @author douzifly
 *
 */
public class AuthService implements IAuthService{
    
    final static String TAG = "SNS_AuthService";
	
	Hashtable<SnsType, AccessToken> mTokens
		 = new Hashtable<SnsType, AccessToken>();
	
	LinkedList<IAuthListener> mAuthListeners 
	    = new LinkedList<IAuthListener>();
	
	AuthCacheManager mCacheManager = new AuthCacheManager();

    @Override
    public AccessToken getAccessToken(SnsType type) {
        return mTokens.get(type);
    }
    
    private IAuth getAuth(SnsType type) {
        if (type == SnsType.WEIBO) {
            return new WeiboAuth();
        } else if (type == SnsType.QQ) {
            return new QQAuth();
        }
        return null;
    }

    @Override
    public void auth(SnsType snsType, Context appCtx, Activity activity) {
        Log.d(TAG, "auth type:" + snsType);
    }

    @Override
    public void addAuthListener(IAuthListener l) {
        mAuthListeners.add(l);
    }

    @Override
    public void removeAuthListener(IAuthListener l) {
        mAuthListeners.remove(l);
    }

    class AuthListener implements IAuthListener {

        @Override
        public void onAuthFinished(SnsType snsType, AuthRet ret) {
            
            Log.d(TAG, "onAuthFinished snsType:" + snsType + " ret:" + ret);
            
            if (ret.state == AuthRetState.SUCESS) {
                mTokens.put(snsType, ret.token);
            }
            
            for (IAuthListener l : mAuthListeners) {
                l.onAuthFinished(snsType, ret);
            }
            
        }
        
    }
}
