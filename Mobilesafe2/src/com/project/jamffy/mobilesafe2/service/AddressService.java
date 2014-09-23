package com.project.jamffy.mobilesafe2.service;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.android.internal.telephony.ITelephony;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.db.dao.BlackNumberDao;
import com.project.jamffy.mobilesafe2.engine.NumberAddressService;
import com.project.jamffy.mobilesafe2.ui.SignalGuardActivity;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.ContentObserver;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.provider.CallLog;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 获取来电归属地，将信息以自定义toast的形式展现
 * 
 * @author tmac
 * @date 2014-8-15 增加黑名单电话拦截功能 拦截“响一声”号码
 */
public class AddressService extends Service {
	private TelephonyManager tmanager;
	private final static String TAG = "AddressService";
	private MyPhoneListener listener;
	private WindowManager wmanager;
	private View view;
	private SharedPreferences sp;
	private BlackNumberDao dao;

	private long firstring_time;
	private long endring_time;
	private long STIME = 5000; // 设定时间间隔为5s内

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		sp = getSharedPreferences("config", Context.MODE_PRIVATE);
		dao = new BlackNumberDao(this);
		wmanager = (WindowManager) this.getSystemService(WINDOW_SERVICE); // 得到窗体对象
		// 注册系统电话管理服务的监听器
		listener = new MyPhoneListener();
		tmanager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
		tmanager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
	}

	private class MyPhoneListener extends PhoneStateListener {

		// 电话状态改变时调用的方法
		@Override
		public void onCallStateChanged(int state, String incomingNumber) {
			super.onCallStateChanged(state, incomingNumber);
			switch (state) {
			case TelephonyManager.CALL_STATE_IDLE: // 静止状态
				endring_time = System.currentTimeMillis();
				if (endring_time > firstring_time
						&& (endring_time - firstring_time) < STIME) {
					Logger.i(TAG, "响一声电话");
					// 弹出notification 需要把该号码添加到黑名单中
					showNotification(incomingNumber);
				}
				if (view != null) {
					wmanager.removeView(view);
					view = null;
				}
				break;
			case TelephonyManager.CALL_STATE_RINGING: // 响铃状态
				firstring_time = System.currentTimeMillis();

				Logger.i(TAG, "来电号码为：" + incomingNumber);
				// 将号码与黑名单数据库比对 //测试一下数据库不关会不会报错--不关也不会报错
				if (dao.find(incomingNumber)) {
					Logger.i(TAG, "一个黑名单来电:" + incomingNumber);
					// 挂断电话
					endcall();
					// 删除该通话记录
					// deleteCallLog(incomingNumber);
					// 给通话记录的uri注册观察者，当通话记录change的时候再删除
					getContentResolver().registerContentObserver(
							CallLog.Calls.CONTENT_URI, true,
							new MyObserver(new Handler(), incomingNumber));

				} else {
					Logger.i(TAG, "正常来电:" + incomingNumber);
				}

				String address = NumberAddressService
						.getAddress(incomingNumber);
				Logger.i(TAG, "号码归属地为：" + address);
				showLocastion(address);
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK: // 接通电话状态
				if (view != null) {
					wmanager.removeView(view);
					view = null;
				}
				break;

			}
		}

		private class MyObserver extends ContentObserver{
			private String incomingNumber;

			public MyObserver(Handler handler, String incomingNumber) {
				super(handler);
				this.incomingNumber = incomingNumber;
			}

			@Override
			public void onChange(boolean selfChange) {
				super.onChange(selfChange);
				deleteCallLog(incomingNumber);
				// 释放观察者
				getContentResolver().unregisterContentObserver(this);
			}

		}

		/**
		 * 删除黑名单通话记录
		 * 
		 * @param incomingNumber
		 *            黑名单号码
		 */
		private void deleteCallLog(String incomingNumber) {
			ContentResolver resolver = getContentResolver();
			Cursor cursor = resolver.query(CallLog.Calls.CONTENT_URI, null,
					"number=?", new String[] { incomingNumber }, null);
			if (cursor.moveToFirst()) { // 查询到了呼叫记录
				String id = cursor.getString(cursor.getColumnIndex("_id"));
				resolver.delete(CallLog.Calls.CONTENT_URI, "_id=?",
						new String[] { id });
			}
		}

		/**
		 * 挂断电话
		 */
		private void endcall() {
			try {
				Method method = Class.forName("android.os.ServiceManager") // 通过反射获取其他进程中的方法
						.getMethod("getService", String.class);
				IBinder binder = (IBinder) method.invoke(null,
						new Object[] { TELEPHONY_SERVICE });
				ITelephony telephony = ITelephony.Stub.asInterface(binder);
				telephony.endCall();
			} catch (Exception e) {
				e.printStackTrace();
			}

		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		tmanager.listen(listener, PhoneStateListener.LISTEN_NONE);
		listener = null;
	}

	/**
	 * 以类似toast的形式显示位置信息
	 * 
	 * @param address
	 */
	private void showLocastion(String address) {
		WindowManager.LayoutParams params = new LayoutParams();
		params.height = WindowManager.LayoutParams.WRAP_CONTENT;
		params.width = WindowManager.LayoutParams.WRAP_CONTENT;
		params.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
				| WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE
				| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
		params.format = PixelFormat.TRANSLUCENT;

		params.type = WindowManager.LayoutParams.TYPE_TOAST;
		params.setTitle("Toast");
		params.gravity = Gravity.LEFT | Gravity.TOP;
		int lastx = sp.getInt("lastx", 0);
		int lasty = (int) sp.getInt("lasty", 0);
		params.x = lastx;
		params.y = lasty;
		view = View.inflate(getApplicationContext(), R.layout.show_location,
				null);
		LinearLayout ll = (LinearLayout) view.findViewById(R.id.ll_location);
		long background = sp.getLong("adress_toast_bg", 0);
		if (background == 0) {
			ll.setBackgroundResource(R.drawable.call_locate_gray);
		} else if (background == 1) {
			ll.setBackgroundResource(R.drawable.call_locate_orange);
		} else if (background == 2) {
			ll.setBackgroundResource(R.drawable.call_locate_green);
		}
		TextView tv = (TextView) view.findViewById(R.id.tv_show_location);
		tv.setText(address);
		tv.setTextSize(24);
		wmanager.addView(view, params);
	}

	/**
	 * 创建并显示“响一声”号码被拦截时弹出的 用户提醒
	 * 
	 * @param incomingNumber
	 */
	private void showNotification(String incomingNumber) {
		// 1. 获取notification的管理服务
		NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		// 2 把一个要想显示的notification 对象创建出来
		Notification notification = new Notification(
				R.drawable.block_calllist_missed, "拦截一条响一声电话",
				System.currentTimeMillis());
		// 3 .配置notification的一些参数
		Context context = getApplicationContext();
		String contentTitle = "拦截响一声电话";
		String contentText = "拦截号码:" + incomingNumber;
		notification.flags = Notification.FLAG_AUTO_CANCEL;
		Intent notificationIntent = new Intent(this, SignalGuardActivity.class);

		// 把响一声的号码 设置到intent对象里面
		notificationIntent.putExtra("beingblacknumber", incomingNumber);
		PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
				notificationIntent, 0);

		notification.setLatestEventInfo(context, contentTitle, contentText,
				contentIntent);

		// 4. 通过manger把notification 激活
		manager.notify(0, notification);
	}
}
