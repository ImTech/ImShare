/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-3
 */
package com.imtech.imshare.sns.share;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

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
	public void share(Context appCtx, Activity activity, AccessToken token,
			ShareObject obj) {
		if (obj == null) {
			return;
		}
		Log.d(TAG, "share token: " + token.accessToken);
		
		init(appCtx);
		mTencent.setAccessToken(token.accessToken, SNSSetting.QQ_APP_ID);
		if (mTencent.ready(activity)) {
			Bundle bundle = new Bundle();
			bundle.putString("format", "json");
			bundle.putString("content", obj.text);
			String path = null;
			Image image = obj.images != null ? obj.images.get(0) : null;
			if (image != null) {
				path = image.filePath;
			}
			Log.d(TAG, "imag path: " + path);
			bundle.putByteArray("pic", BitmapUtil.decodeBitmapToByte(path));
			mTencent.requestAsync(Constants.GRAPH_ADD_PIC_T, bundle,
					Constants.HTTP_POST, new ShareListener(obj), null);
		}else{
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, obj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
			Log.d(TAG, "mTencent not ready ");
		}
	}

	@Override
	public void checkActivityResult(int requestCode, int responseCode,
			Intent data) {
	}

	@Override
	public SnsType getSnsType() {
		return SnsType.TENCENT_WEIBO;
	}

	class ShareListener implements IRequestListener {

		private ShareObject shareObj;

		public ShareListener(ShareObject obj) {
			shareObj = obj;
		}

		@Override
		public void onComplete(JSONObject response, Object state) {
			Log.d(TAG, "onComplete response:" + response + " state:" + state);
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.SUCESS, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

		@Override
		public void onConnectTimeoutException(ConnectTimeoutException e,
				Object arg1) {
			Log.e(TAG, "onConnectTimeoutException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

		@Override
		public void onHttpStatusException(HttpStatusException e, Object arg1) {
			Log.e(TAG, "onHttpStatusException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

		@Override
		public void onIOException(IOException e, Object arg1) {
			Log.e(TAG, "onIOException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

		@Override
		public void onJSONException(JSONException e, Object arg1) {
			Log.e(TAG, "onJSONException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

		@Override
		public void onMalformedURLException(MalformedURLException e, Object arg1) {
			Log.e(TAG, "onMalformedURLException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

		@Override
		public void onNetworkUnavailableException(
				NetworkUnavailableException e, Object arg1) {
			Log.e(TAG, "onNetworkUnavailableException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

		@Override
		public void onSocketTimeoutException(SocketTimeoutException e,
				Object arg1) {
			Log.e(TAG, "onSocketTimeoutException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

		@Override
		public void onUnknowException(Exception e, Object arg1) {
			Log.e(TAG, "onUnknowException:" + e.getMessage());
			if (mListener != null) {
				ShareRet ret = new ShareRet(ShareRetState.FAILED, shareObj,
						getSnsType());
				mListener.onShareFinished(ret);
			}
		}

	}
}
