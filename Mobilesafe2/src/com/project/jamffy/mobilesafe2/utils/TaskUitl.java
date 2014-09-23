package com.project.jamffy.mobilesafe2.utils;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class TaskUitl {
	public static void killAllProcess(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningPssInfos = am
				.getRunningAppProcesses();
		for (RunningAppProcessInfo runningPssInfo : runningPssInfos) {
			String packageName = runningPssInfo.processName;
			am.killBackgroundProcesses(packageName);
		}
	}

	public static int getProcessCount(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningPsInfos = am
				.getRunningAppProcesses();
		return runningPsInfos.size();
	}

	/**
	 * 获取当前系统的剩余的可用内存信息 byte long
	 */
	public static long getAvailMem(Context context) {
		ActivityManager am = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		MemoryInfo outInfo = new ActivityManager.MemoryInfo();
		am.getMemoryInfo(outInfo);
		return outInfo.availMem;

	}

}
