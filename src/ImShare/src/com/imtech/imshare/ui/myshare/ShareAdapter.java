/**
 * douzifly @2013-12-28
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.ui.myshare;

import java.util.List;

import android.R.color;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.imtech.imshare.R;
import com.imtech.imshare.core.store.Pic;
import com.imtech.imshare.core.store.ShareItem;
import com.imtech.imshare.ui.preview.PreviewActivity;
import com.imtech.imshare.ui.preview.TextPreviewActivity;
import com.imtech.imshare.utils.DateUtil;
import com.imtech.imshare.widget.TextViewME;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * @author douzifly
 *
 */
public class ShareAdapter extends BaseAdapter{
    
    public ShareAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
        mContext = context;
    }
    
    Context         mContext;
    List<ShareItem> mItems;
    LayoutInflater  mInflater;
    
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
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            v = mInflater.inflate(R.layout.share_item, null);
            ViewTag tag = new ViewTag();
            tag.imageView = (ImageView) v.findViewById(R.id.imgView);
            tag.txtContent = (TextView) v.findViewById(R.id.txtContent);
            tag.txtDate = (TextView) v.findViewById(R.id.txtDate);
            tag.txtLocal = (TextView) v.findViewById(R.id.txtLocation);
            tag.txtMonth = (TextView) v.findViewById(R.id.txtMonth);
            v.setTag(tag);
            tag.imageView.setOnClickListener(mImageClick);
            tag.txtContent.setOnClickListener(mTextClick);
        }
        
        ViewTag tag = (ViewTag) v.getTag();
        updateView(tag, position);
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
        if (pos == 0 || (pos > 0 && pos < getCount() && getItem(pos - 1).postTime.getDate() != 
                getItem(pos).postTime.getDate())) {
            tag.txtMonth.setText(DateUtil.getMonthDesc(item.postTime));
            tag.txtDate.setText(String.valueOf(item.postTime.getDate()));
        } else {
            tag.txtDate.setText("");
            tag.txtMonth.setText("");
        }
        // ---------
        
        // update location
        String city = getItem(pos).city;
        if (pos == 0) {
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
        
    }

    
    static class ViewTag {
        public ImageView imageView;
        public TextView  txtContent;
        public TextView  txtDate;
        public TextView  txtLocal;
        public TextView  txtMonth;
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
}
