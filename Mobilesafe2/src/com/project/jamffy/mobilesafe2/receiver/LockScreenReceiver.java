package com.project.jamffy.mobilesafe2.receiver;

import com.project.jamffy.mobilesafe2.utils.TaskUitl;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class LockScreenReceiver extends BroadcastReceiver {

	private final static String TAG = "LockScreenReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(TAG, "接受到锁屏广播");
		SharedPreferences sp = context.getSharedPreferences("config",
				Context.MODE_PRIVATE);
		boolean is_clean = sp.getBoolean("is_clean_pss_ts", false);
		if (is_clean) {
			TaskUitl.killAllProcess(context);
		}

	}

}
