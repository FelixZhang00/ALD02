package com.project.jamffy.mobilesafe2;

import android.app.Application;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import com.project.jamffy.mobilesafe2.utils.Logger;

import com.project.jamffy.mobilesafe2.domain.TaskInfo;
import com.project.jamffy.mobilesafe2.receiver.LockScreenReceiver;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class MyApplication extends Application {

	public TaskInfo taskInfo;
	private static final String TAG = "MyApplication";

	@Override
	public void onCreate() {
		super.onCreate();
		Logger.i(TAG, "MyApplication onCreate");
	
		IntentFilter intentFilter=new IntentFilter(Intent.ACTION_SCREEN_OFF);
		intentFilter.setPriority(1000);
		LockScreenReceiver receiver=new LockScreenReceiver();
		registerReceiver(receiver, intentFilter);

	}

}
