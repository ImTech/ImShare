package com.imtech.imshare.ui;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Process;
import android.provider.MediaStore;
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

import com.imtech.imshare.R;
import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.core.auth.IAuthService;
import com.imtech.imshare.core.locate.LocateHelper;
import com.imtech.imshare.core.locate.Location;
import com.imtech.imshare.core.locate.LocationListener;
import com.imtech.imshare.core.preference.CommonPreference;
import com.imtech.imshare.core.share.IShareService;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.AuthRet.AuthRetState;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.sns.share.ShareObject.Image;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.sns.share.ShareRet.ShareRetState;
import com.imtech.imshare.sns.share.SnsHelper;
import com.imtech.imshare.ui.GuideFragment.OnGuideFinishListener;
import com.imtech.imshare.utils.BitmapUtil;
import com.imtech.imshare.utils.Log;

public class ShareActivity extends FragmentActivity implements OnClickListener, IAuthListener,
		IShareListener, OnGuideFinishListener, LocationListener {
	private static final String TAG = "ShareActivity";
	private static final int PHOTO_REQUEST_GALLERY = 12;// 从相册中选择
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
	private boolean mIsWbAuthed;
	private boolean mIsQQAuthed;
	private SnsType[] mSnsTypes = new SnsType[] {SnsType.WEIBO, SnsType.TENCENT_WEIBO};
	private HashMap<SnsType, Boolean> mChecked = new HashMap<SnsType, Boolean>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		findView();
		initAuthInfo();

		mShareService = ShareService.sharedInstance();
		mShareService.addListener(this);

		showGuideView();
		locateBegtin();
	}
	
	private void locateBegtin() {
	    LocateHelper.getInstance(this).locate(this);
	    mLocateView.setText("正在获取位置信息...");
	}

	private void initAuthInfo() {
		mAuthService = AuthService.getInstance();
		mAuthService.addAuthListener(this);
		mAuthService.loadCachedTokens(this);

		AccessToken token = mAuthService.getAccessToken(SnsType.TENCENT_WEIBO);
		if (token != null) {
			mTxWeibo.setImageResource(R.drawable.ic_tx_weibo_normal);
			setChecked(SnsType.TENCENT_WEIBO, true);
		} else {
			mTxWeibo.setImageResource(R.drawable.ic_tx_weibo_unable);
		}

		token = mAuthService.getAccessToken(SnsType.WEIBO);
		if (token != null) {
			mWeibo.setImageResource(R.drawable.ic_weibo_normal);
			setChecked(SnsType.WEIBO, true);
		} else {
			mWeibo.setImageResource(R.drawable.ic_weibo_unable);
		}
	}
	
	private boolean isChecked(SnsType type) {
	    return mChecked.get(type) != null && mChecked.get(type).booleanValue();
	}
	
	private void setChecked(SnsType type, boolean value) {
	    mChecked.put(type, value);
	}

	private void showGuideView() {
		boolean firstLaunch = CommonPreference.getBoolean(this, CommonPreference.TYPE_APP_FIRST_LAUNCH, true);
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
		Button shareBtn = (Button) findViewById(R.id.share_out);
		Button sshareBtn = (Button) findViewById(R.id.share);
		View weiboItem = findViewById(R.id.weibo);
		View txWeiboItem = findViewById(R.id.tx_weibo);
		// View qzoneItem = findViewById(R.id.qzone);

		// qzoneItem.setOnClickListener(this);
		shareBtn.setOnClickListener(this);
		sshareBtn.setOnClickListener(this);
		weiboItem.setOnClickListener(this);
		txWeiboItem.setOnClickListener(this);
		mAddImage.setOnClickListener(this);
		mImageView0.setOnClickListener(this);
		mLocateView.setOnClickListener(this);

		setLocateIcon(CommonPreference.getBoolean(this, CommonPreference.TYPE_LOCATE, true));
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAuthService.removeAuthListener(this);
		mShareService.removeListener(this);
		LocateHelper.release();
		Process.killProcess(Process.myPid());
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult requestCode: " + requestCode + " resultCode: " + resultCode);
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			addImageFinish(data);
		} else {
			mAuthService.checkActivityResult(requestCode, resultCode, data);
		}
	}

	private void setLocateIcon(boolean needLocate) {
		int resId = needLocate ? R.drawable.ic_location : R.drawable.ic_location_unable;
		mLocateView.setCompoundDrawablesWithIntrinsicBounds(resId, 0, 0, 0);
	}

	private void addImageFinish(Intent data) {
		// String path = data != null ? data.getDataString() : null;
		// Log.d(TAG, "addImageFinish path: " + path);
		if (data == null) {
			return;
		}
		Uri uri = data.getData();
		Log.d(TAG, "uri: " + uri.toString());
		String path = BitmapUtil.getImagePathByUri(this, uri);
		Log.d(TAG, "path: " + path);
		Bitmap bitmap;
		int size = getResources().getDimensionPixelSize(R.dimen.image_size);
		bitmap = BitmapUtil.decodeFile(path, size);
		if (bitmap != null) {
			mImageView0.setVisibility(View.VISIBLE);
			mImageView0.setImageBitmap(bitmap);
			mBitmap = bitmap;
			mShareImagePath = path;
			mAddImage.setVisibility(View.GONE);
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share:
			// LocateHelper.getInstance(getApplicationContext()).locate();
			// break;
		case R.id.share_out:
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
		    AnimUtil.fadeOut(v);
			addImage();
			break;
		case R.id.image0:
			showImagePreview();
			break;
		case R.id.locate:
			clickLocate();
			break;
		}
	}
	
	private void clickLocate(){
		boolean newValue = !CommonPreference.getBoolean(this, CommonPreference.TYPE_LOCATE, true);
		CommonPreference.setBoolean(this, CommonPreference.TYPE_LOCATE, newValue);
		setLocateIcon(newValue);
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
		trans.add(R.id.dynamic_panel, mPreviewFragment);
		trans.commit();
		mDynamicPanel.setVisibility(View.VISIBLE);
		mContentPanel.setVisibility(View.GONE);
		mPreviewFragment.setImagePath(mShareImagePath);
	}

	private void delSelectImage() {
		mImageView0.setImageBitmap(null);
		mImageView0.setVisibility(View.GONE);
		mBitmap.recycle();
		mBitmap = null;
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

	private void addImage() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
		try {
			startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
		} catch (Exception e) {
			e.printStackTrace();
			Toast.makeText(this, "无法选择照片,没有图库应用?", Toast.LENGTH_SHORT).show();
		}
	}

	private void share() {
		ShareObject obj = new ShareObject();
		obj.text = mContentText.getText().toString();
		if (mShareImagePath != null) {
			obj.images = new ArrayList<ShareObject.Image>(1);
			obj.images.add(new Image(0, null, mShareImagePath));
		}
		
		if(CommonPreference.getBoolean(this, CommonPreference.TYPE_LOCATE, true)
				&& mLocation != null){
			obj.lat = String.valueOf(mLocation.latitude);
			obj.lng = String.valueOf(mLocation.longitude);
		}
		
		mShareService.clear();
		
		for (SnsType type : mSnsTypes) {
		    if (isChecked(type)) {
		        Log.d(TAG, "share addShare :" + type);
		        mShareService.addShare(this, obj, type);
		    } else {
		        Log.d(TAG, "share " + type + " not checked");
		    }
		}
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
		
		runOnUiThread(new Runnable() {

			@Override
			public void run() {
				mLocateView.setText(location == null ? "获取失败" : location.detail);
			}
		});
	}
}
