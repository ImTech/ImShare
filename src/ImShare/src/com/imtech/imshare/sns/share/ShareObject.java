/**
 * douzifly @Nov 21, 2013
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.sns.share;

import java.util.List;

/**
 * describe Share resource
 * @author douzifly
 *
 */
public class ShareObject {

    public String text;
    public List<Image> images;
    public int id;
    
    public static class Image {
        public String name;
        public String filePath;
    }
}
