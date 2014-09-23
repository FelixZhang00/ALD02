package com.project.jamffy.mobilesafe2.test;

import java.text.DecimalFormat;

import com.project.jamffy.mobilesafe2.utils.TextFormatUtil;

import android.test.AndroidTestCase;

public class TestTextFormat extends AndroidTestCase {

	public void testByteFormater()throws Exception{
//		DecimalFormat format=new DecimalFormat("###.00");
		long size=1276600320;
		
		System.out.println(TextFormatUtil.byteFormater(size));
	}
}
