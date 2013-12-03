/*
 * @project :ImShare
 * @author  :huqiming 
 * @date    :2013-12-4
 */
package com.imtech.imshare.sns.share;

import com.imtech.imshare.sns.SnsType;

/**
 *
 */
public class SnsHelper {
	public static String getSnsName(SnsType type) {
		switch (type) {
		case TENCENT_WEIBO:
			return "腾讯微博";
		case WEIBO:
			return "新浪微博";
		}
		return null;
	}
}
