/**
 * Author:Xiaoyuan
 * Date: Nov 26, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.core.share;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ShareObject;

/**
 * @author Xiaoyuan
 *
 */
public interface IShareService {
	
	/**
	 * share ShareObj with SnsType
	 * @param obj
	 * @param snsType
	 * @return id for this share call
	 */
	int share(ShareObject obj, SnsType snsType);
	void addListener(IShareListener listener);
	void removeListener(IShareListener l);
	void cancel(int shareId);
}
