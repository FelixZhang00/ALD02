package com.project.jamffy.mobilesafe2.receiver;

import com.project.jamffy.mobilesafe2.ui.LostProtectdActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class CallPhoneReceiver extends BroadcastReceiver {

	private String TAG="CallPhoneReceiver";

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(TAG, "只有按下拨号键才能接受到消息");
		String number = getResultData();
		if("20142014".equals(number)){
			Intent lostIntent = new Intent(context, LostProtectdActivity.class);
			//指定要激活的activity在自己的任务栈里运行
			lostIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			context.startActivity(lostIntent);
			//终止掉这个电话
			setResultData(null);
		}
	}

}
