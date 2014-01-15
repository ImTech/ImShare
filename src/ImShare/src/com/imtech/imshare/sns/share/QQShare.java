/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-3
 */
package com.imtech.imshare.sns.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.imtech.imshare.sns.SNSSetting;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.share.ShareObject.Image;
import com.imtech.imshare.sns.share.ShareRet.ShareRetState;
import com.imtech.imshare.utils.BitmapUtil;
import com.imtech.imshare.utils.Log;
import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.Tencent;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

/**
 * QQ分享
 */
public class QQShare extends ShareBase {
	private final static String TAG = "SNS_QQShare";
	private Tencent mTencent;

	private void init(Context context) {
		if (mTencent != null)
			return;
		mTencent = Tencent.createInstance(SNSSetting.QQ_APP_ID, context);
	}

	@Override
	public void share(Context appCtx, Activity activity, AccessToken token, ShareObject obj) {
		if (obj == null) {
			return;
		}
		Log.d(TAG, "share token: " + token.accessToken);

		init(appCtx);
		if (mTencent.ready(activity)) {
			Bundle bundle = new Bundle();
			bundle.putString("format", "json");
			bundle.putString("content", obj.text);
			bundle.putString("longitude", obj.lng);
			bundle.putString("latitude", obj.lat);
			String picPath = obj.images.size() > 0 ? obj.images.get(0).filePath : null;
	        String scaledPath = obj.images.size() > 0 ? obj.images.get(0).scaledPath : null;
	        String sendImgPath = scaledPath;
	        if (sendImgPath == null) {
	            sendImgPath = picPath;
	        }
	        Log.d(TAG, "send pic:" + sendImgPath);
	        if (sendImgPath != null) {
	            bundle.putByteArray("pic", BitmapUtil.decodeBitmapToByte(sendImgPath, 100));
	        }
			mTencent.requestAsync(Constants.GRAPH_ADD_PIC_T, bundle, Constants.HTTP_POST, new ShareListener(
					obj), null);
		} else {
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, obj, getSnsType());
				mListener.onShareFinished(ret);
			}
			Log.d(TAG, "mTencent not ready ");
		}
	}

	@Override
	public void checkActivityResult(int requestCode, int responseCode, Intent data) {
	}

	@Override
	public SnsType getSnsType() {
		return SnsType.TENCENT_WEIBO;
	}

	private void notifyResult(ShareRetState state, ShareObject shareObj) {
		if (mListener != null) {
			ShareRet ret = new ShareRet(state, shareObj, getSnsType());
			mListener.onShareFinished(ret);
		}
	}

	class ShareListener implements IRequestListener {

		private ShareObject shareObj;

		public ShareListener(ShareObject obj) {
			shareObj = obj;
		}

//		100013    access token非法。
//		100014  access token过期。 token过期时间为3个月。如果存储的access token过期，请重新走登录流程，根据使用Authorization_Code获取Access_Token或使用Implicit_Grant方式获取Access_Token获取新的access token值。
//		100015  access token废除。 token被回收，或者被用户删除。请重新走登录流程，根据使用Authorization_Code获取Access_Token或使用Implicit_Grant方式获取Access_Token获取新的access token值。
//		100016  access token验证失败。
		@Override
		public void onComplete(JSONObject response, Object state) {
			Log.d(TAG, "onComplete response:" + response + " state:" + state);
			int ret = -1;
			try {
				ret = response.getInt("ret");
				Log.d(TAG, "ret:" + ret);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			if (ret == 0) {
				notifyResult(ShareRetState.SUCESS, shareObj);
			} else if (ret == 100013 || ret == 100014 || ret == 100015 || ret == 100016
			        || ret == 100030) {
			    notifyResult(ShareRetState.TOKEN_EXPIRED, shareObj);
			}else {
				notifyResult(ShareRetState.FAILED, shareObj);
			}
		}

		@Override
		public void onConnectTimeoutException(ConnectTimeoutException e, Object arg1) {
			Log.e(TAG, "onConnectTimeoutException:" + e.getMessage());
			notifyResult(ShareRetState.FAILED, shareObj);
		}

		@Override
		public void onHttpStatusException(HttpStatusException e, Object arg1) {
			Log.e(TAG, "onHttpStatusException:" + e.getMessage());
			notifyResult(ShareRetState.FAILED, shareObj);
		}

		@Override
		public void onIOException(IOException e, Object arg1) {
			Log.e(TAG, "onIOException:" + e.getMessage());
			notifyResult(ShareRetState.FAILED, shareObj);
		}

		@Override
		public void onJSONException(JSONException e, Object arg1) {
			Log.e(TAG, "onJSONException:" + e.getMessage());
			notifyResult(ShareRetState.FAILED, shareObj);
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object arg1) {
			Log.e(TAG, "onMalformedURLException:" + e.getMessage());
			notifyResult(ShareRetState.FAILED, shareObj);
		}

		@Override
		public void onNetworkUnavailableException(NetworkUnavailableException e, Object arg1) {
			Log.e(TAG, "onNetworkUnavailableException:" + e.getMessage());
			notifyResult(ShareRetState.FAILED, shareObj);
		}

		@Override
		public void onSocketTimeoutException(SocketTimeoutException e, Object arg1) {
			Log.e(TAG, "onSocketTimeoutException:" + e.getMessage());
			notifyResult(ShareRetState.FAILED, shareObj);
		}

		@Override
		public void onUnknowException(Exception e, Object arg1) {
			Log.e(TAG, "onUnknowException:" + e.getMessage());
			notifyResult(ShareRetState.FAILED, shareObj);
		}

	}
}
