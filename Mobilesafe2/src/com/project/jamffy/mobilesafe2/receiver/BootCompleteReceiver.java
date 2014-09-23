package com.project.jamffy.mobilesafe2.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;
import com.project.jamffy.mobilesafe2.utils.Logger;

/**
 * 接受sim卡发生改变时系统发送的广播，并做出发送提醒短信给安全号码的处理
 * 
 * @author tmac
 *
 */
public class BootCompleteReceiver extends BroadcastReceiver {

	private String TAG = "BootCompleteReceiver";
	private SharedPreferences sp;

	@Override
	public void onReceive(Context context, Intent intent) {
		Logger.i(TAG, "重启完毕");
		// 判断手机是否处于保护状态
//		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
//		boolean isprotecting = sp.getBoolean("isprotecting", false);
//		if (isprotecting) {
//			TelephonyManager manager = (TelephonyManager) context
//					.getSystemService(Context.TELEPHONY_SERVICE);
//			String realsim = sp.getString("sim", "");
//			String currentsim = manager.getSimSerialNumber();
//			if (!currentsim.equals(realsim)) { // sim卡串号不同
//				// 发送报警短信
//				Logger.i(TAG, "发送报警短信");
//				SmsManager smsManager = SmsManager.getDefault();
//				String destinationAddress = sp.getString("safenumber", null);
//				smsManager.sendTextMessage(destinationAddress, null,
//						"sim卡发生了改变，手机有可能被盗", null, null);
//			} else {
//				Logger.i(TAG, "手机串号没变");
//			}
//
//		}else{
//			Logger.i(TAG, "手机没有在保护状态");
//		}
	}
}
