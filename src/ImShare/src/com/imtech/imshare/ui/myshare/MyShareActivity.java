/**
 * Author:Xiaoyuan
 * Date: 2013-12-27
 * 深圳快播科技
 */
package com.imtech.imshare.ui.myshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.widget.ListView;

import com.imtech.imshare.R;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.core.store.StoreManager;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.ui.ShareActivity;
import com.imtech.imshare.utils.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * 我的分享
 * @author Xiaoyuan
 *
 */
public class MyShareActivity extends Activity implements IShareListener{

    final static String TAG = "MyShareActivity";

	ListView mListView;
	List<ShareItem> mItems = new ArrayList<ShareItem>();
	ShareAdapter mAdapter;
	StoreManager mStroeMgr = StoreManager.sharedInstance();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_share);
		mListView = (ListView) findViewById(R.id.listMyShare); 
		mAdapter = new MyShareAdapter(this);
		mListView.setAdapter(mAdapter);
		loadData();
		ShareService.sharedInstance().addListener(this);
	}
	
	@Override
	protected void onDestroy() {
	    super.onDestroy();
	    ShareService.sharedInstance().removeListener(this);
        Process.killProcess(Process.myPid());
	}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        loadData();
    }

    void loadData() {
        Log.d(TAG,  "loadData");
		List<ShareItem> data = mStroeMgr.loadShareItems();
		if (data != null) {
            mItems.clear();
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

    class MyShareAdapter extends  ShareAdapter {

        public MyShareAdapter(Context context) {
            super(context);
        }

        @Override
        public void onAddButtonClicked() {
            Intent i = new Intent(MyShareActivity.this, ShareActivity.class);
            startActivityForResult(i, 0);
        }

        @Override
        public void onPlatformClicked(SnsType type) {
        }
    }

}
