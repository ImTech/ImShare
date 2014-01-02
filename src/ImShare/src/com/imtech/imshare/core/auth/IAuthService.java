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
     * 加载缓存的token
     */
    void loadCachedTokens(Context context);

    /**
     * 获取Token，如果有缓存，返回缓存 <br />
     * Get Accesstoken for specific SnsType, <br />
     * if AsscessToken was cached, return cached token
     */
    AccessToken getAccessToken(SnsType type);
    
    /**
     * 认证指定的 SnsType <br />
     * Auth for specific SnsType
     * @throws IllegalStateException when authing
     * @throws UnsupportedOperationException when not support specific snsType
     */
    void auth(SnsType snsType, Activity activity);
    
    /**
     * add one auth listener
     */
    void addAuthListener(IAuthListener l);
    
    /**
     * remove auth listener which was added before
     */
    void removeAuthListener(IAuthListener l);
    
    /**
     * this method must be called, for get auth result
     */
    void checkActivityResult(int requestCode, int resultCode, Intent data);
    
    boolean isAuthed(SnsType type);
    
    void setAuthExpired(Activity context, SnsType type);
}
