/**
 * Author:Xiaoyuan
 * Date: 2013-12-27
 * 深圳快播科技
 */
package com.imtech.imshare;

import java.util.HashMap;

import android.graphics.Bitmap;

import com.activeandroid.app.Application;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.utils.Log;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiscCache;
import com.nostra13.universalimageloader.cache.memory.impl.FIFOLimitedMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.assist.LoadedFrom;
import com.nostra13.universalimageloader.core.display.BitmapDisplayer;
import com.nostra13.universalimageloader.core.imageaware.ImageAware;
import com.nostra13.universalimageloader.utils.StorageUtils;

/**
 * @author Xiaoyuan
 *
 */
public class ImShareApp extends Application{

    final static String TAG = "ImShareApp";
    
    @Override
    public void onCreate() {
        super.onCreate();
        // Create global configuration and initialize ImageLoader with this configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .defaultDisplayImageOptions((new DisplayImageOptions.Builder())
                                                    .bitmapConfig(Bitmap.Config.RGB_565)
                                                    .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                                                    .cacheOnDisc(true)
                                                    .cacheInMemory(true)
                                                    .considerExifParams(true)
                                                    .build())
                .threadPoolSize(5)
                .denyCacheImageMultipleSizesInMemory()
                .discCache(new UnlimitedDiscCache(StorageUtils.getCacheDirectory(this)))
                .memoryCache(new FIFOLimitedMemoryCache(1024))
                .build();
        ImageLoader.getInstance().init(config);
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
