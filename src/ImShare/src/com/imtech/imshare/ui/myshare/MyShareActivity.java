/**
 * Author:Xiaoyuan
 * Date: 2013-12-27
 * 深圳快播科技
 */
package com.imtech.imshare.ui.myshare;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

import com.imtech.imshare.R;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.core.store.StoreManager;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.ShareRet;

/**
 * 我的分享
 * @author Xiaoyuan
 *
 */
public class MyShareActivity extends Activity implements IShareListener{

	ListView mListView;
	List<ShareItem> mItems = new ArrayList<ShareItem>();
	ShareAdapter mAdapter;
	StoreManager mStroeMgr = StoreManager.sharedInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_share);
		mListView = (ListView) findViewById(R.id.listMyShare); 
		mAdapter = new ShareAdapter(this);
		mListView.setAdapter(mAdapter);
		loadData();
		ShareService.sharedInstance().addListener(this);
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    ShareService.sharedInstance().removeListener(this);
	}
	
	void loadData() {
		List<ShareItem> data = mStroeMgr.loadShareItems();
		if (data != null) {
			mItems.addAll(data);
		}
		mAdapter.setItems(mItems);
	}

    @Override
    public void onShareFinished(final ShareRet ret) {
        runOnUiThread(new Runnable() {
            
            @Override
            public void run() {
                checkShareResult(ret);
            }
        });
    }

    @Override
    public void onShareImageUpload(ImageUploadInfo info) {
    }
    
    void checkShareResult(ShareRet ret) {
    }
	
}
