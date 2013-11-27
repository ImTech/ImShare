/**
 * douzifly @2013-11-26
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.share;

/**
 * @author douzifly
 *
 */
public class ShareIDGen {
    public static int sIdCounter = 0;
    public static int sImageIdCounter = 0;
    
    /**
     * 新的分享id
     */
    public static int nextId() {
        return sIdCounter ++;
    }
    
    /**
     * 新的ImageId
     */
    public static int nextImageId() {
        return sImageIdCounter ++;
    }
}
