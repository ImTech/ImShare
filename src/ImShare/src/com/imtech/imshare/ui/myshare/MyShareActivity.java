/**
 * Author:Xiaoyuan
 * Date: 2013-12-27
 * 深圳快播科技
 */
package com.imtech.imshare.ui.myshare;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.imtech.imshare.ImShareApp;
import com.imtech.imshare.R;
import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.core.preference.CommonPreference;
import com.imtech.imshare.core.setting.AppSetting;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.core.store.StoreManager;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.AuthRet.AuthRetState;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.sns.share.SnsHelper;
import com.imtech.imshare.sns.share.ShareRet.ShareRetState;
import com.imtech.imshare.ui.SettingActivity;
import com.imtech.imshare.ui.ShareActivity;
import com.imtech.imshare.utils.BitmapUtil;
import com.imtech.imshare.utils.Log;
import com.imtech.imshare.utils.UmUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的分享
 * @author Xiaoyuan
 *
 */
public class MyShareActivity extends Activity implements IShareListener
    , OnClickListener, IAuthListener{

    final static String TAG = "MyShareActivity";

    private static final int REQ_SEL_PIC = 12;// 从相册中选择
    private static final int REQ_TAKE_PIC = 13; //take pic
    private static final int REQ_SEL_PIC_COVER = 14;
    private static final int REQ_SHARE = 15;// 去分享界面
    private static final int REQ_SETTING = 16;

	ListView mListView;
	List<ShareItem> mItems = new ArrayList<ShareItem>();
	com.imtech.imshare.ui.myshare.MyShareAdapter mAdapter;
	StoreManager mStroeMgr = StoreManager.sharedInstance();
    MenuMode mMenuMode;
    String mTakePicPath;
    AuthService mAuthService;
    Button mBtnActionSetting;
    HashMap<SnsType, Boolean> mChecked = new HashMap<SnsType, Boolean>();

    enum MenuMode {
        Share,
        ChangeCover
    }

    private void initAuthInfo() {
        if (mAuthService == null) {
            mAuthService = AuthService.getInstance();
            mAuthService.loadCachedTokens(this);
        }

        AccessToken token = mAuthService.getAccessToken(SnsType.TENCENT_WEIBO);
        setIconState(SnsType.TENCENT_WEIBO, token != null);
        token = mAuthService.getAccessToken(SnsType.WEIBO);
        setIconState(SnsType.WEIBO, token != null);
    }

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_my_share);
		mListView = (ListView) findViewById(R.id.listMyShare); 
		mBtnActionSetting = (Button) findViewById(R.id.btnActionSetting);
		mBtnActionSetting.setOnClickListener(this);
		
		mAdapter = new MyShareAdapter(this);
		mListView.setAdapter(mAdapter);
        loadCover();
		loadData();
		ShareService.sharedInstance().addListener(this);
        initAuthInfo();
        MobclickAgent.setDebugMode(false);
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
        } else if (requestCode == REQ_SHARE) {
            Log.d(TAG, "back from share ui");
            loadData();
            syncState();
        } else if (requestCode == REQ_SETTING && resultCode == RESULT_OK) {
            // 可能取消了授权
            Log.d(TAG, "back from setting");
            initAuthInfo();
        } else {
            Log.d(TAG, "auth service checkActivity Result, req:" + requestCode + "result:" + requestCode + "data:" + data);
            if (!mAuthService.haveListener(this)) {
                mAuthService.addAuthListener(this);
            }
            mAuthService.checkActivityResult(requestCode, resultCode, data);
        }
    }
    
    void syncState() {
        setIconState(SnsType.WEIBO, ((ImShareApp) getApplication()).isChecked(SnsType.WEIBO));
        setIconState(SnsType.TENCENT_WEIBO, ((ImShareApp) getApplication()).isChecked(SnsType.TENCENT_WEIBO));
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
        String msg = null;
        String snsName = SnsHelper.getSnsName(ret.snsType);
        if (ret.state == ShareRetState.SUCESS) {
            msg = snsName + "分享成功";
        } else if (ret.state == ShareRetState.TOKEN_EXPIRED) {
            msg = snsName + "未授权或授权已过期，请先授权";
            setIconState(ret.snsType, false);
            mAuthService.setAuthExpired(this, ret.snsType);
        } else if (ret.state == ShareRetState.CANCELED) {
            msg = snsName + "取消分享";
        } else if (ret.state == ShareRetState.FAILED) {
            msg = snsName + "分享失败";
        }
        
        if (ret.state == ShareRetState.SUCESS) {
            MobclickAgent.onEvent(this, UmUtil.EVENT_SHARE_SUCESS);
        } else {
            MobclickAgent.onEvent(this, UmUtil.EVENT_SHARE_FAILED);
        }
        
        if (msg != null) {
            Toast.makeText(MyShareActivity.this, msg, Toast.LENGTH_SHORT).show();
        }
    }

    class MyShareAdapter extends com.imtech.imshare.ui.myshare.MyShareAdapter {

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
            authOrToggle(type);
        }

        @Override
        public void onCoverPressed() {
            mMenuMode = MenuMode.ChangeCover;
            openOptionsMenu();
        }
        
        @Override
        public void onAddButtonLongClicked() {
            super.onAddButtonLongClicked();
            goToShare(null);
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
        choosePic.choose(this, REQ_SEL_PIC, null);
    }

    public void takePic() {
        ChooseTakePic c = new ChooseTakePic();
        mTakePicPath = AppSetting.getTakePicDir() + System.currentTimeMillis() + ".jpg";
        File f = new File(mTakePicPath);
        if (!f.getParentFile().exists()) {
            f.getParentFile().mkdirs();
        }
        Bundle b = new Bundle();
        b.putParcelable(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(mTakePicPath)));
        c.choose(this, REQ_TAKE_PIC, b);
    }

    public void changeCover() {
        ChoosePic c = new ChoosePic();
        c.choose(this, REQ_SEL_PIC_COVER, null);
    }

    public void resetCover() {
        mAdapter.setCoverPath(null);
        CommonPreference.setString(this, CommonPreference.KEY_COVER, "");
    }

    public void goToShare(String filePath) {
        Intent i = new Intent(MyShareActivity.this, ShareActivity.class);
        if (filePath != null) {
            i.putExtra(ShareActivity.EXTRA_FILE_PATH, filePath);
        }
        startActivityForResult(i, REQ_SHARE);
    }

    public void handleSelPic(Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();
        Log.d(TAG, "uri: " + uri.toString());
        String path = BitmapUtil.getImagePathByUri(this, uri);
        goToShare(path);
        MobclickAgent.onEvent(this, UmUtil.EVENT_SEL_PIC);
    }

    public void handleTakePic(Intent data) {
//        Bundle bundle = data.getExtras();
//        Bitmap bitmap = (Bitmap) bundle.get("data");
        String savePath = mTakePicPath;
        Log.d(TAG, "handleTakePic:" + savePath); // + " bmp:" + bitmap);
        MobclickAgent.onEvent(this, UmUtil.EVENT_TAKE_PIC);
//        try {
//            BitmapUtil.scaleAndSave(bitmap, 720, savePath);
            goToShare(savePath);
//        } catch (IOException e) {
//            Log.e(TAG, "handleTakePic exp:" + e.getMessage());
//            e.printStackTrace();
//        }
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
        MobclickAgent.onEvent(this, UmUtil.EVENT_CHANGE_COVER);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
       
        if (!mAuthService.haveListener(this)) {
            mAuthService.addAuthListener(this);
        }
        MobclickAgent.onResume(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause remove auth l");
        mAuthService.removeAuthListener(this);
        MobclickAgent.onPause(this);
    }
    
    private void gotoSetting() {
        Intent i = new Intent(this, SettingActivity.class);
        startActivityForResult(i, REQ_SETTING);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnActionSetting) {
            gotoSetting();
        }
    }
    
    private boolean isChecked(SnsType type) {
        return mChecked.get(type) != null && mChecked.get(type).booleanValue();
    }
    
    public void setIconState(SnsType type, boolean enable) {
        mAdapter.setActiveSnsType(type, enable);
        setChecked(type, enable);
    }
    
    private void setChecked(SnsType type, boolean value) {
        Log.d(TAG, "setChecked type:" + type + " value:" + value);
        mChecked.put(type, value);
        ((ImShareApp)getApplication()).setChecked(type, value);
    }
    
    private void authOrToggle(SnsType type) {
        if (mAuthService.isAuthed(type)) {
            // 已经授权，取消选中，表示不发送到相关平台
            if (isChecked(type)) {
                setIconState(type, false);
            } else {
                setIconState(type, true);
            }
        } else {
            mAuthService.auth(type, this);
            MobclickAgent.onEvent(this, UmUtil.EVENT_AUTH);
        }
    }
    
    @Override
    public void onAuthFinished(SnsType snsType, AuthRet ret) {
        Log.d(TAG, "onAuthFinished snsType: " + snsType + " AuthRet: " + ret.state);
        String snsName = SnsHelper.getSnsName(snsType);
        String msg = null;

        if (ret.state == AuthRetState.SUCESS) {
            msg = snsName + "授权成功";
            setIconState(snsType, true);
        } else if (ret.state == AuthRetState.FAILED) {
            msg = snsName + "授权失败";
            setIconState(snsType, false);
        } else if (ret.state == AuthRetState.CANCELED) {
            msg = snsName + "取消授权";
            setIconState(snsType, false);
        }
        
        if (msg != null) {
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
        }
    }
}
