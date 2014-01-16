package com.imtech.imshare.ui;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imtech.imshare.ImShareApp;
import com.imtech.imshare.R;
import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.core.auth.IAuthService;
import com.imtech.imshare.core.locate.LocateHelper;
import com.imtech.imshare.core.locate.Location;
import com.imtech.imshare.core.locate.LocationListener;
import com.imtech.imshare.core.preference.CommonPreference;
import com.imtech.imshare.core.setting.AppSetting;
import com.imtech.imshare.core.share.IShareService;
import com.imtech.imshare.core.share.IShareService.IShareServiceListener;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.core.store.Pic;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.core.store.StoreManager;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.AuthRet.AuthRetState;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.sns.share.ShareObject.Image;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.sns.share.ShareRet.ShareRetState;
import com.imtech.imshare.sns.share.SnsHelper;
import com.imtech.imshare.ui.GuideFragment.OnGuideFinishListener;
import com.imtech.imshare.ui.myshare.ChoosePic;
import com.imtech.imshare.ui.preview.PreviewFragment;
import com.imtech.imshare.utils.BitmapUtil;
import com.imtech.imshare.utils.Log;
import com.imtech.imshare.utils.StringUtils;
import com.imtech.imshare.utils.UmUtil;
import com.umeng.analytics.MobclickAgent;

