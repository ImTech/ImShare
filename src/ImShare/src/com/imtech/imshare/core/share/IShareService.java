/**
 * Author:Xiaoyuan
 * Date: Nov 26, 2013
 */
package com.imtech.imshare.core.share;

import android.app.Activity;

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
	int addShare(Activity acticity, ShareObject obj, SnsType snsType);
	void addListener(IShareServiceListener listener);
	void removeListener(IShareServiceListener l);
	void cancel(int shareId);
    void setTmpScaledImagePath(String dir);
	/**
	 * 清除任务
	 */
	void clear();
	
	public static interface IShareServiceListener extends IShareListener{
		   /**
	     * 分享已经添加到队列 (不一定开始)
	     */
	    void onShareAdded(ShareObject obj);
	    /**
	     * 分享已经开始
	     */
	    void onShareBegin(ShareObject obj);
	    
	    /**
	     * 所有的分享都完成了
	     */
	    void onShareComplete();
	}
}
