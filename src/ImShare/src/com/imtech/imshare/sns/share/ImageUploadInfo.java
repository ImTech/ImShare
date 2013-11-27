/**
 * douzifly @2013-11-28
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

import com.imtech.imshare.sns.SnsType;

/**
 * @author douzifly
 *
 */
public class ImageUploadInfo {
    
    public enum State {
        Running,
        Pending,
        Failed,
        Sucess
    }
    
    public int imageId;
    public SnsType snsType;
    public int progress;
    public State state;
}
