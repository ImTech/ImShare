/**
 * douzifly @Nov 21, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

import java.util.LinkedList;
import java.util.List;

/**
 * describe Share resource
 * @author douzifly
 *
 */
public class ShareObject{

    public String text;
    public List<Image> images = new LinkedList<ShareObject.Image>();
    
    /**
     * 纬度
     */
    public String lat = "0.0";
    /**
     * 经度
     */
    public String lng = "0.0";
    
    public static class Image {
        public Image(int id, String name, String path) {
            this.name = name;
            this.filePath = path;
            this.id = id;
        }
        
        public Image(int id, String name, String path, String thumbPath) {
            this.name = name;
            this.filePath = path;
            this.id = id;
            this.thumbPath = thumbPath;
        }
        
        public int id;
        public String name;
        public String filePath;
        public String thumbPath;
    }
    
    @Override
    public String toString() {
    	return text + " lat:" + lat + " lng:" + lng + " pic count:" + (images.size());
    }
    
    
}
