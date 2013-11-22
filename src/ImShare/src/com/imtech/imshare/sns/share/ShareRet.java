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
public class ShareRet {
    public enum ShareRetState {
        SUCESS, 
        FAILED,
        CANCELED
    }
    
    public ShareRet(ShareRetState state) {
    	this.state = state;
    }
    
    public int id;
    public ShareRetState state;
    public String errorCode;
    public String errorMessage;
}
