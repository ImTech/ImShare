/**
 * douzifly @Nov 14, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.tencent.open.HttpStatusException;
import com.tencent.open.NetworkUnavailableException;
import com.tencent.tauth.Constants;
import com.tencent.tauth.IRequestListener;
import com.tencent.tauth.IUiListener;
import com.tencent.tauth.Tencent;
import com.tencent.tauth.UiError;

/**
 * @author douzifly
 *
 */
public class Share {
    final static String TAG = "Share";
    final static String APP_ID = "100557004";
    final static String APP_KEY = "112ed79c74f3e20fea90c8d8a3b17a14";
    
    Tencent mTencent;
    ShareListener mListener;
    
    public Tencent getTencent() {
        return mTencent;
    }
    
    public void setListener(ShareListener l) {
        mListener = l;
    }
    
    public void init(Context appCtx) {
        mTencent = Tencent.createInstance(APP_ID, appCtx);
        Log.d(TAG, "inti tencent:" + mTencent);
    }
    
    public void login(Activity act) {
        if (mTencent.isSessionValid()) return;
        IUiListener listener = new IUiListener() {
            
            @Override
            public void onError(UiError arg0) {
                Log.d(TAG, "onError");
                if (mListener != null) {
                    mListener.onLoign(false);
                }
            }
            
            @Override
            public void onComplete(JSONObject arg0) {
                Log.d(TAG, "onComplete");
                if (mListener != null) {
                    mListener.onLoign(true);
                }
            }
            
            @Override
            public void onCancel() {
                Log.d(TAG, "onCancel");
                if (mListener != null) {
                    mListener.onLoign(false);
                }
            }
        };
        mTencent.login(act, "all", listener);
    }
    
    public void post(Activity act, String msg) {
//        if (mTencent.ready(act)) {
//            Bundle params = new Bundle();
//            params = new Bundle();
//            params.putString("richtype", "2");// 发布心情时引用的信息的类型。1表示图片；
//                                              // 2表示网页； 3表示视频。
//            params.putString("richval",
//                    ("http://www.qq.com" + "#" + System.currentTimeMillis()));// 发布心情时引用的信息的值。有richtype时必须有richval
//            params.putString("con", msg);// 发布的心情的内容。
//            params.putString("lbs_nm", "广东省深圳市南山区高新科技园腾讯大厦");// 地址文
//            params.putString("lbs_x", "0-360");// 经度。请使用原始数据（纯经纬度，0-360）。
//            params.putString("lbs_y", "0-360");// 纬度。请使用原始数据（纯经纬度，0-360）。
//            params.putString("lbs_id", "360");// 地点ID。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。
//            params.putString("lbs_idnm", "腾讯");// 地点名称。lbs_id与lbs_idnm通常一起使用，来明确标识一个地址。
//
//           mTencent.requestAsync(Constants.GRAPH_ADD_SHARE, params,
//                    Constants.HTTP_POST, new IRequestListener() {
//                        
//                        @Override
//                        public void onUnknowException(Exception arg0, Object arg1) {
//                            Log.d(TAG,"onUnExp");
//                        }
//                        
//                        @Override
//                        public void onSocketTimeoutException(SocketTimeoutException arg0,
//                                Object arg1) {
//                            Log.d(TAG, "socket timeout");
//                        }
//                        
//                        @Override
//                        public void onNetworkUnavailableException(NetworkUnavailableException arg0,
//                                Object arg1) {
//                            Log.d(TAG, "nue");
//                        }
//                        
//                        @Override
//                        public void onMalformedURLException(MalformedURLException arg0, Object arg1) {
//                            Log.d(TAG, "onMalf");
//                        }
//                        
//                        @Override
//                        public void onJSONException(JSONException arg0, Object arg1) {
//                            Log.d(TAG, "onJsone");
//                        }
//                        
//                        @Override
//                        public void onIOException(IOException arg0, Object arg1) {
//                            Log.d(TAG, "onIoe");
//                        }
//                        
//                        @Override
//                        public void onHttpStatusException(HttpStatusException arg0, Object arg1) {
//                            Log.d(TAG, "onHttpse");
//                        }
//                        
//                        @Override
//                        public void onConnectTimeoutException(ConnectTimeoutException arg0,
//                                Object arg1) {
//                            Log.d(TAG, "onCTE");
//                        }
//                        
//                        @Override
//                        public void onComplete(JSONObject arg0, Object arg1) {
//                            Log.d(TAG, "onComplete");
//                            if (mListener != null) {
//                                mListener.onShare(true);
//                            }
//                        }
//                    }, null);
//        }
    }
    
    public static interface ShareListener {
        
        void onLoign(boolean sucess);
        void onShare(boolean sucess);
    }
    
}
