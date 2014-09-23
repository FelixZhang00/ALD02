package com.project.jamffy.mobilesafe2.ui;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.adapter.CtrlAppAdapter;
import com.project.jamffy.mobilesafe2.domain.AppInfo;
import com.project.jamffy.mobilesafe2.engine.AppInfoProvider;
import com.project.jamffy.mobilesafe2.utils.DensityUtil;

import android.R.color;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.ScaleAnimation;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.PopupWindow.OnDismissListener;
import android.widget.TextView;
import android.widget.Toast;

public class CtrlAppActivity extends Activity implements OnClickListener {
	private static final int FINISH_LOAD = 10;

	protected static final String TAG = "CtrlAppActivity";
	private boolean flag = true; // 由大到小为true

	private AppInfoProvider provider;
	private List<AppInfo> appInfos;
	// private List<AppInfo> appUserInfos;
	private CtrlAppAdapter adapter;

	private TextView tv_title;
	private ListView lv_ctrlapp;
	private LinearLayout ll_load;
	private Button bt_sort;
	private LinearLayout ll_popup;
	private LinearLayout ll_delete;
	private LinearLayout ll_run;
	private LinearLayout ll_share;
	private PopupWindow mPopupWindow;

	private Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			switch (msg.what) {
			case FINISH_LOAD:
				refreshListView(true);
				break;
			default:
				break;
			}
		}
	};

	/**
	 * @param flag
	 *            false:适配器不用变，只是里面的item有变化，刷新下就可以
	 */
	private void refreshListView(boolean flag) {
		ll_load.setVisibility(View.INVISIBLE); // 先置加载控件为不可见
		if (flag == true) {
			adapter = new CtrlAppAdapter(CtrlAppActivity.this, appInfos);
			lv_ctrlapp.setAdapter(adapter);
			bt_sort.setClickable(true);
			tv_title.setClickable(true);
		}
		// 刷新界面
		adapter.notifyDataSetChanged();
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.ctrl_app);
		lv_ctrlapp = (ListView) this.findViewById(R.id.lv_ctrlapp);
		ll_load = (LinearLayout) this.findViewById(R.id.ll_ctrlapp_load);
		tv_title = (TextView) this.findViewById(R.id.tv_ctrlapp_title);
		bt_sort = (Button) this.findViewById(R.id.bt_ctrlapp_sort);
		initListViewUI(true);

		lv_ctrlapp.setOnItemClickListener(new MyItemClickListenter());
		lv_ctrlapp.setOnScrollListener(new OnScrollListener() {

			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				Logger.i(TAG, "onScrollStateChanged");
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				Logger.i(TAG, "onScroll");
				if (mPopupWindow != null) {
					popupWindowDission();
				}
			}
		});
	}

	// /**
	// * 刷新ListViewUI 前的入口方法 判断是否有必要刷新
	// */
	// private void enterInitListViewUI() {
	//
	// }

	/**
	 * 更新ListViewUI
	 * 
	 * @param flag
	 *            false:筛选用户级程序 true:筛选所有程序
	 */
	private void initListViewUI(final boolean flag) {
		ll_load.setVisibility(View.VISIBLE);
		bt_sort.setClickable(false); // 数据加载成功之前 不可点
		tv_title.setClickable(false);
		new Thread() { // 用于查找所有app的耗时操作
			@Override
			public void run() {
				super.run();
				// try {
				// Thread.sleep(200); // 在加载的时间内，按钮点了也不会报错
				// } catch (InterruptedException e) {
				// e.printStackTrace();
				// }
				provider = new AppInfoProvider(CtrlAppActivity.this);
				appInfos = null;
				if (flag) {
					Logger.i(TAG, "" + flag + "准备加载所有程序");
					// the method getAllApps() contains a async operation , so
					// some info may not setted.
					// Try to set up a AsyncTask doInBack. failed!
					// Try to modify the method setSize() in AppInfoProvider.
					setAppInfos(true);
					Message msg = Message.obtain();
					msg.what = FINISH_LOAD;
					handler.sendMessage(msg);
				} else {
					Logger.i(TAG, "" + flag + "准备加载用户级程序");
					setAppInfos(false);
					Message msg = Message.obtain();
					msg.what = FINISH_LOAD;
					handler.sendMessage(msg);
				}

			}

		}.start();
	}

	/**
	 * Accroding to which set the var appInfos
	 * 
	 * @param which
	 *            : true set All; false set User
	 */
	protected void setAppInfos(boolean which) {
		if (which) {
			appInfos = provider.getAllApps();
		} else {
			appInfos = provider.getUserApps();
		}

	}

	/**
	 * 当Activity销毁时，需要关闭PopupWindow，因为PopupWindow挂载有TextView
	 * 如果不关闭该窗体，并不影响程序的执行，但Log中会出现"AppManagerActivity has leaked window"的红色错误提示
	 */
	@Override
	protected void onDestroy() {
		if (mPopupWindow != null && mPopupWindow.isShowing()) {
			mPopupWindow.dismiss();
			mPopupWindow = null;
		}
		super.onDestroy();
	}

	/**
	 * 实现listview点击事件监听器
	 * 
	 * @author tmac
	 *
	 */
	class MyItemClickListenter implements OnItemClickListener {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// Toast.makeText(getApplicationContext(),
			// appInfos.get(position).toString(), 0).show();
			// 获取当前view对象在窗体中的位置
			int[] arrayOfInt = new int[2];
			view.getLocationInWindow(arrayOfInt);

			int x = arrayOfInt[0] + 45;
			int y = arrayOfInt[1];

			getPoputWindowInstance(position);

			// // 给父LinearLayout 设置动画
			// ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f);
			// sa.setDuration(200);
			// ll_popup.setAnimation(sa);

			// 直接给PopupWindow 设置动画 岂不更好
			mPopupWindow.setAnimationStyle(R.style.PopupAnimation);
			// mPopupWindow.showAsDropDown(view, 45, 0);
			mPopupWindow.showAtLocation(view, Gravity.NO_GRAVITY, x, y);
		}
	}

	/**
	 * 按钮点击事件
	 * 
	 * @param view
	 */
	public void btSortOnClick(View view) {

		if (mPopupWindow != null) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
		}

		// I suspect the sort operation needs some time , so I set up a new
		// worker thread. But I failed. ಥ_ಥ

		Message msg = Message.obtain();
		msg.what = FINISH_LOAD;
		if (flag) { // 起先的 由大到小 排列 改为 由小到大
			Collections.sort(appInfos, new strlenComparator1());
			flag = false;
			// Looper.prepare();
			Toast.makeText(getApplicationContext(), "由小到大排列", 0).show();
			// Looper.loop();
			handler.sendMessage(msg);
		} else { // 起先的 由小到大 排列 改为 由大到小
			Collections.sort(appInfos, new strlenComparator2());
			flag = true;
			// Looper.prepare();
			Toast.makeText(getApplicationContext(), "由大到小排列", 0).show();
			// Looper.loop();
			handler.sendMessage(msg);
		}

		// 刷新页面
		adapter.notifyDataSetChanged();

	}

	/**
	 * 标题点击事件
	 */
	public void tvShiftOnClick(View view) {
		if (mPopupWindow != null) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
			}
		}
		Resources res = getResources();
		String title = tv_title.getText().toString();
		if (title.equals(res.getString(R.string.ctrl_app_shift_title1))) {
			tv_title.setText(res.getString(R.string.ctrl_app_shift_title2));
			// 现在就要筛选所有程序了
			initListViewUI(true);
		} else {
			tv_title.setText(res.getString(R.string.ctrl_app_shift_title1));
			// 现在就要筛选用户级程序了
			initListViewUI(false);
		}
	}

	/**
	 * @param position
	 *            item在listview中的位置 得到PopupWindow实例
	 */
	public void getPoputWindowInstance(int position) {
		if (mPopupWindow != null) {
			// 如果mPopupWindow 已经init 就不需要多此一举再初始化了。
			// 不过需要把当前的显示位置取消
			popupWindowDission();
		} else {
			initPopupWindow();
		}

		// 给控件设置标签

		ll_delete.setTag(position);
		ll_run.setTag(position);
		ll_share.setTag(position);
		return;
	}

	private void popupWindowDission() {
		mPopupWindow.dismiss();
	}

	/**
	 * @param position
	 *            item在listview中的位置 创建PopupWindow
	 */
	private void initPopupWindow() {
		LayoutInflater layoutInflater = LayoutInflater
				.from(CtrlAppActivity.this);
		View pwView = layoutInflater.inflate(R.layout.ctrlapp_popw, null);
		ll_delete = (LinearLayout) pwView.findViewById(R.id.ll_uninstall);
		ll_run = (LinearLayout) pwView.findViewById(R.id.ll_move);
		ll_share = (LinearLayout) pwView.findViewById(R.id.ll_share);
		ll_popup = (LinearLayout) pwView.findViewById(R.id.ll_popup);

		ll_delete.setOnClickListener(this);
		ll_run.setOnClickListener(this);
		ll_share.setOnClickListener(this);
		// // 给父LinearLayout 设置动画 因为已经给PopupWindow设置了动画 下面就显得多余
		// ScaleAnimation sa = new ScaleAnimation(0.0f, 1.0f, 1.0f, 1.0f);
		// sa.setDuration(200);
		// ll_popup.setAnimation(sa);

		// 获取屏幕和PopupWindow的width和height
		int mScreenWidth = getWindowManager().getDefaultDisplay().getWidth();
		int mScreenHeight = getWindowManager().getDefaultDisplay().getHeight();

		mPopupWindow = new PopupWindow(pwView, DensityUtil.dip2px(
				getApplicationContext(), 240), DensityUtil.dip2px(
				getApplicationContext(), 60));
		Drawable background = new ColorDrawable(color.transparent);
		mPopupWindow.setBackgroundDrawable(background);
	}

	@Override
	public void onClick(View v) {
		int position = (Integer) v.getTag();
		AppInfo info = appInfos.get(position);
		String packName = info.getPackname();
		switch (v.getId()) {
		case R.id.ll_uninstall:
			delayPopupWindowDissmiss();
			Logger.i(TAG, "uninstall->" + packName);
			if (info.isSystemApp()) {

				Toast.makeText(getApplicationContext(), "应用程序无法卸载	", 0).show();
			} else {
				Logger.i(TAG, "uninstall");
				uninstall(packName);
			}
			break;
		case R.id.ll_move:
			delayPopupWindowDissmiss();
			Logger.i(TAG, "run->" + packName);
			// runOtherApp(packName); // 此处有无法启动那些没有入口的系统级程序
			// startAPP(packName); //至少不会报错的方法
			openApp2(packName); // 能打开大部分应用，目前最完美
			break;
		case R.id.ll_share:
			delayPopupWindowDissmiss();
			Logger.i(TAG, "share->" + packName);
			shareApp(info);
			break;

		default:
			break;
		}
	}

	private void uninstall(String packName) {
		String uri = "package:" + packName;
		Intent uninstallIntent = new Intent(Intent.ACTION_DELETE);
		uninstallIntent.setData(Uri.parse(uri));
		startActivityForResult(uninstallIntent, 0);
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 0) {
			// 刷新界面
			refreshListView(false);
			// Resources res = getResources();
			// String title = tv_title.getText().toString();
			// if (title.equals(res.getString(R.string.ctrl_app_shift_title1)))
			// {
			// // 现在就要筛选用户级程序了
			// initListViewUI(false);
			// } else if (title.equals(res
			// .getString(R.string.ctrl_app_shift_title2))) {
			// // 现在就要筛选所有程序了
			// initListViewUI(true);
			// }
		}
	}

	private void shareApp(AppInfo info) {
		Intent shartIntent = new Intent(Intent.ACTION_SEND);
		shartIntent.setType("text/plain");
		shartIntent.putExtra(Intent.EXTRA_SUBJECT, "分享");
		shartIntent.putExtra(Intent.EXTRA_TEXT,
				"Hi! 推荐你一款好玩的APP：\n" + info.getAppname() + "\n--(分享自手机安全卫士)");
		// shartIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		startActivity(shartIntent);

	}

	// 参考例子，没有实际用途
	/**
	 * 根据包名启动app 打开有些app时会报错
	 * 
	 * @param packName
	 *            包名
	 */
	private void runOtherApp(String packName) {
		Logger.i(TAG, "runOtherApp启动");
		try {
			PackageInfo packageInfo = getPackageManager().getPackageInfo(
					packName,
					PackageManager.GET_UNINSTALLED_PACKAGES
							| PackageManager.GET_ACTIVITIES);
			ActivityInfo[] activityInfo = packageInfo.activities;
			Logger.i(TAG, activityInfo.length + "");

			if (activityInfo.length > 0) {
				ActivityInfo startActivity = activityInfo[0];
				Intent intent = new Intent();
				intent.setClassName(packName, startActivity.name);
				intent.setAction(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);
				startActivity(intent);
			} else {
				Toast.makeText(getApplicationContext(), "应用程序无法启动	", 0).show();
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "应用程序无法启动	", 0).show();
		}

	}

	// 参考例子，没有实际用途
	// 最保险的方法，至少不会报错
	// 启动一个app 如果该程序不可启动（像系统自带的包，有很多是没有入口的）会返回NU LL
	public void startAPP(String appPackageName) {
		try {
			Intent intent = this.getPackageManager().getLaunchIntentForPackage(
					appPackageName);
			startActivity(intent);
		} catch (Exception e) {
			Toast.makeText(this, "无法启动", Toast.LENGTH_LONG).show();
		}
	}

	/**
	 * 参考例子，没有实际用途
	 * 
	 * @param packageName
	 */
	private void openApp(String packageName) {
		PackageInfo pi;
		try {
			pi = getPackageManager().getPackageInfo(packageName, 0);
			Intent resolveIntent = new Intent(Intent.ACTION_MAIN, null);
			resolveIntent.addCategory(Intent.CATEGORY_LAUNCHER);
			resolveIntent.setPackage(pi.packageName);
			PackageManager pm = getPackageManager();
			List<ResolveInfo> apps = pm.queryIntentActivities(resolveIntent, 0);
			ResolveInfo ri = apps.iterator().next();
			if (ri != null) {
				String packageName2 = ri.activityInfo.packageName;
				String className = ri.activityInfo.name;

				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_LAUNCHER);

				ComponentName cn = new ComponentName(packageName, className);

				intent.setComponent(cn);
				startActivity(intent);
			}
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			Toast.makeText(getApplicationContext(), "应用程序无法启动	", 0).show();
		}

	}

	// 能打开大部分应用，目前最完美
	private void openApp2(String packageName) {
		Intent intentToResolve = new Intent(Intent.ACTION_MAIN);
		// intentToResolve.addCategory(Intent.CATEGORY_LAUNCHER);
		intentToResolve.setPackage(packageName);
		ResolveInfo ri = getPackageManager()
				.resolveActivity(intentToResolve, 0);
		if (ri != null) {
			Intent intent = new Intent(intentToResolve);
			intent.setClassName(ri.activityInfo.applicationInfo.packageName,
					ri.activityInfo.name);
			intent.setAction(Intent.ACTION_MAIN);
			// intent.addCategory(Intent.CATEGORY_LAUNCHER);
			startActivity(intent);
		} else {
			Toast.makeText(this, "无法启动", 0).show();
		}

	}

	/**
	 * 延迟关闭 popupWindow 看清点击的效果
	 */
	private void delayPopupWindowDissmiss() {
		// 如果给PopupWindow设置了动画延迟，就不要新开线程了
		// new Thread() {
		// @Override
		// public void run() {
		// super.run();
		// try {
		// sleep(200);
		popupWindowDission();
		// } catch (InterruptedException e) {
		// e.printStackTrace();
		// }
		// }
		//
		// }.start();
	}

	/**
	 * 定义比较器 由小到大
	 * 
	 * @author tmac
	 *
	 */
	class strlenComparator1 implements Comparator<AppInfo> {

		@Override
		public int compare(AppInfo lhs, AppInfo rhs) {
			String size1 = lhs.getAppLength() + "";
			Integer valueOf1 = Integer.valueOf(size1);

			String size2 = rhs.getAppLength() + "";
			Integer valueOf2 = Integer.valueOf(size2);

			if (valueOf1 > valueOf2)
				return 1;
			if (valueOf2 > valueOf1)
				return -1;

			return valueOf1.compareTo(valueOf2);
		}
	}

	/**
	 * 定义比较器 由大到小
	 * 
	 * @author tmac
	 *
	 */
	class strlenComparator2 implements Comparator<AppInfo> {

		@Override
		public int compare(AppInfo lhs, AppInfo rhs) {
			String size1 = lhs.getAppLength() + "";
			Integer valueOf1 = Integer.valueOf(size1);

			String size2 = rhs.getAppLength() + "";
			Integer valueOf2 = Integer.valueOf(size2);

			if (valueOf1 > valueOf2)
				return -1;
			if (valueOf2 > valueOf1)
				return 1;

			return valueOf1.compareTo(valueOf2);
		}

	}

	@Override
	public void onBackPressed() {
		if (mPopupWindow != null) {
			if (mPopupWindow.isShowing()) {
				mPopupWindow.dismiss();
				return;
			}
		}
		super.onBackPressed(); // must placed the super on the end ,beacause we
								// can return before the super know.

	}

}
