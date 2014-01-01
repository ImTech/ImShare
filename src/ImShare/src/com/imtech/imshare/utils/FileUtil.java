package com.imtech.imshare.utils;

/**
 * Created by douzifly on 14-1-1.
 */
public class FileUtil {

    public final static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }
}
