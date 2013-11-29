/**
 * Author:Xiaoyuan
 * Date: Nov 22, 2013
 */
package com.imtech.imshare.sns.auth;

import android.util.Log;

import com.imtech.imshare.sns.SnsType;

/**
 * @author Xiaoyuan
 *
 */
public class AccessToken {
    
    final static String TAG = "SNS_AccessToken";
    
    public String uid;
	public String accessToken;
	public long expires_in;
	public long expires_when;
	public SnsType snsType;
	
	/**
	 * create new AccessToken object, if expires_when > 0 use cached expires_when, otherwise use expires_in to create new one
	 */
	public AccessToken(SnsType type, String uid, String accessToken, long expires_in, long expires_when) {
	    Log.d(TAG, "new token, uid:" + uid + " accessToken:" + accessToken + " expires_in:" + expires_in + " expires_when:" + expires_when);
		this.accessToken = accessToken;
		this.expires_in = expires_in;
		this.uid = uid;
		this.snsType = type;
		if (expires_when < 0) {
		    this.expires_when = System.currentTimeMillis() + expires_in;
		} else {
		    this.expires_when = expires_when;
		}
	}
	
	@Override
	public String toString() {
	    return "[AccessToken type:" + snsType + " token:" + accessToken + "]";
	}
}
