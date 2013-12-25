package com.imtech.imshare.core.auth;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.imtech.imshare.core.auth.AuthCacheManager.AuthCache;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.AuthRet.AuthRetState;
import com.imtech.imshare.sns.auth.IAuth;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.auth.QQAuth;
import com.imtech.imshare.sns.auth.WeiboAuth;

/**
 * 提供各平台的认证服务和授权管理
 * @author douzifly
 *
 */
public class AuthService implements IAuthService{
    
    final static String TAG = "SNS_AuthService";
    
    static AuthService sInstance;
	
	Hashtable<SnsType, AccessToken> mTokens
		 = new Hashtable<SnsType, AccessToken>();
	
	LinkedList<IAuthListener> mAuthListeners 
	    = new LinkedList<IAuthListener>();
	
	HashMap<SnsType, IAuth> mAuths 
		= new HashMap<SnsType, IAuth>();
	
	AuthCacheManager mCacheManager = new AuthCacheManager();
	IAuth mCurrentAuth;
	
	Context mAppCtx;
	
	private AuthService() {
		initAuths();
	}
	
	public synchronized static AuthService getInstance() {
	    if (sInstance == null) {
	        sInstance = new AuthService();
	    }
	    return sInstance;
	}
	
	@Override
	public void loadCachedTokens(Context context) {
	    Log.d(TAG, "loadCachedTokens");
	    SnsType[] snsTypes = {SnsType.FACEBOOK, 
	                          SnsType.GOOGLE_PLUS,
	                          SnsType.LOFTER,
	                          SnsType.QQ,
	                          SnsType.TENCENT_WEIBO,
	                          SnsType.RENREN,
	                          SnsType.TWITTER,
	                          SnsType.WEIBO};
	    for (SnsType type : snsTypes) {
	        AuthCache cache = mCacheManager.get(context, type);
	        Log.d(TAG, "loadCache for:" + type + " cache:" + cache);
	        if (cache != null && cache.token != null) {
	            mTokens.put(type, cache.token);
	            mAuths.get(type).setCacheToken(context, cache.token);
	        }
	    }
	}

    @Override
    public AccessToken getAccessToken(SnsType type) {
        return mTokens.get(type);
    }
    
    private void initAuths() {
    	mAuths.put(SnsType.WEIBO, new WeiboAuth());
    	mAuths.put(SnsType.TENCENT_WEIBO, new QQAuth());
    }

    @Override
    public void auth(SnsType snsType, Activity activity) {
        Log.d(TAG, "auth type:" + snsType);
        mAppCtx = activity.getApplicationContext();
        AuthCache cache = mCacheManager.get(mAppCtx, snsType);
        if (cache != null && cache.token != null) {
            Log.d(TAG, "AccessToken cached!");
            AuthRet ret = new AuthRet(AuthRetState.SUCESS);
            ret.token = cache.token;
            mTokens.put(snsType, ret.token);
            notifyAuthFinished(snsType, ret);
            return;
        }
        IAuth auth = mAuths.get(snsType);
        if (auth == null) {
            throw new UnsupportedOperationException("unknow SnsType:" + snsType);
        }
        auth.setListener(new AuthListener());
        mCurrentAuth = auth;
        auth.auth(mAppCtx, activity);
    }

    @Override
    public void addAuthListener(IAuthListener l) {
        mAuthListeners.add(l);
    }

    @Override
    public void removeAuthListener(IAuthListener l) {
        mAuthListeners.remove(l);
    }
    
    private void notifyAuthFinished(SnsType snsType, AuthRet ret) {
        Log.d(TAG, "notifyAuthFinished snsType:" + snsType + " ret:" + ret);
        for (IAuthListener l : mAuthListeners) {
            l.onAuthFinished(snsType, ret);
        }
    }
    
    @Override
    public void checkActivityResult(int requestCode, int resultCode, Intent data) {
        mCurrentAuth.checkActivityResult(requestCode, resultCode, data);
    }

    class AuthListener implements IAuthListener {

        @Override
        public void onAuthFinished(SnsType snsType, AuthRet ret) {
            
            Log.d(TAG, "onAuthFinished snsType:" + snsType + " ret:" + ret);
            
            if (ret.state == AuthRetState.SUCESS) {
                mTokens.put(snsType, ret.token);
                mCacheManager.put(mAppCtx, snsType, ret.token.uid, ret.token.accessToken, 
                        ret.token.expires_when, ret.token.expires_in);
            }
            
            notifyAuthFinished(snsType, ret);
            
            // clear object
            mCurrentAuth.setListener(null);
            mCurrentAuth = null;
        }
    }

    @Override
    public boolean isAuthed(SnsType type) {
        return mTokens.get(type) != null;
    }
    
}
