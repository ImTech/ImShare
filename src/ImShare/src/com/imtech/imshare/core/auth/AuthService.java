package com.imtech.imshare.core.auth;

import java.util.Hashtable;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;

/**
 * 提供各平台的认证服务
 * @author douzifly
 *
 */
public class AuthService {
	

	public Hashtable<SnsType, AccessToken> mTokens
		 = new Hashtable<SnsType, AccessToken>();
	
	
	
	
	

}
