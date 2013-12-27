/**
 * Author:Xiaoyuan
 * Date: 2013-12-27
 * 深圳快播科技
 */
package com.imtech.imshare.ui.myshare;

import java.util.ArrayList;
import java.util.List;

import com.imtech.imshare.R;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.core.store.StoreManager;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;

/**
 * 我的分享
 * @author Xiaoyuan
 *
 */
public class MyShareActivity extends Activity{

	ListView mListView;
	List<ShareItem> mItems = new ArrayList<ShareItem>();
	ArrayAdapter<ShareItem> mAdapter;
	StoreManager mStroeMgr = StoreManager.sharedInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_share);
		mListView = (ListView) findViewById(R.id.listMyShare); 
		mAdapter = new ArrayAdapter<ShareItem>(this, android.R.layout.simple_list_item_1, mItems);
		mListView.setAdapter(mAdapter);
		loadData();
	}
	
	void loadData() {
		List<ShareItem> data = mStroeMgr.loadShareItems();
		if (data != null) {
			mItems.addAll(data);
		}
		mAdapter.notifyDataSetChanged();
	}
	
}
