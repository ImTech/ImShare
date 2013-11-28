/**
 * douzifly @Nov 21, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;

/**
 * Share interface
 * @author douzifly
 *
 */
public interface IShare {
    
    void setListener(IShareListener listener);
    void share(Context appCtx, Activity activity, AccessToken token, ShareObject obj);
    void checkActivityResult(int requestCode , int responseCode, Intent data);
    SnsType getSnsType();
}
