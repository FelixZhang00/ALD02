package com.project.jamffy.mobilesafe2.service;

import java.util.Timer;
import java.util.TimerTask;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.R.color;
import com.project.jamffy.mobilesafe2.receiver.LockScreenReceiver;
import com.project.jamffy.mobilesafe2.ui.MainActivity;
import com.project.jamffy.mobilesafe2.utils.TaskUitl;
import com.project.jamffy.mobilesafe2.utils.TextFormatUtil;

import android.app.PendingIntent;
import android.app.Service;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.graphics.Color;
import android.os.IBinder;
import android.widget.RemoteViews;

public class UpdateWidgetService extends Service {

	private Timer timer;
	private TimerTask task;
	private AppWidgetManager widgetManager;

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// 1、获取appwidget包管理的实例
		// 2、开启一个倒计时
		// 3、准备 远程views对象、设置好上面的数据和点击事件
		// 4、在widget包上调用update..方法
		// 5、给倒计时规划任务

		timer = new Timer();
		widgetManager = AppWidgetManager.getInstance(getApplicationContext());
		task = new TimerTask() {

			@Override
			public void run() {
				RemoteViews views = new RemoteViews(
						"com.project.jamffy.mobilesafe2",
						R.layout.process_widget);
				views.setTextViewText(
						R.id.process_count,
						"进程数目:"
								+ TaskUitl
										.getProcessCount(getApplicationContext()));
				views.setTextColor(R.id.process_count, Color.BLACK);
				views.setTextViewText(
						R.id.process_memory,
						"可用内存:"
								+ TextFormatUtil.byteFormater(TaskUitl
										.getAvailMem(getApplication())));
				views.setTextColor(R.id.process_memory, Color.BLACK);

				Intent intent = new Intent(UpdateWidgetService.this,
						LockScreenReceiver.class);
				PendingIntent pendingIntent = PendingIntent.getBroadcast(
						getApplicationContext(), 0, intent, 0);
				views.setOnClickPendingIntent(R.id.btn_clear, pendingIntent);

				Intent startMainIntent = new Intent(UpdateWidgetService.this,
						MainActivity.class);
				PendingIntent startMainPIntent = PendingIntent.getActivity(
						getApplicationContext(), 0, startMainIntent, 0);
				views.setOnClickPendingIntent(R.id.tv_pw_appname,
						startMainPIntent);

				ComponentName provider = new ComponentName(
						"com.project.jamffy.mobilesafe2",
						"com.project.jamffy.mobilesafe2.receiver.ProcessWidget");
				widgetManager.updateAppWidget(provider, views);

			}
		};

		timer.scheduleAtFixedRate(task, 1000, 2000);

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		timer.cancel();
		timer = null;
		task = null;
	}

}
