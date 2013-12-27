/**
 * douzifly @2013-12-26
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.core.store;

import java.util.ArrayList;
import java.util.List;

import com.activeandroid.Model;
import com.activeandroid.annotation.Column;
import com.activeandroid.annotation.Table;
import com.imtech.imshare.sns.SnsType;
import com.imtech.imshare.utils.Log;

/**
 * @author douzifly
 *
 */
@Table(name = "share")
public class ShareItem extends Model {
	
	private final static String TAG = "ShareItem";
   
    /**
     * 分享的图片路径 "/path/to/thumb1:/path/to/file1=/path/to/thumb2:/path/to/file2/"
     */
	@Column(name = "pic_paths")
    public String picPaths;
    
    /**
     * 分享的内容 
     */
	@Column(name = "content")
    public String content;
    
    /**
     * 分享到的平台 "QQ:WEIBO:FACEBOOK" 如此
     */
	@Column(name = "snstypes")
    public String snsTypes;
    
    /**
     * 分享状态 1 成功 2 失败 0 未知
     */
	@Column(name = "state")
    public int state;
	
	@Column(name="lat")
	public String lat;
	
	@Column(name="lng")
	public String lng;
	
	@Column(name="addr")
	public String addr;
	
	public List<Pic> getPicPaths() {
		if (picPaths == null || picPaths.length() == 0) return null;
		String[] item = picPaths.split("=");
		if (item.length == 0 || item[0].length() == 0) return null;
		List<Pic> pic = new ArrayList<Pic>();
		for (String s : item) {
			System.out.println(s);
			Pic p = new Pic();
			String[] item2 = s.split(":");
			if (item2.length == 2) {
				p.thumbPath = item2[0];
				p.originPath = item2[1];
				pic.add(p);
			}
		}
		return pic;
	}
	
	public void setPic(List<Pic> pic) {
		if (pic == null || pic.size() == 0) return;
		StringBuilder sb = new StringBuilder();
		for (Pic p : pic) {
			sb.append(p.thumbPath+":"+p.originPath);
			sb.append("=");
		}
		picPaths = sb.toString();
		Log.d(TAG, "setPic:" + picPaths);
	}
	
	public List<SnsType> getSysTypes() {
		if (snsTypes == null || snsTypes.length() == 0) {
			return null;
		}
		String[] item = snsTypes.split(":");
		List<SnsType> types = new ArrayList<SnsType>();
		for(String str : item) {
			try {
				types.add(SnsType.valueOf(str));
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
		return types;
	}
	
	public void setSnsTypes(List<SnsType> snsTypes) {
		if (snsTypes == null || snsTypes.size() == 0) {
			return;
		}
		
		StringBuilder str = new StringBuilder(); 
		for (SnsType s : snsTypes) {
			str.append(s.name());
			str.append(":");
		}
		this.snsTypes = str.toString();
	}
	
	@Override
	public String toString() {
		return "id:" + getId() + " content:" + content + " path:" + picPaths;
	}

}
