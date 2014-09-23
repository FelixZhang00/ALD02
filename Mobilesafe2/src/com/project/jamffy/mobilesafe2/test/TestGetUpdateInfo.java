package com.project.jamffy.mobilesafe2.test;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.UpdateInfo;
import com.project.jamffy.mobilesafe2.engine.UpdateInfoService;

import android.test.AndroidTestCase;

public class TestGetUpdateInfo extends AndroidTestCase {

	public void testGetInfo()throws Exception {
		UpdateInfoService service=new UpdateInfoService(getContext());
		UpdateInfo info= service.getUpdateInfo(R.string.updateurl);
		//用断言测试再好不过
		assertEquals("2.0", info.getVersion());
		
	}
}
