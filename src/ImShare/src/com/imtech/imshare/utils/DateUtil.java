/**
 * douzifly @2013-12-29
 * github.com/douzifly
 * douzifly@gmail.com
 */
package com.imtech.imshare.utils;

import java.util.Date;

/**
 * @author douzifly
 *
 */
public class DateUtil {
    
    
    public static String[] dates = {"一月", "二月", "三月", "四月", "五月", "六月", "七月", "八月",
        "九月", "十月", "十一月", "十二月"};
    
    public static String getMonthDesc(Date date) {
        return dates[date.getMonth()];
    }

}
