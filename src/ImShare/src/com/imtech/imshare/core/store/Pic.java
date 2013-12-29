/**
 * Author:Xiaoyuan
 * Date: 2013-12-27
 * 深圳快播科技
 */
package com.imtech.imshare.core.store;

/**
 * @author Xiaoyuan
 *
 */
public class Pic {
	 /**
     * 缩略图路径
     */
    public String thumbPath;
    /**
     * 真实图片地址
     */
    public String originPath;
    
    public Pic() {}
    
    public Pic(String thumb, String origin) {
        this.thumbPath = thumb;
        this.originPath = origin;
    }
}
