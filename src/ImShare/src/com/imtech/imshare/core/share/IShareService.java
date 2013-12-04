/**
 * Author:Xiaoyuan
 * Date: Nov 26, 2013
 */
package com.imtech.imshare.core.share;

import android.app.Activity;
import android.content.Context;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ShareObject;

/**
 * @author Xiaoyuan
 *
 */
public interface IShareService {
	
	/**
	 * 添加一个分享 返回任务id <br />
	 * share ShareObj with SnsType
	 * @param obj
	 * @param snsType
	 * @return id for this share call
	 */
	int addShare(Context appContext, Activity acticity, ShareObject obj, SnsType snsType);
	void addListener(IShareListener listener);
	void removeListener(IShareListener l);
	void cancel(int shareId);
	/**
	 * 清除任务
	 */
	void clear();
}
