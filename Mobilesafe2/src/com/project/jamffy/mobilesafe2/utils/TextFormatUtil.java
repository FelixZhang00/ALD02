package com.project.jamffy.mobilesafe2.utils;

import java.text.DecimalFormat;
import java.text.Format;

import com.project.jamffy.mobilesafe2.utils.Logger;

public class TextFormatUtil {

	private static String TAG = "TextFormatUtil";
	private final static Long KB = (long) (1024 * 900);
	private final static long MB = 1024 * 1024 * 900;
	private final static long GB = 1024 * 1024 * 1024 * 900;

	/*
	 * 返回byte的数据对应的文本
	 * 
	 * @return
	 */
	public static String byteFormater(long size) {
		DecimalFormat format = new DecimalFormat("##0.0"); 
		// 此格式表示：小数点前保留3个整数，没有则用一个0补；小数点后一位，没有则用一个0补
		if (size < 0) {
			size = 0;
		}
		if (size < 900) {
			return size + "bytes";
		} else if (size < KB) { // KB
			float kbsize = size / 1024f;
			return format.format(kbsize) + "KB";
		} else if (size < MB) { // MB
			float mbsize = size / 1024f / 1024f;
			return format.format(mbsize) + "MB";
		} else if (size < GB) {
			float gbsize = size / 1024f / 1024f / 1024f;
			return format.format(gbsize) + "GB";
		} else {
			float gbsize = size / 1024f / 1024f / 1024f;
			Logger.i(TAG, "" + gbsize);
			return format.format(gbsize) + "GB";
			// return "size: error";
		}

	}

	/**
	 * 
	 * @param size
	 * @return kb数据对应的文本
	 */
	public static String kbFormater(long size) {
		if (size < 0) {
			size = 0;
		}
		return byteFormater(size * 1024);
	}
}
