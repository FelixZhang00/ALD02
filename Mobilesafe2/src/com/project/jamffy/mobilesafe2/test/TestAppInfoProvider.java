package com.project.jamffy.mobilesafe2.test;

import java.util.List;

import com.project.jamffy.mobilesafe2.domain.AppInfo;
import com.project.jamffy.mobilesafe2.engine.AppInfoProvider;

import android.test.AndroidTestCase;

public class TestAppInfoProvider extends AndroidTestCase {

	public void testGetAllInfo() throws Exception{
		AppInfoProvider provider=new AppInfoProvider(getContext());
		List<AppInfo> infos= provider.getAllApps();
		System.out.println(infos);
	}
	
}
