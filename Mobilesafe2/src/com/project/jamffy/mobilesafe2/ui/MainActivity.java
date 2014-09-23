package com.project.jamffy.mobilesafe2.ui;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.adapter.MainAdapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends Activity implements OnItemClickListener,
		OnItemLongClickListener {
	private GridView gv_main;
	private String TAG = "MainActivity";
	// 用来持久化一些配置信息
	private SharedPreferences sp;
	private boolean mBackKeyPressedTimes = false; // false 不允许退出

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.mainscreen);
		gv_main = (GridView) this.findViewById(R.id.gv_main);
		sp = this.getSharedPreferences("config", Context.MODE_PRIVATE);
		gv_main.setAdapter(new MainAdapter(this));
		gv_main.setOnItemClickListener(this);
		// 设置长按监听器
		gv_main.setOnItemLongClickListener(this);
	}

	/*
	 * 当gridview的条目被点击时对应的 回调 parent ：gridview view：当前被点击的条目 LinearLayout
	 * position：点击条目对应的位置 id：单击GridView的第9项，则id为8
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		Logger.i(TAG, "当前点击的位置：" + position);
		switch (position) {
		case 0:
			Logger.i(TAG, "进入手机防盗");
			Intent lostIntent = new Intent(MainActivity.this,
					LostProtectdActivity.class);
			startActivity(lostIntent);
			break;
		case 1:
			Logger.i(TAG, "进入通信卫士");
			Intent signal_guardIntent = new Intent(MainActivity.this,
					SignalGuardActivity.class);
			startActivity(signal_guardIntent);
			break;
		case 2:
			Logger.i(TAG, "进入软件管理");
			Intent ctrlappIntent = new Intent(MainActivity.this,
					CtrlAppActivity.class);
			startActivity(ctrlappIntent);
			break;
		case 3:
			Logger.i(TAG, "进入进程管理");
			Intent ctrltastIntent = new Intent(MainActivity.this,
					CtrlTaskActivity.class);
			startActivity(ctrltastIntent);
			break;
		case 4:
			Intent traffficIntent = new Intent(MainActivity.this,
					TrafficInfoActivity.class);
			startActivity(traffficIntent);
			break;
		case 5:
			Intent antivirusIntent = new Intent(MainActivity.this,
					AntiVirusActivity.class);
			startActivity(antivirusIntent);
			break;
		case 6:
			Intent clearIntent = new Intent(MainActivity.this,
					ClearActivity.class);
			startActivity(clearIntent);
			break;
		case 7:
			Logger.i(TAG, "进入高级工具");
			Intent atoolsIntent = new Intent(MainActivity.this,
					AToolsActivity.class);
			startActivity(atoolsIntent);
			break;
		case 8:
			Logger.i(TAG, "进入设置中心");
			Intent settingIntent = new Intent(MainActivity.this,
					SettingCenterActivity.class);
			startActivity(settingIntent);
			break;

		default:
			break;
		}
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, final View view,
			int position, long id) {
		// 长按修改功能一的名字
		if (position == 0) {
			AlertDialog.Builder builder = new Builder(MainActivity.this);
			builder.setTitle("您可以修改 手机防盗的名称");
			builder.setMessage("请输入更改的名称");
			final EditText ed = new EditText(MainActivity.this);
			builder.setView(ed);
			builder.setPositiveButton("确定", new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String name = ed.getText().toString().trim();
					if ("".equals(name)) {
						Toast.makeText(getApplicationContext(), "内容不能为空", 0)
								.show();
						return;
					} else {
						Editor editor = sp.edit();
						editor.putString("lost_name", name);
						// 完成数据的提交
						editor.commit();
						TextView tv = (TextView) view
								.findViewById(R.id.tv_mainscreen_item_name);
						tv.setText(name);
					}
				}
			});
			builder.setNegativeButton("取消", new OnClickListener() {
				// 什么都不做
				@Override
				public void onClick(DialogInterface dialog, int which) {
				}
			});
			builder.create().show();
		}
		return false;
	}

	// 按返回键后调用的方法
	@Override
	public void onBackPressed() {
		if (!mBackKeyPressedTimes) {
			Toast.makeText(getApplicationContext(), "再按一次退出应用",
					Toast.LENGTH_SHORT).show();
			mBackKeyPressedTimes = true;
			new Thread() {
				@Override
				public void run() {
					try {
						Thread.sleep(2000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					} finally {
						mBackKeyPressedTimes = false;
					}
					super.run();
				}
			}.start();
			return; // 在打开上面的新线程的同时，已经return了
		} else {
			finish();
		}
		super.onBackPressed(); // 抢在调用父类方法之前
	}
}
