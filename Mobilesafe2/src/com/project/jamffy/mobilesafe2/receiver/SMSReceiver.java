package com.project.jamffy.mobilesafe2.receiver;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.db.dao.BlackNumberDao;
import com.project.jamffy.mobilesafe2.engine.GPSInfoProvider;

import android.app.admin.DevicePolicyManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.webkit.WebView.FindListener;

/**
 * 短信接受者
 * 
 * @author tmac
 * @date 2014-8-15 新增黑名单短信拦截
 */
public class SMSReceiver extends BroadcastReceiver {

	private String TAG = "SMSReceiver";
	private BlackNumberDao dao;

	@Override
	public void onReceive(Context context, Intent intent) {
		dao = new BlackNumberDao(context);
		// 获取短信的内容
		// #*location*#q
		Object[] pdus = (Object[]) intent.getExtras().get("pdus");
		for (Object pdu : pdus) {
			SmsMessage sms = SmsMessage.createFromPdu((byte[]) pdu);
			String content = sms.getMessageBody();
			Logger.i(TAG, "短信内容：" + content);
			String sender = sms.getOriginatingAddress();
			if (content.equals("#*location*#q")) { // 获取位置信息
				abortBroadcast();
				GPSInfoProvider provider = GPSInfoProvider.getInstance(context);
				String location = provider.getLocation();
				// String location="mo-ren";
				SmsManager smsManager = SmsManager.getDefault();
				if (location.trim().equals("")) {
					Logger.i(TAG, "location为空");
				} else {
					smsManager.sendTextMessage(sender, null, location, null,
							null);
				}
			} else if (content.equals("#*locknow*#q")) { // 锁屏
				abortBroadcast();
				// 获取超级设备员包
				DevicePolicyManager manager = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.resetPassword("123", 0); // 设置锁屏密码
				manager.lockNow(); // 锁屏
			} else if ("#*wipedata*#q".equals(content)) { // 恢复出厂设置
				DevicePolicyManager manager = (DevicePolicyManager) context
						.getSystemService(Context.DEVICE_POLICY_SERVICE);
				manager.wipeData(0);
				abortBroadcast();
			} else if (content.equals("#*alarm*#q")) { // 报警
				abortBroadcast();
				MediaPlayer player = MediaPlayer.create(context, R.raw.ylzs);
				player.setVolume(1.0f, 1.0f); // 设置声音
				player.start();
			}

			if (dao.find(sender)) {
				Logger.i(TAG, "黑名单短信拦截");
				abortBroadcast();
				//todo: 把短信内容存放到自己的数据库里面
			}
			
			//垃圾短信的简单实现 建立短信内容的匹配库 (关键字: 发票,卖房,办证...)
			if(content.contains("fapiao")){
				Logger.i(TAG,"垃圾短信 发票");
				abortBroadcast();
			}

		}

	}

}