public class ShareActivity extends FragmentActivity implements OnClickListener, IAuthListener,
		IShareServiceListener, OnGuideFinishListener, LocationListener {
	private static final String TAG = "ShareActivity";

    public final static String EXTRA_FILE_PATH = "file_path";
    final static int REQ_SEL_PIC = 1;
    final static int REQ_TAKE_PIC = 2;

	private View mContentPanel;
	private ImageView mImageView0;
	private ImageView mAddImage;
	private ImageView mWeibo, mTxWeibo;
	private EditText mContentText;
	private IAuthService mAuthService;
	private IShareService mShareService;
	private String mShareImagePath;
	private GuideFragment mGuideFragment;
	private PreviewFragment mPreviewFragment;
	private View mDynamicPanel;
	private Bitmap mBitmap;
	private TextView mLocateView;
	private Location mLocation;
	private SnsType[] mSnsTypes = new SnsType[] {SnsType.WEIBO, SnsType.TENCENT_WEIBO};
	private HashMap<SnsType, Boolean> mChecked = new HashMap<SnsType, Boolean>();
	private boolean mIsLocatedSucess;
	private Button mBtnShare;
	private boolean mLocationChecked;
	private Button mBtnActionBack;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		findView();
		initAuthInfo();

        handleIntent(getIntent());
		mShareService = ShareService.sharedInstance();
        mShareService.setTmpScaledImagePath (AppSetting.getScaledImageDir());
        mShareService.addListener(this);

		locateBegin();
	}

    void setImage(String path) {
        if (path == null) {
            finish();
            return;
        }
        
        mShareImagePath = path;
        Bitmap bitmap;
        int size = getResources().getDimensionPixelSize(R.dimen.image_size);
        bitmap = BitmapUtil.decodeFile(path, size);
        int degree = BitmapUtil.getRotate(path);
        Log.d(TAG, "set image path: " + path + " degree:" + degree);
        bitmap = BitmapUtil.rotateBitmap(bitmap, degree);
        if (bitmap != null) {
            mImageView0.setVisibility(View.VISIBLE);
            mImageView0.setImageBitmap(bitmap);
            mBitmap = bitmap;
            mShareImagePath = path;
            mAddImage.setVisibility(View.GONE);
        }
    }

    void handleIntent(Intent intent) {
        String path = intent.getStringExtra(EXTRA_FILE_PATH);
        if (path != null) {
            setImage(path);
        }
    }
	
	private void locateBegin() {
	    LocateHelper.getInstance(this).locate(this);
	    mLocateView.setText("正在获取位置信息...");
	}

	private void initAuthInfo() {
		mAuthService = AuthService.getInstance();
		syncState();
	}
	
	void syncState() {
	    setIconState(SnsType.WEIBO, ((ImShareApp) getApplication()).isChecked(SnsType.WEIBO));
	    setIconState(SnsType.TENCENT_WEIBO, ((ImShareApp) getApplication()).isChecked(SnsType.TENCENT_WEIBO));
	}
	
	private boolean isChecked(SnsType type) {
	    return mChecked.get(type) != null && mChecked.get(type).booleanValue();
	}
	
	private void setChecked(SnsType type, boolean value) {
	    mChecked.put(type, value);
	    ((ImShareApp)getApplication()).setChecked(type, value);
	}

	private void showGuideView() {
		boolean firstLaunch = CommonPreference.getBoolean(this, CommonPreference.TYPE_APP_FIRST_LAUNCH, true);
        firstLaunch = true;
		if (firstLaunch) {
			mGuideFragment = new GuideFragment();
			mGuideFragment.setOnGuideFinishListenr(this);
			FragmentManager m = getSupportFragmentManager();
			FragmentTransaction trans = m.beginTransaction();
			trans.add(R.id.dynamic_panel, mGuideFragment);
			trans.commit();
			CommonPreference.setBoolean(this, CommonPreference.TYPE_APP_FIRST_LAUNCH, false);
			mDynamicPanel.setVisibility(View.VISIBLE);
			mContentPanel.setVisibility(View.GONE);
		}
	}

	private void removeGuideView() {
		FragmentManager fragmentManager = getSupportFragmentManager();
		FragmentTransaction transaction = fragmentManager.beginTransaction();
		transaction.remove(mGuideFragment);
		mDynamicPanel.setVisibility(View.GONE);
		mContentPanel.setVisibility(View.VISIBLE);
	}

	private void findView() {
		mContentPanel = findViewById(R.id.content_panel);
		mDynamicPanel = findViewById(R.id.dynamic_panel);
		mContentText = (EditText) findViewById(R.id.share_text);
		mImageView0 = (ImageView) findViewById(R.id.image0);
		mAddImage = (ImageView) findViewById(R.id.add_image);
		mWeibo = (ImageView) findViewById(R.id.icon_weibo);
		mTxWeibo = (ImageView) findViewById(R.id.icon_tx_weibo);
		mLocateView = (TextView) findViewById(R.id.locate);
		mBtnShare = (Button) findViewById(R.id.btnActionShare);
		mBtnActionBack = (Button) findViewById(R.id.btnActionBack);
		View weiboItem = findViewById(R.id.weibo);
		View txWeiboItem = findViewById(R.id.tx_weibo);
		// View qzoneItem = findViewById(R.id.qzone);

		// qzoneItem.setOnClickListener(this);
		mBtnShare.setOnClickListener(this);
		weiboItem.setOnClickListener(this);
		txWeiboItem.setOnClickListener(this);
		mAddImage.setOnClickListener(this);
		mImageView0.setOnClickListener(this);
		mLocateView.setOnClickListener(this);
		mBtnActionBack.setOnClickListener(this);
		
		mLocationChecked = CommonPreference.getBoolean(this, CommonPreference.TYPE_LOCATE, true);
		setLocateIcon(mLocationChecked);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAuthService.removeAuthListener(this);
		mShareService.removeListener(this);
		LocateHelper.release();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (!mAuthService.haveListener(this)) {
	            Log.d(TAG, "onResume add auth l");
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

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult requestCode: " + requestCode + " resultCode: " + resultCode);
        if (requestCode == REQ_SEL_PIC) {
            handleSelPic(data);
        } else {
            Log.d(TAG, "auth service checkActivity Result, req:" + requestCode + "result:" + requestCode + "data:" + data);
            if (!mAuthService.haveListener(this)) {
                Log.d(TAG, "onResume add auth l");
                mAuthService.addAuthListener(this);
            }
	    	mAuthService.checkActivityResult(requestCode, resultCode, data);
        }
	}

    public void handleSelPic(Intent data) {
        if (data == null) {
            return;
        }
        Uri uri = data.getData();
        Log.d(TAG, "uri: " + uri.toString());
        String path = BitmapUtil.getImagePathByUri(this, uri);
        setImage(path);
    }

	private void setLocateIcon(boolean needLocate) {
		int resId = needLocate ? R.drawable.ic_location : R.drawable.ic_location_unable;
		mLocateView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
		
		if (!needLocate && !mIsLocatedSucess) {
			mLocateView.setText("点击获取位置信息");
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btnActionShare:
			share();
			break;
		case R.id.weibo:
		    AnimUtil.scaleBig(v, new AnimationListener() {
                
                @Override
                public void onAnimationStart(Animation animation) {
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    auth(SnsType.WEIBO);
                }
            });
			
			break;
		case R.id.tx_weibo:
		    AnimUtil.scaleBig(v, new AnimationListener() {
                
                @Override
                public void onAnimationStart(Animation animation) {
                }
                
                @Override
                public void onAnimationRepeat(Animation animation) {
                }
                
                @Override
                public void onAnimationEnd(Animation animation) {
                    auth(SnsType.TENCENT_WEIBO);
                }
            });
			
			break;
		// case R.id.qzone:
		// auth(SnsType.QQ);
		// break;
		case R.id.add_image:
            ChoosePic c = new ChoosePic();
            c.choose(this, REQ_SEL_PIC, null);
			break;
		case R.id.image0:
			showImagePreview();
			break;
		case R.id.locate:
			clickLocate();
			break;
		case R.id.btnActionBack:
		    finish();
		    break;
		}
	}
	
	private void clickLocate(){
		boolean newValue = !CommonPreference.getBoolean(this, CommonPreference.TYPE_LOCATE, true);
		mLocationChecked = newValue;
		CommonPreference.setBoolean(this, CommonPreference.TYPE_LOCATE, newValue);
		setLocateIcon(newValue);
		
		if (newValue && !mIsLocatedSucess) {
			// 定位失败，重新定位
			locateBegin();
		}
	}

	private void showImagePreview() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction trans = fm.beginTransaction();
		mPreviewFragment = new PreviewFragment();
		mPreviewFragment.setOnDeleteListener(new PreviewFragment.OnDeleteListener() {

			@Override
			public void onDelete() {
				showNormal();
				delSelectImage();
			}
		});
		trans.add(R.id.dynamic_panel, mPreviewFragment, "preivew");
		trans.commit();
		mDynamicPanel.setVisibility(View.VISIBLE);
		mContentPanel.setVisibility(View.GONE);
		mPreviewFragment.setImagePath(mShareImagePath);
	}

	private void delSelectImage() {
		mImageView0.setImageBitmap(null);
		mImageView0.setVisibility(View.GONE);
		if (mBitmap != null) {
		    mBitmap.recycle();
		    mBitmap = null;
		}
		mShareImagePath = null;
		mAddImage.setVisibility(View.VISIBLE);
	}

	private void showNormal() {
		FragmentManager fm = getSupportFragmentManager();
		FragmentTransaction trans = fm.beginTransaction();
		trans.remove(mPreviewFragment);
		mPreviewFragment = null;
		trans.commit();
		mDynamicPanel.setVisibility(View.GONE);
		mContentPanel.setVisibility(View.VISIBLE);
	}

	List<SnsType> getCheckedSns() {
	    List<SnsType> checked = new ArrayList<SnsType>();
	    for (SnsType type : mSnsTypes) {
            if (isChecked(type)) {
                checked.add(type);
            } 
        }
	    return checked;
	}

	private void share() {
	    
	    String text = mContentText.getText().toString().trim();
	    if (mShareImagePath == null && text.equals("")) {
            Toast.makeText(this, "亲，想分享什么呢?", Toast.LENGTH_SHORT).show();
            return;
        }
	    
	    List<SnsType> checked = getCheckedSns();
	    if (checked.size() == 0) {
	        Toast.makeText(this, "亲，先授权哦", Toast.LENGTH_SHORT).show();
	        return;
	    }
	    
		ShareObject obj = new ShareObject();
		obj.text = text;
		 if (StringUtils.isEmpty(obj.text)) {
             obj.text = "分享图片";
         }
		if (mShareImagePath != null) {
			obj.images = new ArrayList<ShareObject.Image>(1);
			obj.images.add(new Image(0, mShareImagePath));
		}
		
		if(mLocationChecked	&& mLocation != null && mLocation.detail != null){
			obj.lat = String.valueOf(mLocation.latitude);
			obj.lng = String.valueOf(mLocation.longitude);
		}

		// 一个ShareItem对应多个ShareObj的task
		ShareItem item = new ShareItem();
		item.postTime = new Date();
		obj.postTime = new Date();
		if (mLocationChecked && mLocation != null) {
		    item.addr = mLocation.detail;
		    item.city = mLocation.city;
		    obj.city = mLocation.city;
		}
		
		item.content = obj.text; 
		if (mShareImagePath != null) {
		    List<Pic> pic = new ArrayList<Pic>();
		    pic.add(new Pic(mShareImagePath, mShareImagePath));
		    item.setPic(pic);
		}
		boolean success = StoreManager.saveShareItem(item);
		Log.d(TAG, "save id:" + item.getId());
		if (success) {
			obj.id = item.getId();
		}
		for (SnsType type : checked) {
            Log.d(TAG, "share addShare :" + type);
            mShareService.addShare(this, obj, type);
        }
        finish();
        
        MobclickAgent.onEvent(this, UmUtil.EVENT_SHARE);
	}
	
	private void clearShareInput() {
	    delSelectImage();
	    mContentText.setText("");
	}
	
	private void auth(SnsType type) {
	    if (mAuthService.isAuthed(type)) {
	        // 已经授权，取消选中，表示不发送到相关平台
	        if (isChecked(type)) {
	            setIconState(type, false);
	        } else {
	            setIconState(type, true);
	        }
	    } else {
	        mAuthService.auth(type, this);
	    }
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			if (mPreviewFragment != null && mPreviewFragment.isVisible()) {
				showNormal();
				return true;
			}
		}
		return super.onKeyDown(keyCode, event);
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

	public void setIconState(SnsType type, boolean enable) {
		switch (type) {
		case TENCENT_WEIBO:
			mTxWeibo.setImageResource(enable ? R.drawable.ic_tx_weibo_normal : R.drawable.ic_tx_weibo_unable);
			break;
		case WEIBO:
			mWeibo.setImageResource(enable ? R.drawable.ic_weibo_normal : R.drawable.ic_weibo_unable);
			break;
		}
		setChecked(type, enable);
	}

	@Override
	public void onShareFinished(final ShareRet ret) {
		Log.d(TAG, "onShareFinished ShareRet msg: " + ret.errorMessage + " state: " + ret.state);
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				String msg = null;
				String snsName = SnsHelper.getSnsName(ret.snsType);
				if (ret.state == ShareRetState.SUCESS) {
					msg = snsName + "分享成功";
				} else if (ret.state == ShareRetState.TOKEN_EXPIRED) {
					msg = snsName + "未授权或授权已过期，请先授权";
				} else if (ret.state == ShareRetState.CANCELED) {
					msg = snsName + "取消分享";
				} else if (ret.state == ShareRetState.FAILED) {
					msg = snsName + "分享失败";
				}
				
				if (msg != null) {
					Toast.makeText(ShareActivity.this, msg, Toast.LENGTH_SHORT).show();
				}
			}
		});
	}

	@Override
	public void onShareImageUpload(ImageUploadInfo info) {
		Log.d(TAG, "onShareImageUpload info progress: " + info.progress);
	}

	@Override
	public void onGuideFinish() {
		removeGuideView();
	}

	@Override
	public void onReceiveLocation(final Location location) {
		if(location != null){
		    Log.d(TAG, "onReceiveLocation latitude: " + location.latitude + " longitude：" + location.longitude
	                + " detail: " + location.detail);
	        mLocation = location;
		} 
		
		mIsLocatedSucess = location != null && location.detail != null;
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLocateView.setText((location == null  || location.detail == null )? "获取位置信息失败" : location.detail);
			}
		});
	}

	@Override
	public void onShareAdded(ShareObject obj) {
	}

	@Override
	public void onShareBegin(ShareObject obj) {
	}

	@Override
	public void onShareComplete() {
	}
}
