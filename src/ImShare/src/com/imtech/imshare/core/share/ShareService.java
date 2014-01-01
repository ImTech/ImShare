/**
 * douzifly @Nov 25, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.share;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

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
import com.imtech.imshare.utils.BitmapUtil;
import com.imtech.imshare.utils.FileUtil;
import com.imtech.imshare.utils.Log;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.sina.weibo.sdk.utils.MD5;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

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

    private String mTmpScaleImageDir;

    private ExecutorService mExecutorService;

    public void setTmpScaledImagePath (String dir) {
        mTmpScaleImageDir = dir;
    }
	
	public synchronized static ShareService sharedInstance() {
	    if (sSharedInstance == null) {
	        sSharedInstance = new ShareService();
	    }
	    return sSharedInstance;
	}
	
	public ShareService() {
		mShareQueue = new ShareQueue();
		mShareQueue.setListener(new QueueListener());
        mExecutorService = Executors.newFixedThreadPool(2);
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

    /**
     * 检查是否需要压缩图片
     * @param filePath 原始图片路径
     * @param scaleWidth 压缩后的大小
     * @return 如果压缩了返回true， 否则返回false
     */
    public boolean checkCompressImage(String filePath, int scaleWidth, String savePath) throws IOException {
        Log.d(TAG, "checkCompressImage path:" + filePath);
        Bitmap bmp = BitmapFactory.decodeFile(filePath);
        if (bmp.getWidth() <= scaleWidth) {
            Log.d(TAG, "checkCompressImage size:" + bmp.getWidth() + " scaleWidth:" + scaleWidth + " no need scale");
            return false;
        }
        BitmapUtil.scaleAndSave(bmp, scaleWidth, savePath);
        return true;
    }

    public void checkCompressImage(ShareObject obj) {
        Log.d(TAG, "checkCompressImage");
        if (obj.images == null || obj.images.size() == 0) {
            Log.d(TAG, "checkCompressImage, no image");
            return;
        }
        for (ShareObject.Image image : obj.images) {
            if (image.filePath != null)  {
                String fileName = null;
                try {
                    fileName = FileUtil.getFileName(image.filePath);
                } catch(Exception e) {
                    fileName = "scaled.png";
                }
                String savePath = mTmpScaleImageDir+ fileName;
                Log.d(TAG, "savePath:" + savePath);
                File f = new File(savePath);
                if (f.exists()) { // test code
                    f.delete();
                } else if (f.exists() && f.length() > 0) {
                    Log.d(TAG, "sacled image exists:" + savePath);
                    continue;
                }

                try {
                   boolean compressed = checkCompressImage(image.filePath, obj.maxPicWidth, savePath);
                    Log.d(TAG, "compressed:" + compressed + " savePath:" + savePath);
                    if (compressed) {
                        image.scaledPath = savePath;
                    }
                } catch (IOException e) {
                    Log.e(TAG, "exp:" + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }

	@Override
	public int addShare(Activity activity, ShareObject obj, SnsType snsType) {
		Log.d(TAG, "share obj:" + obj + " type:" + snsType);
		mAppContext = activity.getApplicationContext();
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
		public void onNextShare(final ShareObject obj, final SnsType type) {
			Log.d(TAG, "onNextShare:" + obj + " type:" + type);
	        final IShare share = getShare(type);
			if (share == null) {
				Log.e(TAG, "unknown share type:" + type);
				ShareRet ret = new ShareRet(ShareRetState.FAILED, obj, type);
				notifyShareFinishend(ret);
				mShareQueue.checkNext();
				return;
			}
			final AccessToken token = getToken(type);
			if (token == null) {
				Log.e(TAG, "no token:" + type);
				ShareRet ret = new ShareRet(ShareRetState.TOKEN_EXPIRED, obj, type);
				notifyShareFinishend(ret);
				mShareQueue.checkNext();
				return;
			}
			share.setListener(new ShareListener());

            // check compress
            mExecutorService.submit(new Runnable() {
                @Override
                public void run() {
                    checkCompressImage(obj);
                    Log.d(TAG, "begin share");
			        share.share(mAppContext, mActivity, token, obj);
                }
            });
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
