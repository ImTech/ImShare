/**
 * douzifly @2013-12-28
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.ui.myshare;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imtech.imshare.R;
import com.imtech.imshare.core.store.Pic;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.ui.AnimUtil;
import com.imtech.imshare.ui.preview.PreviewActivity;
import com.imtech.imshare.ui.preview.TextPreviewActivity;
import com.imtech.imshare.utils.DateUtil;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

import java.util.Hashtable;
import java.util.List;

/**
 * @author douzifly
 */
public class ShareAdapter extends BaseAdapter implements OnClickListener {

    final static int VIEW_TYPE_COVER = 0;
    final static int VIEW_TYPE_ITEM = 1;
    final static int VIEW_TYPE_ACTION = 2;

    Context mContext;
    List<ShareItem> mItems;
    LayoutInflater mInflater;
    int mViewTypeCount = 3;
    // active SnsTypes
    Hashtable<SnsType, Boolean> mActiveSnsType = new Hashtable<SnsType, Boolean>();

    public ShareAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }

    public void setItems(List<ShareItem> items) {
        mItems = items;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return mItems == null ? 0 : mItems.size();
    }

    @Override
    public ShareItem getItem(int position) {
        return mItems.get(position);
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        int viewType = getItemViewType(position);
        if (v == null) {
            if (viewType == VIEW_TYPE_COVER) {
                v = new ImageView(mContext);
                ((ImageView) v).setImageResource(R.drawable.bg_cover_def);
                ((ImageView) v).setScaleType(ImageView.ScaleType.CENTER_CROP);
                v.setBackgroundColor(Color.BLACK);
                v.setLayoutParams(new AbsListView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        mContext.getResources().getDimensionPixelSize(R.dimen.cover_height)));
            } else if (viewType == VIEW_TYPE_ACTION) {
                v = mInflater.inflate(R.layout.share_action_item, null);
                v.findViewById(R.id.btnShare).setOnClickListener(this);
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
                v.setTag(tag);
                tag.imageView.setOnClickListener(mImageClick);
                tag.txtContent.setOnClickListener(mTextClick);
            }
        }

        if (viewType == VIEW_TYPE_ITEM) {
            ViewTag tag = (ViewTag) v.getTag();
            updateView(tag, position);
        } else if (viewType == VIEW_TYPE_ACTION) {
            ActionHolder holder = (ActionHolder) v.getTag();
            updateAction(holder);
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
                .cacheInMemory(true)
                .showImageForEmptyUri(R.drawable.ic_pic_def_gray)
                .showImageOnFail(R.drawable.ic_pic_def_gray)
                .showImageOnLoading(R.drawable.ic_pic_def_gray)
                .build();
    }

    public void updateView(ViewTag tag, int pos) {
        ShareItem item = getItem(pos);
        List<Pic> pic = item.getPicPaths();
        if (pic != null && pic.size() > 0) {
            tag.imageView.setVisibility(View.VISIBLE);
            ImageLoader.getInstance().displayImage("file:///" + pic.get(0).originPath, tag.imageView, op);
            tag.imageView.setTag(pic.get(0).originPath);
            tag.txtContent.setBackgroundColor(color.transparent);
        } else {
            tag.imageView.setVisibility(View.GONE);
            tag.txtContent.setBackgroundColor(Color.LTGRAY);
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
//        tag.txtFailed.setVisibility(item.state == 1 ? View.GONE : View.VISIBLE);
    }

    public void updateAction(ActionHolder holder) {
        Boolean txWbActive = mActiveSnsType.get(SnsType.TENCENT_WEIBO);
        Boolean wBActive = mActiveSnsType.get(SnsType.WEIBO);
        holder.txWeibo.setImageResource(txWbActive != null && txWbActive ? R.drawable.ic_tx_weibo_normal
                : R.drawable.ic_tx_weibo_unable);
        holder.weibo.setImageResource(wBActive != null && wBActive ? R.drawable.ic_weibo_normal
                : R.drawable.ic_weibo_unable);
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

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    onPlatformClicked(SnsType.TENCENT_WEIBO);
                }
            });

        } else if (id == R.id.icon_weibo) {
            AnimUtil.fadeOut(v, new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {

                }

                @Override
                public void onAnimationRepeat(Animation animation) {
                    onPlatformClicked(SnsType.WEIBO);
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

    /**
     * Called when add share button clicked
     * subclass callback
     */
    public void onAddButtonClicked() {

    }

    /**
     * Called when platform icon clicked
     *
     * @param type
     */
    public void onPlatformClicked(SnsType type) {

    }
}
