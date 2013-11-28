package com.imtech.imshare.ui;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.imtech.imshare.R;
import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.core.auth.IAuthService;
import com.imtech.imshare.core.share.IShareService;
import com.imtech.imshare.core.share.ShareService;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AuthRet;
import com.imtech.imshare.sns.auth.IAuthListener;
import com.imtech.imshare.sns.share.IShareListener;
import com.imtech.imshare.sns.share.ImageUploadInfo;
import com.imtech.imshare.sns.share.ShareObject;
import com.imtech.imshare.sns.share.ShareObject.Image;
import com.imtech.imshare.sns.share.ShareRet;
import com.imtech.imshare.utils.Log;

public class ShareActivity extends Activity implements OnClickListener,
		IAuthListener, IShareListener {
	private static final String TAG = "ShareActivity";
	private static final int PHOTO_REQUEST_GALLERY = 12;// 从相册中选择
	private ImageView mImageView1;
	private EditText mContentText;
	private IAuthService mAuthService;
	private IShareService mShareService;
	private String mShareImagePath;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_share);
		findView();
		mAuthService = AuthService.getInstance();
		mAuthService.addAuthListener(this);
		mAuthService.loadCachedTokens(this);

		mShareService = ShareService.sharedInstance();
		mShareService.addListener(this);
	}

	private void findView() {
		mContentText = (EditText) findViewById(R.id.share_text);
		mImageView1 = (ImageView) findViewById(R.id.image1);
		Button shareBtn = (Button) findViewById(R.id.share_out);
		View weiboItem = findViewById(R.id.weibo);
		View txWeiboItem = findViewById(R.id.tx_weibo);
		View addImageItem = findViewById(R.id.add_image);

		shareBtn.setOnClickListener(this);
		weiboItem.setOnClickListener(this);
		txWeiboItem.setOnClickListener(this);
		addImageItem.setOnClickListener(this);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		mAuthService.removeAuthListener(this);
		mShareService.removeListener(this);
	}

	@Override
	protected void onResume() {
		super.onResume();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "onActivityResult requestCode: " + requestCode
				+ " resultCode: " + resultCode);
		if (requestCode == PHOTO_REQUEST_GALLERY) {
			addImageFinish(data);
		} else{
			mAuthService.checkActivityResult(requestCode, resultCode, data);
		}
	}

	private void addImageFinish(Intent data) {
//		String path = data != null ? data.getDataString() : null;
//		Log.d(TAG, "addImageFinish path: " + path);
		if(data == null){
			return;
		}
		Uri uri = data.getData();
		Log.d(TAG, "uri: " + uri.toString());
		ContentResolver cr = getContentResolver();
		String path = getImagePath(uri, cr);
		Log.d(TAG, "path: " + path);
		mShareImagePath = path;
		try {
			Bitmap bitmap = BitmapFactory.decodeStream(cr.openInputStream(uri));
			mImageView1.setVisibility(View.VISIBLE);
			mImageView1.setImageBitmap(bitmap);
		} catch (FileNotFoundException e) {
			Log.e("Exception", e.getMessage());
		}
	}
	
	private String getImagePath(Uri uri, ContentResolver cr){
		 Cursor cursor = cr.query(uri, null, null, null, null);
		 String path = null;
		 if(cursor.moveToFirst()){
			 int column = cursor.getColumnIndex(MediaStore.Images.Media.DATA);
		     path = cursor.getString(column);
		 }
	     cursor.close();
	     return path;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share_out:
			share();
			break;
		case R.id.weibo:
			authWeibo();
			break;
		case R.id.tx_weibo:
			authTxWeibo();
			break;
		case R.id.add_image:
			addImage();
			break;
		}
	}

	private void addImage() {
		Intent intent = new Intent(Intent.ACTION_PICK, null);
		intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				"image/*");
		startActivityForResult(intent, PHOTO_REQUEST_GALLERY);
	}

	private void share() {
		ShareObject obj = new ShareObject();
		obj.text = mContentText.getText().toString();
		if(mShareImagePath != null){
			obj.images = new ArrayList<ShareObject.Image>();
			obj.images.add(new Image(123, "pic", mShareImagePath));
		}
		mShareService.share(this, this, obj, SnsType.WEIBO);
		// mShareService.share(this, this, obj, SnsType.TENCENT_WEIBO);
	}

	private void authWeibo() {
		mAuthService.auth(SnsType.WEIBO, this, this);
	}

	private void authTxWeibo() {
		mAuthService.auth(SnsType.TENCENT_WEIBO, this, this);
	}

	@Override
	public void onAuthFinished(SnsType snsType, AuthRet ret) {
		Log.d(TAG, "onAuthFinished snsType: " + snsType + " AuthRet: "
				+ ret.state);
	}

	@Override
	public void onShareFinished(ShareRet ret) {
		Log.d(TAG, "onShareFinished ShareRet msg: " + ret.errorMessage
				+ " state: " + ret.state);
	}

	@Override
	public void onShareImageUpload(ImageUploadInfo info) {
		Log.d(TAG, "onShareImageUpload info progress: " + info.progress);
	}

}
