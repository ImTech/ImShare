/**
 * Author:Xiaoyuan
 * Date: Nov 26, 2013
 * 深圳快播科技
 */
package com.imtech.imshare.core.share;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.share.ShareObject;

/**
 * @author Xiaoyuan
 *
 */
public class ShareQueue implements IShareQueue{

	@Override
	public int add(ShareObject obj, SnsType type) {
		return 0;
	}

	@Override
	public void remove(int id) {
	}

	@Override
	public void setListener(IShareQueueListener l) {
	}

}
