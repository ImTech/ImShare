/**
 * Author:Xiaoyuan
 * Date: Nov 20, 2013
 */
package com.imtech.imshare.sns.auth;

import com.imtech.imshare.sns.SnsType;

/**
 * 第三方验证回调接口
 * @author douzifly
 *
 */
public interface IAuthListener {
	void onAuthFinished(SnsType snsType, AuthRet ret);
}
