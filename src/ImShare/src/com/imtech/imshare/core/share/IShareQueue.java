/**
 * Author:Xiaoyuan
 * Date: Nov 26, 2013
 */
package com.imtech.imshare.core.share;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.share.ShareObject;

/**
 * @author Xiaoyuan
 *
 */
public interface IShareQueue {
    
	/**
	 * 
	 * @param obj
	 * @param type
	 * @return id 
	 */
	int add(ShareObject obj, SnsType type);
	void remove(int id);
	void clear();
	/**
	 * 检查下一个任务
	 * return true 还有任务 false 没有任务
	 */
	boolean checkNext();
	
	void setListener(IShareQueueListener l);
	
	public static interface IShareQueueListener {
		public void onNextShare(ShareObject obj, SnsType type);
	}
	
}
