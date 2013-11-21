/**
 * Author:Xiaoyuan
 * Date: Nov 20, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.sns.auth;

/**
 * 第三方验证回调接口
 * @author douzifly
 *
 */
public interface IAuthListener {
	void onAuthFinished(AuthRet ret);
}
