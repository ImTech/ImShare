/**
 * douzifly @Nov 25, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.share;

import java.util.LinkedList;

import android.app.Activity;
import android.content.Context;

import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.core.share.IShareQueue.IShareQueueListener;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.share.IShare;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.QQShare;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.sns.share.ShareRet.ShareRetState;
import com.imtech.imshare.sns.share.WeiboShare;
import com.imtech.imshare.utils.Log;

/**
 * @author douzifly
 *
 */
public class ShareService implements IShareService{
	
	final static String TAG = "SNS_ShareService";
	
	private LinkedList<IShareListener> mListeners 
				= new LinkedList<IShareListener>();
	
	private IShareQueue mShareQueue;
	private Context mAppContext;
	private Activity mActivity;
	
	private static ShareService sSharedInstance;
	
	public synchronized static ShareService sharedInstance() {
	    if (sSharedInstance == null) {
	        sSharedInstance = new ShareService();
	    }
	    return sSharedInstance;
	}
	
	public ShareService() {
		mShareQueue = new ShareQueue();
		mShareQueue.setListener(new QueueListener());
	}
	
	private IShare getShare(SnsType type) {
		 if (type == SnsType.WEIBO) {
			 return new WeiboShare();
		 } else if (type == SnsType.TENCENT_WEIBO) {
			 return new QQShare();
		 }
		 return null;
	}
	
	private AccessToken getToken(SnsType type) {
		return AuthService.getInstance().getAccessToken(type);
	}

	@Override
	public int addShare(Context appCtx, Activity activity, ShareObject obj, SnsType snsType) {
		Log.d(TAG, "share obj:" + obj + " type:" + snsType);
		mAppContext = appCtx;
		mActivity = activity;
		return mShareQueue.add(obj, snsType);
	}
	
	@Override
	public void cancel(int shareId) {
		mShareQueue.remove(shareId);
	}
	
	@Override
	public void clear() {
	    mShareQueue.clear();
	}

	@Override
	public void addListener(IShareListener listener) {
		mListeners.add(listener);
	}

	@Override
	public void removeListener(IShareListener l) {
		mListeners.remove(l);
	}
	
	class QueueListener implements IShareQueueListener {

		@Override
		public void onNextShare(ShareObject obj, SnsType type) {
			Log.d(TAG, "onNextShare:" + obj + " type:" + type);
			IShare share = getShare(type);
			if (share == null) {
				Log.e(TAG, "unknown share type:" + type);
				ShareRet ret = new ShareRet(ShareRetState.FAILED, obj, type);
				notifyShareFinishend(ret);
				mShareQueue.checkNext();
				return;
			}
			AccessToken token = getToken(type);
			if (token == null) {
				Log.e(TAG, "no token:" + type);
				ShareRet ret = new ShareRet(ShareRetState.TOKEN_EXPIRED, obj, type);
				notifyShareFinishend(ret);
				mShareQueue.checkNext();
				return;
			}
			share.setListener(new ShareListener());
			share.share(mAppContext, mActivity, token, obj);
		}
		
	}
	
	private void notifyShareFinishend(ShareRet ret) {
		for (IShareListener l : mListeners) {
			l.onShareFinished(ret);
		}
	}
	
	private void notifyImageUploadChange(ImageUploadInfo info) {
        for (IShareListener l : mListeners) {
            l.onShareImageUpload(info);
        }
    }
	
	class ShareListener implements IShareListener {

		@Override
		public void onShareFinished(ShareRet ret) {
			notifyShareFinishend(ret);
			mShareQueue.checkNext();
		}
		
		@Override
		public void onShareImageUpload(ImageUploadInfo info) {
		    notifyImageUploadChange(info);
		}
	}

}
