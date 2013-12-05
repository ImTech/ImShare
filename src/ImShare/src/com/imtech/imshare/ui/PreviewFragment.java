/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-4
 */
package com.imtech.imshare.ui;

import com.imtech.imshare.R;
import com.imtech.imshare.utils.BitmapUtil;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

/**
 * 图片预览界面
 */
public class PreviewFragment extends Fragment implements OnClickListener {
	private static final String TAG = "PreviewFragment";
	private OnDeleteListener mListener;
	private ImageView mImageView;
	private Bitmap mImageBitmap;
	private String mImagePath;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "onCreateView");
		View view = inflater.inflate(R.layout.fragment_preview, null);
		Button btn = (Button) view.findViewById(R.id.delete);
		btn.setOnClickListener(this);
		mImageView = (ImageView) view.findViewById(R.id.image);
		showImage();
		return view;
	}

	public void setOnDeleteListener(OnDeleteListener l) {
		mListener = l;
	}

	private void showImage(){
		int size = getResources().getDisplayMetrics().widthPixels;
		Bitmap bmp = BitmapUtil.decodeFile(mImagePath, size);
		if (bmp != null) {
			mImageView.setImageBitmap(bmp);
			mImageBitmap = bmp;
		}
	}
	
	public void setImagePath(String path) {
		Log.d(TAG, "setImagePath");
		mImagePath = path;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		Log.d(TAG, "onDestroy");
	}

	@Override
	public void onDetach() {
		super.onDetach();
		Log.d(TAG, "onDetach");
		mImageView.setImageBitmap(null);
		if (mImageBitmap != null) {
			mImageBitmap.recycle();
			mImageBitmap = null;
		}
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.delete:
			notifyDelete();
			break;
		}
	}

	private void notifyDelete() {
		if (mListener != null) {
			mListener.onDelete();
		}
	}

	public interface OnDeleteListener {
		void onDelete();
	}
}
