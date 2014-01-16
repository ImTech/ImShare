/**
 * douzifly @2013-12-26
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.store;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.imtech.imshare.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author douzifly
 *
 */
public class StoreManager {

	final static String TAG = "StoreManager";
	private List<ShareItem> mDatas = new ArrayList<ShareItem>();
	
	private static StoreManager sSharedInstances;
	private long mCurrentId = -1;
	private int mPageCount = 8;
	private boolean mIsFirstLoad = true;
	
	public static StoreManager sharedInstance() {
		if (sSharedInstances == null) {
			sSharedInstances = new StoreManager();
		}
		return sSharedInstances;
	}
	
	public List<ShareItem> loadAllShareItems() {
		if (mDatas != null) {
			mDatas.clear();
		}
		mDatas = new Select().from(ShareItem.class).orderBy("id desc").execute();
		Log.d(TAG, "loadShareItmes from db, count:" + mDatas.size());
		return mDatas;
	}
	
	/**
	 * 获取已经加载到内存中的数据
	 */
	public List<ShareItem> getLoadedDatas() {
		return mDatas;
	}
	
	public void resetPageCursor() {
		mCurrentId = -1;
		mIsFirstLoad = true;
	}
	
	public void setPageCount(int count) {
		mPageCount = count;
	}
	
	// 是否还有更多数据
	private boolean haveMoreData() {
		 ShareItem firstItem = new Select().from(ShareItem.class).orderBy("id asc").executeSingle();
		 Log.d(TAG, "haveMoreData lastItem:" + firstItem + " mCurrentId" + mCurrentId);
		 if (firstItem == null) return false;
		 return firstItem.getId() != mCurrentId;
	}
	
	public List<ShareItem> getNextDatas() {
		if (!haveMoreData()) {
			return null;
		}
		
		if (mCurrentId == -1) {
			 ShareItem lastItem = new Select().from(ShareItem.class).orderBy("id desc").executeSingle();
			 Log.d(TAG, "init mCurrentId:" + lastItem.getId());
			 mCurrentId = lastItem.getId();
		}
		// not loaded before
		String where = mIsFirstLoad ? "id <= " : "id < ";
		List<ShareItem> datas = new Select().from(ShareItem.class).where(where + mCurrentId)
				.limit(mPageCount).orderBy("id desc").execute();
		Log.d(TAG, "getNextDatas from db, count:" + mDatas.size());
		if (datas != null && datas.size() > 0) {
			if (mIsFirstLoad) {
				mIsFirstLoad = false;
			}
			mDatas.addAll(datas);
			mCurrentId = datas.get(datas.size() -1).getId();
		}
		Log.d(TAG, "getNextDatas mCurrentId:" + mCurrentId);
		return datas;
	}
	
	/**
	 * 保存或者更新分享数据
	 * @param item
	 */
	public static boolean saveShareItem(ShareItem item) {
		return item.save();
	}
	
	/**
	 * 删除分享数据
	 * @param id
	 */
	public static void deleteShareItem(long id) {
		Model.delete(ShareItem.class, id);
	}
	
}
