/**
 * douzifly @Nov 21, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.imtech.imshare.sns.SNSSetting;
import com.imtech.imshare.sns.share.ShareRet.ShareRetState;
import com.imtech.imshare.utils.Log;
import com.sina.weibo.sdk.api.TextObject;
import com.sina.weibo.sdk.api.WeiboMessage;
import com.sina.weibo.sdk.api.share.BaseResponse;
import com.sina.weibo.sdk.api.share.IWeiboHandler;
import com.sina.weibo.sdk.api.share.IWeiboShareAPI;
import com.sina.weibo.sdk.api.share.SendMessageToWeiboRequest;
import com.sina.weibo.sdk.api.share.WeiboShareSDK;
import com.sina.weibo.sdk.constant.WBConstants;

/**
 * @author douzifly
 *
 */
public class WeiboShare extends ShareBase implements IWeiboHandler.Response{
 
	final static String TAG = "SNS_WeiboShare";
	
    IWeiboShareAPI mShareApi;
    
    private void init(Activity activity) {
    	if (mShareApi == null) {
    		Log.d(TAG, "init");
    		 // 创建微博分享接口实例
    		mShareApi = WeiboShareSDK.createWeiboAPI(activity, SNSSetting.WEIBO_APPKEY);
    		if (!mShareApi.isWeiboAppInstalled()) {
    			Log.d(TAG, "no weibo app install");
            }
    	}
    }

    @Override
    public void share(Context appCtx, Activity activity, ShareObject obj) {
    	Log.d(TAG, "share");
        init(activity);
        
        boolean checkEnv = mShareApi.checkEnvironment(true);
        if (!checkEnv) {
        	Log.e(TAG, "checkEnv:" + checkEnv);
        	return;
        }
       
        WeiboMessage wbMsg = new WeiboMessage();

        TextObject txt = new TextObject();
        txt.text = obj.text;
        wbMsg.mediaObject = txt;
        
        // 2. 初始化从第三方到微博的消息请求
        SendMessageToWeiboRequest request = new SendMessageToWeiboRequest();
        // 用transaction唯一标识一个请求
        request.transaction = String.valueOf(System.currentTimeMillis());
        request.message = wbMsg;
        
        // 3. 发送请求消息到微博，唤起微博分享界面
        boolean ret = mShareApi.sendRequest(request);
        Log.d(TAG, "sendRequest:" + ret);
    }

	@Override
	public void checkActivityResult(int requestCode, int responseCode, Intent data) {
		if (mShareApi != null) {
			mShareApi.handleWeiboResponse(data, this);
		}
	}

	@Override
	public void onResponse(BaseResponse baseResp) {
		ShareRet ret = new ShareRet();
		switch (baseResp.errCode) {
		case WBConstants.ErrorCode.ERR_OK:
			ret.state = ShareRetState.SUCESS;	
			break;
		case WBConstants.ErrorCode.ERR_CANCEL:
			ret.state = ShareRetState.CANCELED;
			break;
		case WBConstants.ErrorCode.ERR_FAIL:
			ret.state = ShareRetState.FAILED;
			break;
		}
		
		mListener.onShareFinished(ret);
	}

}
