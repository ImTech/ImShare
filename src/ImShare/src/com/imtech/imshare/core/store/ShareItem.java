/**
 * douzifly @2013-12-26
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.store;

import com.imtech.imshare.sns.SnsType;

/**
 * @author douzifly
 *
 */
public class ShareItem {
    
    public int id;
    
    /**
     * 分享的图片路径
     */
    public Pic[] picPaths;
    
    /**
     * 分享的内容 
     */
    public String content;
    
    /**
     * 分享到的平台
     */
    public SnsType[] snsTypes;
    
    /**
     * 分享状态 1 成功 2 失败 0 未知
     */
    public int state;
    
    
    public static class Pic {
        /**
         * 缩略图路径
         */
        public String thumbPath;
        /**
         * 真实图片地址
         */
        public String originPath;
    }

}
