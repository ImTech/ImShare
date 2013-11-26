/**
 * douzifly @Nov 21, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

import com.imtech.imshare.sns.SnsType;

/**
 * @author douzifly
 *
 */
public class ShareRet {
    public enum ShareRetState {
        SUCESS, 
        FAILED,
        CANCELED,
        TOKEN_EXPIRED
    }
    
    public ShareRet(ShareRetState state, ShareObject obj, SnsType type) {
    	this.state = state;
    	this.obj = obj;
    	this.snsType = type;
    }
    
    public ShareObject obj;
    public SnsType snsType;
    public ShareRetState state;
    public String errorCode;
    public String errorMessage;
}
