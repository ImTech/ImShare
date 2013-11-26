/**
 * douzifly @Nov 21, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.share.ShareRet.ShareRetState;
import com.imtech.imshare.utils.Log;
import com.sina.weibo.sdk.auth.Oauth2AccessToken;
import com.sina.weibo.sdk.exception.WeiboException;
import com.sina.weibo.sdk.net.RequestListener;
import com.sina.weibo.sdk.openapi.legacy.StatusesAPI;

/**
 * @author douzifly
 *
 */
public class WeiboShare extends ShareBase {
 
	final static String TAG = "SNS_WeiboShare";
	
	StatusesAPI mApi; 
	
    private void init(AccessToken token) {
    	if (mApi == null) {
    		Oauth2AccessToken oat = new Oauth2AccessToken(token.accessToken, "" + token.expires_in);
    		mApi = new StatusesAPI(oat);
    	}
    }
    
    @Override
    public SnsType getSnsType() {
    	return SnsType.WEIBO;
    }
    
    @Override
    public void share(Context appCtx, Activity activity, AccessToken token, ShareObject obj) {
    	Log.d(TAG, "share:" + obj);
        init(token);
        mApi.update(obj.text, obj.lat, obj.lng, new ShareListener(obj));
    }

	@Override
	public void checkActivityResult(int requestCode, int responseCode, Intent data) {
	}

	class ShareListener implements RequestListener {
		
		private ShareObject shareObj; 
		public ShareListener(ShareObject obj) {
			shareObj = obj;
		}
		
		@Override
		public void onComplete(String response) {
			Log.d(TAG, "onComplete:" + response);
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.SUCESS, shareObj, getSnsType());
				mListener.onShareFinished(ret);
			}
		}
	
		@Override
		public void onComplete4binary(ByteArrayOutputStream responseOS) {
			Log.d(TAG, "onComplete4binary");
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.SUCESS, shareObj, getSnsType());
				mListener.onShareFinished(ret);
			}
		}
	
		@Override
		public void onIOException(IOException e) {
			Log.e(TAG, "onIOException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj, getSnsType());
				mListener.onShareFinished(ret);
			}
		}
	
		@Override
		public void onError(WeiboException e) {
			Log.e(TAG, "onError:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj, getSnsType());
				mListener.onShareFinished(ret);
			}
		}
	
	}

}
