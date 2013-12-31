/**
 * douzifly @2013-12-26
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.store;

import com.activeandroid.Model;
import com.activeandroid.query.Select;
import com.imtech.imshare.utils.Log;

import java.util.List;

/**
 * @author douzifly
 *
 */
public class StoreManager {

	final static String TAG = "StoreManager";
	private List<ShareItem> mShares;
	
	private static StoreManager sSharedInstances;
	
	public static StoreManager sharedInstance() {
		if (sSharedInstances == null) {
			sSharedInstances = new StoreManager();
		}
		return sSharedInstances;
	}
	
	
	public List<ShareItem> loadShareItems() {
		if (mShares == null) {
			// not loaded before
			mShares = new Select().from(ShareItem.class).orderBy("id desc").execute();
			Log.d(TAG, "loadShareItmes from db, count:" + mShares.size());
		}
		
		return mShares;
	}
	
	/**
	 * 保存或者更新分享数据
	 * @param item
	 */
	public void saveShareItem(ShareItem item) {
		boolean sucess = item.save();
		if (sucess) {
			if (mShares != null) {
				mShares.add(0, item);
			}
		}
	}
	
	/**
	 * 删除分享数据
	 * @param id
	 */
	public void deleteShareItem(int id) {
		Model.delete(ShareItem.class, id);
	}
	
}
