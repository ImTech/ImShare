package com.imtech.imshare.core.setting;

import android.os.Environment;

/**
 * Created by douzifly on 14-1-1.
 */
public class AppSetting {

    static String scaledImageDir;
    static String takePicDir;
    static String appDir;

    public static String getAppDir() {
        return Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImTech/ImShare";
    }


    public static String getScaledImageDir() {
        if (scaledImageDir == null) {
            scaledImageDir = getAppDir() + "/.scaled_img/";
        }
        return scaledImageDir;
    }

    public static String getTakePicDir() {
        if (takePicDir == null) {
            takePicDir = getAppDir() + "/photo/";
        }
        return takePicDir;
    }


}
