/**
 * douzifly @Nov 25, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.auth;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.auth.IAuthListener;

/**
 * @author douzifly
 *
 */
public interface IAuthService {

    /**
     * Get Accesstoken for specific SnsType, <br />
     * if AsscessToken was cached, return cached token
     */
    AccessToken getAccessToken(SnsType type);
    
    /**
     * Auth for specific SnsType
     * @throws IllegalStateException when authing
     * @throws UnsupportedOperationException when not support specific snsType
     */
    void auth(SnsType snsType, Context appCtx, Activity activity);
    
    /**
     * add one auth listener
     */
    void addAuthListener(IAuthListener l);
    
    /**
     * remove auth listener which was added before
     */
    void removeAuthListener(IAuthListener l);
    
    void checkActivityResult(int requestCode, int resultCode, Intent data);
}
