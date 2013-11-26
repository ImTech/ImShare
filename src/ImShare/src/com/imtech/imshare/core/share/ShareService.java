/**
 * douzifly @Nov 25, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.share;

import java.util.LinkedList;

import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.sns.share.ShareRet;

/**
 * @author douzifly
 *
 */
public class ShareService implements IShareService{
	
	private LinkedList<IShareListener> mListeners 
				= new LinkedList<IShareListener>();
	
	private IShareQueue mShareQueue;

	@Override
	public int share(ShareObject obj, SnsType snsType) {
		return 0;
	}
	
	@Override
	public void cancel(int shareId) {
	}

	@Override
	public void addListener(IShareListener listener) {
		mListeners.add(listener);
	}

	@Override
	public void removeListener(IShareListener l) {
		mListeners.remove(l);
	}
	
	class ShareListener implements IShareListener {

		@Override
		public void onShareFinished(ShareRet ret) {
			for (IShareListener l : mListeners) {
				l.onShareFinished(ret);
			}
		}
		
	}

}
