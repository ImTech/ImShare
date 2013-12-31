/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-4
 */
package com.imtech.imshare.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;

import com.imtech.imshare.R;

/**
 * 引导界面
 */
public class GuideFragment extends Fragment implements OnClickListener {
	private static final String TAG = "GuideFragment";
	private OnGuideFinishListener mGuideFinishListener;
	
	public void setOnGuideFinishListenr(OnGuideFinishListener l) {
		mGuideFinishListener = l;
	}
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_guide, null);
		Button btn = (Button) view.findViewById(R.id.share);
		btn.setOnClickListener(this);
		return view;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.share:
			goToShare();
			break;
		}
	}

	private void goToShare() {
		if(mGuideFinishListener != null){
			mGuideFinishListener.onGuideFinish();
		}
	}
	
	public interface OnGuideFinishListener{
		void onGuideFinish();
	}
}
