package com.project.jamffy.mobilesafe2.service;

import java.util.ArrayList;
import java.util.List;

import com.project.jamffy.mobilesafe2.db.dao.AppLockDao;
import com.project.jamffy.mobilesafe2.ui.LockDoorActivity;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class LockAppService extends Service {

	private String TAG = "LockAppService";
	private boolean flag;
	private List<String> mTempUnlocks; // 将被成功解锁的包名，保存在此
	private List<String> lockapps;
	private AppLockDao dao;
	private MyContentObserver observer;
	private KeyguardManager km;
	private long TIMESLICE; // 循环时间

	@Override
	public IBinder onBind(Intent intent) {

		return new MyBinder();
	}

	public class MyBinder extends Binder implements IService {

		@Override
		public void callLockApps(String name) {
			if (mTempUnlocks.contains(name)) {
				mTempUnlocks.remove(name);
			}
		}

		@Override
		public void callUnLockApps(String name) {
			mTempUnlocks.add(name);
		}

	}

	@Override
	public void onCreate() {
		super.onCreate();
		flag = true;
		mTempUnlocks = new ArrayList<String>();
		dao = new AppLockDao(this);
		lockapps = dao.getAllData();
		final ActivityManager am = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
		km = (KeyguardManager) getSystemService(KEYGUARD_SERVICE);
		final Intent lockdoorIntent = new Intent();
		lockdoorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // 服务是没有任务栈的
		lockdoorIntent.setClass(this, LockDoorActivity.class);
		observer = new MyContentObserver(new Handler());
		Uri uri = Uri
				.parse("content://com.project.jamffy.mobilesafe2.provider.AppLocksProvider");
		this.getContentResolver().registerContentObserver(uri, true, observer);
		new Thread() {
			@Override
			public void run() {
				while (flag) {
					ComponentName cn = am.getRunningTasks(1).get(0).topActivity; // 得到当前栈定的activity的ComponentName
					String pagname = cn.getPackageName();
					try {
						if (km.inKeyguardRestrictedInputMode()) { // 判断屏幕是否为锁屏状态
							// 清空临时的集合
							mTempUnlocks.clear();
							// TIMESLICE=1000*60*10; //将循环时间改为 10分钟
							TIMESLICE = 1000;
						} else {
							TIMESLICE = 1000;
						}
						if (lockapps.contains(pagname)) {
							if (mTempUnlocks.contains(pagname)) {

								continue; // 跳过当次循环
							}
							// 召唤门锁
							lockdoorIntent
									.putExtra(
											"com.project.jamffy.mobilesafe2.service.packname",
											pagname);
							Logger.i(TAG, pagname);
							startActivity(lockdoorIntent);
						} else {
							// 放行
						}

						Thread.sleep(TIMESLICE); // 每秒循环一次
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					super.run();
				}
			}

		}.start();
	}

	private class MyContentObserver extends ContentObserver {

		public MyContentObserver(Handler handler) {
			super(handler);

		}

		@Override
		public void onChange(boolean selfChange) {
			super.onChange(selfChange);
			Logger.i(TAG, "data---------------changed--------");
			lockapps = dao.getAllData();
		}

	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Logger.i(TAG, "service onStartCommand");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		flag = false;
		getContentResolver().unregisterContentObserver(observer);
	}

}
