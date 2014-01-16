/**
 * douzifly @2013-12-28
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.ui.myshare;

import java.util.Date;
import java.util.Hashtable;
import java.util.List;

import android.R.color;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imtech.imshare.R;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.ui.AnimUtil;
import com.imtech.imshare.ui.preview.PreviewActivity;
import com.imtech.imshare.ui.preview.TextPreviewActivity;
import com.imtech.imshare.utils.DateUtil;
import com.imtech.imshare.utils.StringUtils;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * @author douzifly
 */
public class MyShareAdapter extends BaseAdapter implements OnClickListener, 
    View.OnLongClickListener {

    final static int VIEW_TYPE_COVER = 0;
    final static int VIEW_TYPE_ITEM = 1;
    final static int VIEW_TYPE_ACTION = 2;
    
    static class ShareViewModel {
    	public String picPath;
    	public String content;
    	public String city;
    	public Date   postTime;
    	public long   id;
    	public Object tag;
    	public boolean isHistory;
    	public boolean shareFailed;
    }

    int mViewTypeCount = 3;
    Context mContext;
    List<ShareViewModel> mItems;
    LayoutInflater  mInflater;
    // active SnsTypes
    Hashtable<SnsType, Boolean> mActiveSnsType = new Hashtable<SnsType, Boolean>();
    String mCoverPath;

    public void setCoverPath(String filePath) {
        if (filePath == null || filePath.equals("")) {
            mCoverPath = null;
            notifyDataSetChanged();
            return;
        }
        mCoverPath = "file:///" + filePath;
        notifyDataSetChanged();;
    }

    public MyShareAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setItems(List<ShareViewModel> items) {
        mItems = items;
        notifyDataSetChanged();
    }
    
    public void addItems(ShareViewModel model) {
    	mItems.add(0, model);
    	notifyDataSetChanged();
    }
    
    public void removeItem(long id) {
    	for (ShareViewModel m : mItems) {
    		if (m.id == id) {
    			mItems.remove(m);
    			notifyDataSetChanged();
    			break;
    		}
    	}
    }

    @Override
    public int getCount() {
        return mItems == null ? 2 : mItems.size() + 2;
    }

    @Override
    public ShareViewModel getItem(int position) {
        return mItems.get(position - 2);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getViewTypeCount() {
        return mViewTypeCount;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return VIEW_TYPE_COVER;
        if (position == 1) return VIEW_TYPE_ACTION;
        return VIEW_TYPE_ITEM;
    }

    DisplayImageOptions coverOpt = new DisplayImageOptions.Builder()
            .cacheInMemory(true)
            .cacheOnDisc(true)
            .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
            .showImageForEmptyUri(R.drawable.bg_cover_def)
            .showImageOnFail(R.drawable.bg_cover_def)
            .considerExifParams(true)
            .showImageOnLoading(R.drawable.bg_cover_def).build()
            ;
    
 

    
    private ImageView mCoverView;
    
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        int viewType = getItemViewType(position);
        if (v == null) {
            if (viewType == VIEW_TYPE_COVER) {
                v = mInflater.inflate(R.layout.myshare_cover, null);
                mCoverView = (ImageView) v.findViewById(R.id.imgCover);
                mCoverView.setOnClickListener(this);
                mCoverView.setOnLongClickListener(this);
            } else if (viewType == VIEW_TYPE_ACTION) {
                v = mInflater.inflate(R.layout.share_action_item, null);
                v.findViewById(R.id.btnShare).setOnClickListener(this);
                v.findViewById(R.id.btnShare).setOnLongClickListener(mAddButtonLongClick);
                ImageView wb = (ImageView) v.findViewById(R.id.icon_weibo);
                wb.setOnClickListener(this);
                ImageView txWb = (ImageView) v.findViewById(R.id.icon_tx_weibo);
                txWb.setOnClickListener(this);
                ActionHolder holder = new ActionHolder();
                holder.txWeibo = txWb;
                holder.weibo = wb;
                v.setTag(holder);
            } else {
                v = mInflater.inflate(R.layout.share_item, null);
                ViewTag tag = new ViewTag();
                tag.imageView = (ImageView) v.findViewById(R.id.imgView);
                tag.txtContent = (TextView) v.findViewById(R.id.txtContent);
                tag.txtDate = (TextView) v.findViewById(R.id.txtDate);
                tag.txtLocal = (TextView) v.findViewById(R.id.txtLocation);
                tag.txtMonth = (TextView) v.findViewById(R.id.txtMonth);
                tag.txtFailed = (TextView) v.findViewById(R.id.txtFailed);
                tag.txtDelete = (TextView) v.findViewById(R.id.txtDelete);
                v.setTag(tag);
                tag.imageView.setOnClickListener(mImageClick);
                tag.txtContent.setOnClickListener(mTextClick);
                tag.txtDelete.setOnClickListener(mTextDeleteClick);
            }
        }

        if (viewType == VIEW_TYPE_ITEM) {
            ViewTag tag = (ViewTag) v.getTag();
            updateView(tag, position);
        } else if (viewType == VIEW_TYPE_ACTION) {
            ActionHolder holder = (ActionHolder) v.getTag();
            updateAction(holder);
        } else if (viewType == VIEW_TYPE_COVER) {
            if (mCoverPath != null) {
                ImageLoader.getInstance().displayImage(mCoverPath, mCoverView, coverOpt);
            } else {
            	mCoverView.setImageResource(R.drawable.bg_cover_def);
            }
        }
        return v;
    }

    @Override
    public boolean isEnabled(int position) {
        return false;
    }

    DisplayImageOptions op;

    {
        op = new DisplayImageOptions.Builder().cacheOnDisc(false)
        		.cacheOnDisc(true)
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.IN_SAMPLE_POWER_OF_2)
                .showImageForEmptyUri(R.drawable.ic_pic_def_gray)
                .showImageOnFail(R.drawable.ic_pic_def_gray)
                .showImageOnLoading(R.drawable.ic_pic_def_gray)
                .considerExifParams(true)
                .build();
    }

    public void updateView(ViewTag tag, int pos) {
        ShareViewModel item = getItem(pos);
        if (!StringUtils.isEmpty(item.picPath)) {
            tag.imageView.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage("file:///" + item.picPath, tag.imageView, op);
            tag.imageView.setTag(item.picPath);
            tag.txtContent.setBackgroundColor(color.transparent);
        } else {
            tag.imageView.setVisibility(View.GONE);
            tag.txtContent.setBackgroundColor(0xfff5f5f5);
        }
        String content = item.content;
        if (content == null) content = "";
        tag.txtContent.setText(content);

        // update date
        if (pos == mViewTypeCount - 1 || (pos > mViewTypeCount - 1 && pos < getCount() && getItem(pos - 1).postTime.getDate() !=
                getItem(pos).postTime.getDate())) {
            tag.txtMonth.setText(DateUtil.getMonthDesc(item.postTime));
            tag.txtDate.setText(String.format("%02d", item.postTime.getDate()));
        } else {
            tag.txtDate.setText("");
            tag.txtMonth.setText("");
        }
        // ---------

        // update location
        String city = getItem(pos).city;
        if (pos == mViewTypeCount - 1) {
            // 第一个必然要显示
            tag.txtLocal.setText(city == null ? "" : city);
        } else {
            // 是否跟上一天为同一天
            boolean sameDay = getItem(pos - 1).postTime.getDate() ==
                    getItem(pos).postTime.getDate();
            String prevCity = getItem(pos - 1).city;
            if (sameDay) {
                // 同一天，地点不同才显示
                if (prevCity == null || !prevCity.equals(city)) {
                    tag.txtLocal.setText(city == null ? "" : city);
                } else {
                    tag.txtLocal.setText("");
                }
            } else {
                tag.txtLocal.setText(city == null ? "" : city);
            }
        }

        // update state
        tag.txtFailed.setVisibility(item.shareFailed ? View.VISIBLE
        		: View.GONE);
        
        tag.txtDelete.setTag(item);
    }

    public void updateAction(ActionHolder holder) {
        Boolean txWbActive = mActiveSnsType.get(SnsType.TENCENT_WEIBO);
        Boolean wBActive = mActiveSnsType.get(SnsType.WEIBO);
        holder.txWeibo.setImageResource(txWbActive != null && txWbActive ? R.drawable.ic_tx_weibo_normal
                : R.drawable.ic_tx_weibo_unable);
        holder.weibo.setImageResource(wBActive != null && wBActive ? R.drawable.ic_weibo_normal
                : R.drawable.ic_weibo_unable);
    }

    @Override
    public boolean onLongClick(View v) {
        onCoverLongPressed();
        return true;
    }

    public static class ActionHolder {
        public ImageView weibo;
        public ImageView txWeibo;
    }
    
    /**
     * Set the platform icon state
     *
     * @param type
     * @param active true means this SnsType is authed
     */
    public void setActiveSnsType(SnsType type, boolean active) {
        mActiveSnsType.put(type, active);
        notifyDataSetChanged();
    }

    @Override
    public void onClick(View v) {
        if (v == mCoverView) {
            onCoverPressed();
            return;
        }
        final int id = v.getId();
        if (id == R.id.btnShare) {
            onAddButtonClicked();
        } else if (id == R.id.icon_tx_weibo) {
            AnimUtil.fadeOut(v, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    onPlatformClicked(SnsType.TENCENT_WEIBO);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                   
                }
            });

        } else if (id == R.id.icon_weibo) {
            AnimUtil.fadeOut(v, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    onPlatformClicked(SnsType.WEIBO);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                   
                }
            });
        }
    }


    static class ViewTag {
        public ImageView imageView;
        public TextView txtContent;
        public TextView txtDate;
        public TextView txtLocal;
        public TextView txtMonth;
        public TextView txtFailed;
        public TextView txtDelete;
    }

    OnClickListener mImageClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String path = (String) v.getTag();
            PreviewActivity.showImage(mContext, path, false);
        }
    };

    OnClickListener mTextClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
            String text = ((TextView) v).getText().toString();
            if (text.equals("")) return;
            TextPreviewActivity.showText(mContext, text);
        }
    };
    
    OnClickListener mTextDeleteClick = new OnClickListener() {

        @Override
        public void onClick(View v) {
        	ShareViewModel model = (ShareViewModel) v.getTag();
        	onDeleteClicked(model);
        }
    };

    /**
     * Called when add share button clicked
     * subclass callback
     */
    public void onAddButtonClicked() {

    }
    
    public void onAddButtonLongClicked() {
        
    }

    /**
     * Called when platform icon clicked
     *
     * @param type
     */
    public void onPlatformClicked(SnsType type) {

    }

    public void onCoverLongPressed() {

    }

    public void onCoverPressed() {

    }
    
    public void onDeleteClicked(ShareViewModel model) {
    	
    }
    
    OnLongClickListener mAddButtonLongClick = new OnLongClickListener() {
        
        @Override
        public boolean onLongClick(View v) {
            onAddButtonLongClicked();
            return true;
        }
    };
}
