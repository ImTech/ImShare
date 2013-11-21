/**
 * douzifly @Nov 21, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

/**
 * Share interface
 * @author douzifly
 *
 */
public interface IShare {
    
    void setListener(IShareListener listener);
    void share(ShareObject obj);
    
}
