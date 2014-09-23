package com.project.jamffy.mobilesafe2.engine;

import java.util.ArrayList;
import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.drawable.Drawable;
import android.os.Debug;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.TaskInfo;

public class TaskInfoProvider {
	private ActivityManager mActivityManager;
	private PackageManager mPackageManager;
	private Context context;

	public TaskInfoProvider(Context context) {
		super();
		mActivityManager = (ActivityManager) context
				.getSystemService(Context.ACTIVITY_SERVICE);
		mPackageManager = context.getPackageManager();
		this.context = context;
	}

	public List<TaskInfo> getAllTaskInfo(
			List<RunningAppProcessInfo> runningPsInfos) {

		List<TaskInfo> taskInfos = new ArrayList<TaskInfo>();
		for (RunningAppProcessInfo psInfo : runningPsInfos) {
			TaskInfo taskInfo;
			taskInfo = new TaskInfo();
			int pid = psInfo.pid;
			taskInfo.setPid(pid);
			String packname = psInfo.processName;
			taskInfo.setPackname(packname);
			try {
				// 根据包名 得到应用程序名
				ApplicationInfo appinfo = mPackageManager.getPackageInfo(
						packname, 0).applicationInfo;
				String appname = appinfo.loadLabel(mPackageManager).toString();
				taskInfo.setAppname(appname);
				Drawable appicon = appinfo.loadIcon(mPackageManager);
				taskInfo.setAppicon(appicon);
				if (filterApp(appinfo)) {
					taskInfo.setSystemApp(false);
				} else {
					taskInfo.setSystemApp(true);
				}
			} catch (NameNotFoundException e) { // 没有找到appnane 说明是系统进程
				e.printStackTrace();
				String appname = packname;
				taskInfo.setAppname(appname);
				Drawable appicon = context.getResources().getDrawable(
						R.drawable.ic_launcher);
				taskInfo.setAppicon(appicon);
				taskInfo.setSystemApp(true);
			}

			Debug.MemoryInfo[] memoryInfos = mActivityManager
					.getProcessMemoryInfo(new int[] { pid });
			int memsize = memoryInfos[0].getTotalPrivateDirty(); // 这样获取的内存并不准确
			taskInfo.setMemorysize(memsize);
			taskInfo.setIschecked(false);

			taskInfos.add(taskInfo);
			taskInfo = null;

		}
		return taskInfos;
	}

	/**
	 * 判断某个app是否是第三方的
	 * 
	 * @param info
	 * @return true 是第三方的
	 */
	public boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}
}
