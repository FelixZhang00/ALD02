package com.project.jamffy.mobilesafe2.test;

import java.util.List;

import com.project.jamffy.mobilesafe2.domain.SmsInfo;
import com.project.jamffy.mobilesafe2.engine.SmsInfoEngine;

import android.test.AndroidTestCase;

public class TestSmsInfoEngine extends AndroidTestCase {

	public void testGetAllInfo() throws Exception{
		SmsInfoEngine engine=new SmsInfoEngine(getContext());
		List<SmsInfo> infos= engine.getAllSms();
		assertEquals(4, infos.size());
		
	}
	public void testGetCounts() throws Exception{
		SmsInfoEngine engine=new SmsInfoEngine(getContext());
		int counts=(int) engine.getCounts();
		assertEquals(4, counts);
	}
}
