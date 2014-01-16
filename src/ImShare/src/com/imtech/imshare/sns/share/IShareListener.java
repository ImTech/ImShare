/**
 * douzifly @Nov 21, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

/**
 * @author douzifly
 *
 */
public interface IShareListener {
    
    void onShareFinished(ShareRet ret);
    void onShareImageUpload(ImageUploadInfo info);
 
}
