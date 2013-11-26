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
    
    public static int nextId() {
        return sIdCounter ++;
    }
}
