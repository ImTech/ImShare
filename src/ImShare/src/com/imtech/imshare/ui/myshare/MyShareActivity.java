/**
 * Author:Xiaoyuan
 * Date: 2013-12-27
 * 深圳快播科技
 */
package com.imtech.imshare.ui.myshare;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.*;
import android.os.Process;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import com.imtech.imshare.R;
import com.imtech.imshare.core.preference.CommonPreference;
import com.imtech.imshare.core.setting.AppSetting;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.core.store.StoreManager;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.ui.ShareActivity;
import com.imtech.imshare.utils.BitmapUtil;
import com.imtech.imshare.utils.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 我的分享
 * @author Xiaoyuan
 *
 */
public class MyShareActivity extends Activity implements IShareListener{

    final static String TAG = "MyShareActivity";

    private static final int REQ_SEL_PIC = 12;// 从相册中选择
    private static final int REQ_TAKE_PIC = 13; //take pic
    private static final int REQ_SEL_PIC_COVER = 14;

	ListView mListView;
	List<ShareItem> mItems = new ArrayList<ShareItem>();
	ShareAdapter mAdapter;
	StoreManager mStroeMgr = StoreManager.sharedInstance();
    MenuMode mMenuMode;

    enum MenuMode {
        Share,
        ChangeCover
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_share);
		mListView = (ListView) findViewById(R.id.listMyShare); 
		mAdapter = new MyShareAdapter(this);
		mListView.setAdapter(mAdapter);
        loadCover();
		loadData();
		ShareService.sharedInstance().addListener(this);
	}

    private void loadCover() {
        String path = CommonPreference.getString(this, CommonPreference.KEY_COVER, null);
        if (path != null && !path.equals("")) {
            mAdapter.setCoverPath(path);
        }
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
        if (requestCode == REQ_SEL_PIC) {
            if (resultCode == RESULT_OK) {
                handleSelPic(data);
            }
        } else if (requestCode == REQ_TAKE_PIC) {
            if (resultCode == RESULT_OK) {
                handleTakePic(data);
            }
        }else if (requestCode == REQ_SEL_PIC_COVER) {
            if (resultCode == RESULT_OK) {
                handelSelCover(data);
            }
        } else {
            loadData();
        }
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
            mMenuMode = MenuMode.Share;
            openOptionsMenu();
        }

        @Override
        public void onPlatformClicked(SnsType type) {
        }

        @Override
        public void onCoverLongPressed() {
            mMenuMode = MenuMode.ChangeCover;
            openOptionsMenu();
        }
    }

    final static int MENU_ID_TAKE_PIC = 0;
    final static int MENU_ID_SELECT = 1;
    final static int MENU_ID_CHANGE_COVER = 2;
    final static int MENU_ID_RESET_COVER = 3;

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_ID_TAKE_PIC, 0, "拍照");
        menu.add(0, MENU_ID_SELECT, 1, "选择图片");
        menu.add(1, MENU_ID_CHANGE_COVER, 0, "选择封面");
        menu.add(1, MENU_ID_RESET_COVER, 1, "重置封面");
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mMenuMode == MenuMode.Share) {
            menu.findItem(MENU_ID_CHANGE_COVER).setVisible(false);
            menu.findItem(MENU_ID_RESET_COVER).setVisible(false);
            menu.findItem(MENU_ID_SELECT).setVisible(true);
            menu.findItem(MENU_ID_TAKE_PIC).setVisible(true);
        } else if (mMenuMode == MenuMode.ChangeCover) {
            menu.findItem(MENU_ID_SELECT).setVisible(false);
            menu.findItem(MENU_ID_TAKE_PIC).setVisible(false);
            menu.findItem(MENU_ID_CHANGE_COVER).setVisible(true);
            menu.findItem(MENU_ID_RESET_COVER).setVisible(true);
        }
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == MENU_ID_SELECT) {
            selectPic();
        } else if (item.getItemId() == MENU_ID_TAKE_PIC) {
            takePic();
        } else if (item.getItemId() == MENU_ID_CHANGE_COVER) {
            changeCover();
        } else if (item.getItemId() == MENU_ID_RESET_COVER) {
            resetCover();
        }
        return true;
    }

    public void selectPic() {
        ChoosePic choosePic = new ChoosePic();
        choosePic.choose(this, REQ_SEL_PIC);
    }

    public void takePic() {
        ChooseTakePic c = new ChooseTakePic();
        c.choose(this, REQ_TAKE_PIC);
    }

    public void changeCover() {
        ChoosePic c = new ChoosePic();
        c.choose(this, REQ_SEL_PIC_COVER);
    }

    public void resetCover() {
        mAdapter.setCoverPath(null);
        CommonPreference.setString(this, CommonPreference.KEY_COVER, "");
    }

    public void goToShare(String filePath) {
        Intent i = new Intent(MyShareActivity.this, ShareActivity.class);
        i.putExtra(ShareActivity.EXTRA_FILE_PATH, filePath);
        startActivityForResult(i, 0);
    }

    public void handleSelPic(Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();
        Log.d(TAG, "uri: " + uri.toString());
        String path = BitmapUtil.getImagePathByUri(this, uri);
        goToShare(path);
    }

    public void handleTakePic(Intent data) {
        Bundle bundle = data.getExtras();
        Bitmap bitmap = (Bitmap) bundle.get("data");
        String savePath = AppSetting.getTakePicDir() + System.currentTimeMillis() + ".jpg";
        Log.d(TAG, "handleTakePic:" + savePath + " bmp:" + bitmap);
        try {
            BitmapUtil.scaleAndSave(bitmap, 720, savePath);
            goToShare(savePath);
        } catch (IOException e) {
            Log.e(TAG, "handleTakePic exp:" + e.getMessage());
            e.printStackTrace();
        }
    }

    public void handelSelCover(Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();
        Log.d(TAG, "uri: " + uri.toString());
        String path = BitmapUtil.getImagePathByUri(this, uri);
        mAdapter.setCoverPath(path);
        CommonPreference.setString(this, CommonPreference.KEY_COVER, path);
    }
}
