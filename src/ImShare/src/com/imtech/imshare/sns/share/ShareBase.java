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
public abstract class ShareBase implements IShare{

    protected IShareListener mListener;
    
    @Override
    public void setListener(IShareListener listener) {
        mListener = listener;
    }

}
