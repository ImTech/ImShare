/**
 * douzifly @2014年1月3日
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.ui;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.imtech.imshare.R;
import com.imtech.imshare.core.auth.AuthService;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.sns.auth.AccessToken;
import com.imtech.imshare.utils.Log;
import com.imtech.imshare.utils.UmUtil;
import com.umeng.analytics.MobclickAgent;

/**
 * @author douzifly
 *
 */
public class SettingActivity extends Activity implements OnClickListener{
    final static String TAG = "SettingActivity";
    boolean canceldAuth;
    
    ImageView mTxWeibo, mWeibo;
    AuthService mAuthService;
    
    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }
    
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.btnActionBack).setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        
        mTxWeibo = (ImageView) findViewById(R.id.icon_tx_weibo);
        mWeibo = (ImageView) findViewById(R.id.icon_weibo);
        mTxWeibo.setOnClickListener(this);
        mWeibo.setOnClickListener(this);
        initAuthInfo();
        
        PackageManager m = getPackageManager();
        String app_ver = null;
        try {
            app_ver = m.getPackageInfo(this.getPackageName(), 0).versionName;
        } 
        catch (NameNotFoundException e) {
            // Exception won't be thrown as the current package name is
            // safe to exist on the system.
        }
        String info = "ImTech 出品";
        if (app_ver != null) {
            info += " v" + app_ver;
        }
        ((TextView)findViewById(R.id.txtVersion)).setText(info);
    }
    
    @Override
    public void finish() {
        if (canceldAuth) {
            setResult(RESULT_OK);
        } else {
            setResult(RESULT_CANCELED);
        }
        super.finish();
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
    
    
    public void setIconState(SnsType type, boolean enable) {
        switch (type) {
        case TENCENT_WEIBO:
            mTxWeibo.setImageResource(enable ? R.drawable.ic_tx_weibo_normal : R.drawable.ic_tx_weibo_unable);
            break;
        case WEIBO:
            mWeibo.setImageResource(enable ? R.drawable.ic_weibo_normal : R.drawable.ic_weibo_unable);
            break;
        }
    }
    
    public void cancelAuth(SnsType type) {
        Log.d(TAG, "cancelAuth type");
        if (mAuthService.isAuthed(type)) {
            canceldAuth = true;
            mAuthService.setAuthExpired(this, type);
            Toast.makeText(this, "取消授权成功", Toast.LENGTH_SHORT).show();
            setIconState(type, false);
            MobclickAgent.onEvent(this, UmUtil.EVENT_CANCEL_AUTH);
        } else {
            Toast.makeText(this, "都木有授权哈", Toast.LENGTH_SHORT).show();
        }
        
    }

    @Override
    public void onClick(final View v) {
        AnimUtil.fadeOut(v, new AnimationListener() {
            
            @Override
            public void onAnimationStart(Animation animation) {
            }
            
            @Override
            public void onAnimationRepeat(Animation animation) {
            }
            
            @Override
            public void onAnimationEnd(Animation animation) {
                if (v.getId() == R.id.icon_tx_weibo) {
                    cancelAuth(SnsType.TENCENT_WEIBO);
                }  else if (v.getId() == R.id.icon_weibo) {
                    cancelAuth(SnsType.WEIBO);
                }
            }
        });
    }
    
}
