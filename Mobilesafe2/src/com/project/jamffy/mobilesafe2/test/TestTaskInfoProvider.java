package com.project.jamffy.mobilesafe2.test;

import java.util.List;

import com.project.jamffy.mobilesafe2.engine.TaskInfoProvider;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.test.AndroidTestCase;

public class TestTaskInfoProvider extends AndroidTestCase {

	public void testGetAllInfo() throws Exception {
		TaskInfoProvider provider = new TaskInfoProvider(getContext());
		ActivityManager am= (ActivityManager) getContext().getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningPsInfos = am
				.getRunningAppProcesses();
		System.out.println(provider.getAllTaskInfo(runningPsInfos).toString());

	}
}
