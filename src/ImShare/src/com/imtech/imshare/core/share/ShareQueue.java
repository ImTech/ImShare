/**
 * Author:Xiaoyuan
 * Date: Nov 26, 2013
 */
package com.imtech.imshare.core.share;

import java.util.ArrayList;
import java.util.List;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.utils.Log;

/** 
 * 分享队列
 * @author Xiaoyuan
 *
 */
public class ShareQueue implements IShareQueue{

    final static String TAG = "SNS_ShareQueue";
    List<TaskInfo> mPendingTasks = new ArrayList<ShareQueue.TaskInfo>();
    TaskInfo mRunningTask;
    IShareQueueListener mListener;
    
	@Override
	public int add(ShareObject obj, SnsType type) {
	    Log.d(TAG, "add obj:" + obj + " type:" + type);
	    int id = ShareIDGen.nextId();
	    Log.d(TAG, "newid:" + id);
	    TaskInfo t = new TaskInfo(id, obj, type);
	    if (mRunningTask == null) {
	        Log.d(TAG, "no running task, run this");
	        mRunningTask = t;
	        notifyTaskAvaiable(mRunningTask);
	        return id;
	    } 
	    Log.d(TAG, "have running task, add to running queue");
	    mPendingTasks.add(t);
		return id;
	}
	
	void notifyTaskAvaiable(TaskInfo task) {
	    if (mListener != null) {
	        mListener.onNextShare(task.obj, task.type);
	    }
	}
	
	@Override
	public void checkNext() {
	    Log.d(TAG, "checkNext");
	    mRunningTask = null;
	    if (mPendingTasks.size() > 0) {
	        mRunningTask = mPendingTasks.remove(0);
	        Log.d(TAG, "have pending task, set running:" + mRunningTask 
	                + " left count:" + mPendingTasks.size());
	        notifyTaskAvaiable(mRunningTask);
	    }
	}

	@Override
	public void remove(int id) {
	    if (mRunningTask != null && mRunningTask.id == id) {
	        mRunningTask = null;
	    }
	    checkNext();
	}

	@Override
	public void setListener(IShareQueueListener l) {
	    mListener = l;
	}
	
	@Override
	public void clear() {
	    mRunningTask = null;
	    mPendingTasks.clear();
	};

	public static class TaskInfo {
	    public TaskInfo(int id, ShareObject o, SnsType type) {
	        this.id = id;
	        this.obj = o;
	        this.type = type;
	    }
	    public ShareObject obj;
	    public SnsType type;
	    public int id;
	}
}
