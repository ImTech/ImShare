/**
 * douzifly @Nov 25, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.auth;

import android.app.Activity;
import android.content.Context;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;

/**
 * @author douzifly
 *
 */
public interface IAuthService {

    
    /**
     * Get Accesstoken for specific SnsType
     */
    public AccessToken getAccessToken(SnsType type);
    
    /**
     * Auth for specific SnsType
     */
    void auth(SnsType snsType, Context appCtx, Activity activity);
    
}
