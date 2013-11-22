/**
 * Author:Xiaoyuan
 * Date: Nov 22, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.sns.auth;

/**
 * @author Xiaoyuan
 *
 */
public class AccessToken {
	public String accessToken;
	public long expires_in;
	public long expires_when;
	
	public AccessToken(String accessToken, long expires_in) {
		this.accessToken = accessToken;
		this.expires_in = expires_in;
		this.expires_when = System.currentTimeMillis() + expires_in;
	}
}
