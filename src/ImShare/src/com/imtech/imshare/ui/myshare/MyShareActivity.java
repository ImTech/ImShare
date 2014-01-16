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
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.app.NotificationCompat;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.imtech.imshare.ImShareApp;
import com.imtech.imshare.R;
import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.core.preference.CommonPreference;
import com.imtech.imshare.core.setting.AppSetting;
import com.imtech.imshare.core.share.IShareService.IShareServiceListener;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.core.store.Pic;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.core.store.StoreManager;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.AuthRet.AuthRetState;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.sns.share.ShareRet.ShareRetState;
import com.imtech.imshare.sns.share.SnsHelper;
import com.imtech.imshare.ui.SettingActivity;
import com.imtech.imshare.ui.ShareActivity;
import com.imtech.imshare.ui.myshare.MyShareAdapter.ShareViewModel;
import com.imtech.imshare.utils.BitmapUtil;
import com.imtech.imshare.utils.Log;
import com.imtech.imshare.utils.UmUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * 我的分享
 * @author Xiaoyuan
 *
 */
public class MyShareActivity extends Activity implements IShareServiceListener
    , OnClickListener, IAuthListener, OnScrollListener{

    final static String TAG = "MyShareActivity";
    final static int NOTI_ID = 1;
    
    private static final int REQ_SEL_PIC = 12;// 从相册中选择
    private static final int REQ_TAKE_PIC = 13; //take pic
    private static final int REQ_SEL_PIC_COVER = 14;
    private static final int REQ_SHARE = 15;// 去分享界面
    private static final int REQ_SETTING = 16;

	ListView mListView;
	com.imtech.imshare.ui.myshare.MyShareAdapter mAdapter;
	StoreManager mStroeMgr = StoreManager.sharedInstance();
    MenuMode mMenuMode;
    String mTakePicPath;
    AuthService mAuthService;
    Button mBtnActionSetting;
    HashMap<SnsType, Boolean> mChecked = new HashMap<SnsType, Boolean>();
    boolean mIsAllDataLoaded;
    boolean mIsLoading;
    ProgressBar mProgressBar;
    Handler mHandler = new Handler();
    boolean mIsShareing;

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
		mProgressBar = (ProgressBar) findViewById(R.id.progressCover);
		mBtnActionSetting.setOnClickListener(this);
		
		mAdapter = new MyShareAdapter(this);
		mListView.setAdapter(mAdapter);
		mListView.setOnScrollListener(this);
        loadCover();
		loadHistoryData();
		ShareService.sharedInstance().addListener(this);
        initAuthInfo();
        MobclickAgent.setDebugMode(false);
        runCheck();
	}
	
	private void runCheck() {
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				checkNoMedia();
			}
		}).start();
	}
	
	public void checkNoMedia() {
		try {
			String path = AppSetting.getScaledImageDir() + ".nomedia";
			File f = new File(path);
			if (!f.exists()) {
				if (!f.getParentFile().exists()) {
					f.getParentFile().mkdirs();
				}
				f.createNewFile();
			} 
		} catch(Exception e) {
			e.printStackTrace();
		}
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
            loadHistoryData();
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

    synchronized void loadHistoryData() {
        Log.d(TAG,  "loadData mIsAllDataLoaded:" + mIsAllDataLoaded);
        if (mIsAllDataLoaded || mIsLoading) return;
        new LoadDataTask().execute();
	}
    
    class LoadDataTask extends AsyncTask<Void, Void, List<ShareItem> > {
    	
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		if (!mIsShareing) {
    			showProgress();
    		}
    		mIsLoading = true;
    	}
    	
		@Override
		protected List<ShareItem> doInBackground(Void... params) {
			List<ShareItem> data = mStroeMgr.getNextDatas();
			if (data == null) {
				mIsAllDataLoaded = true;
			}
			return data;
		}
		
		@Override
		protected void onPostExecute(List<ShareItem> data){
			mAdapter.setItems(convertModel(mStroeMgr.getLoadedDatas()));
			if (!mIsShareing) {
				hideProgressDelay();
			}
			mIsLoading = false;
		}
    }
    
    public List<ShareViewModel> convertModel(List<ShareItem> items) {
    	if (items == null) return null;
    	List<ShareViewModel> models = new ArrayList<MyShareAdapter.ShareViewModel>(items.size());
    	for (ShareItem s : items) {
    		ShareViewModel m = new ShareViewModel();
    		m.id = s.getId();
    		m.content = s.content;
    		m.city = s.city;
    		List<Pic> pics = s.getPicPaths();
    		if (pics != null && pics.size() > 0) {
    			m.picPath = pics.get(0).originPath;
    		}
    		m.postTime = s.postTime;
    		m.isHistory = true;
    		models.add(m);
    	}
    	return models;
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
        
        @Override
        public void onDeleteClicked(ShareViewModel model) {
        	super.onDeleteClicked(model);
        	StoreManager.deleteShareItem(model.id);
        	mAdapter.removeItem(model.id);
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
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_MENU) {
            // ignore
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_BACK) {
        	boolean ret = moveTaskToBack(true);
        	Log.d(TAG, "moveTaskToBack ret:" + ret);
        	if (ret) {
        		checkAndShowNotificaton();
        	}
        	return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
    private void checkAndShowNotificaton() {
    	if (!mIsShareing) {
    		return;
    	}
    	Intent i = new Intent(this, MyShareActivity.class);
    	i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    	PendingIntent pi = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
    	Notification notification = new NotificationCompat.Builder(this)
    		.setAutoCancel(true)
    		.setContentTitle("极享正在为您分享")
    		.setContentText("触摸可以返回极享")
    		.setTicker("正在分享")
    		.setSmallIcon(R.drawable.ic_launcher)
    		.setLargeIcon(((BitmapDrawable)getResources().getDrawable(R.drawable.ic_launcher)).getBitmap())
    		.setContentIntent(pi)
    		.setOngoing(false)
    		.build();
    	
    	NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	nm.notify(NOTI_ID, notification);
    	
    }
    
    private void cancelNotification() {
    	NotificationManager nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
    	nm.cancel(NOTI_ID);
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
        String savePath = mTakePicPath;
        Log.d(TAG, "handleTakePic:" + savePath); // + " bmp:" + bitmap);
        MobclickAgent.onEvent(this, UmUtil.EVENT_TAKE_PIC);
        goToShare(savePath);
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
			Log.d(TAG, "scroll idle");
			if (mAdapter != null && mAdapter.getCount() -1 == view.getLastVisiblePosition()) {
				loadHistoryData();
			}
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
	}
	
	Animation mBottomIn;
	Animation mBottomOut;
	boolean   mIsShowProgress;
	
	public void showProgress() {
		if (mProgressBar == null || mIsShowProgress) return;
		if (mBottomIn == null) {
			mBottomIn = AnimationUtils.loadAnimation(this, R.anim.bottom_in);
		}
		mProgressBar.setVisibility(View.VISIBLE);
		mProgressBar.startAnimation(mBottomIn);
		mIsShowProgress = true;
	}
	
	public void hideProgressDelay() {
		if (!mIsShowProgress) return;
		if (mProgressBar == null) return;
		if (mBottomOut == null) {
			mBottomOut = AnimationUtils.loadAnimation(this, R.anim.bottom_out);
		}
		
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				mProgressBar.startAnimation(mBottomOut);
				mProgressBar.setVisibility(View.GONE);
			}
		}, 500);	
		mIsShowProgress = false;    		
	}

	@Override
	public void onShareAdded(ShareObject obj) {
		Log.d(TAG, "onShareAdded obj:" + obj);
		ShareViewModel m = new ShareViewModel();
		m.city = obj.city;
		m.content = obj.text;
		m.id = obj.id;
		m.isHistory = false;
		if (obj.images != null && obj.images.size() > 0) {
			m.picPath = obj.images.get(0).filePath;
		}
		m.postTime = obj.postTime;
		mAdapter.addItems(m);
		mIsShareing = true;
		showProgress();
	}

	@Override
	public void onShareBegin(ShareObject obj) {
		Log.d(TAG, "onShareBegin obj:" + obj);
	}

	@Override
	public void onShareComplete() {
		Log.d(TAG, "onShareComplete");
		runOnUiThread(new Runnable() {
			
			@Override
			public void run() {
				hideProgressDelay();
				mIsShareing = false;
				cancelNotification();
			}
		});
		
	}	
}	
