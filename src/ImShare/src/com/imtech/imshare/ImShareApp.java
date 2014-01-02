/**
 * Author:Xiaoyuan
 * Date: 2013-12-27
 * 深圳快播科技
 */
package com.imtech.imshare;

import java.util.HashMap;

import com.activeandroid.app.Application;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.utils.Log;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

/**
 * @author Xiaoyuan
 *
 */
public class ImShareApp extends Application{

    final static String TAG = "ImShareApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        ImageLoaderConfiguration configuration = ImageLoaderConfiguration.createDefault(this);
        ImageLoader.getInstance().init(configuration);
    }
    
    HashMap<SnsType, Boolean> mChecked = new HashMap<SnsType, Boolean>();
    public void setChecked(SnsType type, boolean value) {
        Log.d(TAG, "setChecked:" + type + " value:" + value);
        mChecked.put(type, value);
    }
    
    public boolean isChecked(SnsType type) {
        boolean checked =  mChecked.get(type) != null && mChecked.get(type);
        Log.d(TAG, "isChecked:" + type + " " + checked);
        return checked;
    }
    
}
