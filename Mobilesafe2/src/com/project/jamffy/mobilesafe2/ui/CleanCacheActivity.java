package com.project.jamffy.mobilesafe2.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.AppInfo;
import com.project.jamffy.mobilesafe2.utils.TextFormatUtil;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.PackageStats;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

/**
 * In this activity,I show those app with cache in a LinearLayout,but I cann't
 * sort them by size just like CtrlAppActivity.
 * @deprecated go to CleanCacheActivity2 which use ListView.
 * @author tmac
 *
 */
public class CleanCacheActivity extends Activity {

	protected static final String TAG = "CleanCacheActivity";
	private ProgressBar mPb;
	private TextView mTv_status;
	private LinearLayout mLL;
	private PackageManager pm;
	private long sumSize = 0;
	private List<String> mCachePackNames; // store package's name which has
											// cache
	private Map<String, Long> mCacheinfo; // packname cachesize

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cleancache);
		mPb = (ProgressBar) this.findViewById(R.id.pb_cleancache);
		mTv_status = (TextView) this.findViewById(R.id.tv_cleancache_status);
		mLL = (LinearLayout) this.findViewById(R.id.ll_cleancache);
		pm = this.getPackageManager();
		scanPackages();
	}

	/**
	 * open up a new thread to scan all packages which have cache
	 */
	private void scanPackages() {
		new AsyncTask<Void, Integer, Void>() {
			List<PackageInfo> packInfos;
			int sCachePackNum = 0; // record the number of the list of packname
									// whice have cache

			@Override
			protected void onPreExecute() {
				mCachePackNames = new ArrayList<String>();
				mCacheinfo = new HashMap<String, Long>();
				packInfos = pm.getInstalledPackages(0);
				mPb.setMax(packInfos.size());
				mTv_status.setText("开始扫描...");
				super.onPreExecute();
			}

			@Override
			protected Void doInBackground(Void... params) {
				int i = 0; // the ProgressBar's process value
				for (PackageInfo info : packInfos) {
					String packName = info.packageName; // get package name
					getSize(packName);
					i++;

					// the sleep is very import!
					// the method getSize() contain a async operation
					// onGetStatsCompleted() which needs some time to finish its
					// work,otherwise it will leave out some work.
					// 100ms is just right.
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					publishProgress(i);
				}

				return null;
			}

			@Override
			protected void onProgressUpdate(Integer... values) {
				mPb.setProgress(values[0]);
				if (mCachePackNames.size() > sCachePackNum) {
					mTv_status.setText("正在扫描   "
							+ mCachePackNames.get(sCachePackNum++));
				}
				super.onProgressUpdate(values);
			}

			@Override
			protected void onPostExecute(Void result) {
				super.onPostExecute(result);
				mTv_status.setText("扫描完毕..." + "发现有" + mCachePackNames.size()
						+ "个缓存信息" + ",总大小为"
						+ TextFormatUtil.byteFormater(sumSize));
				for (String packname : mCachePackNames) {
					initLinearLayout(packname);
				}
			}

		}.execute();
	}

	/**
	 * in charge of the work of show all app which has cache in a LinearLayout.
	 * 
	 * @param packname
	 */
	private void initLinearLayout(final String packname) {
		View childView = View.inflate(getApplicationContext(),
				R.layout.cleancache_item, null);
		childView.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// According to the version of sdk , choose the Intent to enter
				// into setting activity where the app item clicked give cache
				// info.
				if (Build.VERSION.SDK_INT >= 9) {
					Intent intent = new Intent();
					intent.setAction("android.settings.APPLICATION_DETAILS_SETTINGS");
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.setData(Uri.parse("package:" + packname));
					startActivity(intent);
				} else {
					Intent intent = new Intent();
					intent.setAction("android.intent.action.VIEW");
					intent.addCategory(Intent.CATEGORY_DEFAULT);
					intent.addCategory("android.intent.category.VOICE_LAUNCH");
					intent.putExtra("pkg", packname);
					startActivity(intent);
				}

				// Logger.i(TAG,Build.VERSION.SDK_INT+"");
			}
		});

		ImageView iv = (ImageView) childView
				.findViewById(R.id.iv_cleancache_item_icon);
		TextView tvName = (TextView) childView
				.findViewById(R.id.tv_cleancache_item_name);
		TextView tvSize = (TextView) childView
				.findViewById(R.id.tv_cleancache_item_size);

		String appName = getAppName(packname);
		Drawable drawable = getAppIcon(packname);
		long size = mCacheinfo.get(packname);
		tvName.setText(appName);
		iv.setImageDrawable(drawable);
		tvSize.setText(TextFormatUtil.byteFormater(size));
		mLL.addView(childView);
	}

	protected Drawable getAppIcon(String packname) {
		try {
			PackageInfo info = pm.getPackageInfo(packname, 0);
			return info.applicationInfo.loadIcon(pm);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return getResources().getDrawable(R.id.icon);
		}
	}

	protected String getAppName(String packname) {
		try {
			PackageInfo info = pm.getPackageInfo(packname, 0);
			return info.applicationInfo.loadLabel(pm).toString();
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return packname;
		}
	}

	/**
	 * get a app's size and store in cacheinfo map
	 * 
	 * @param packName
	 */
	protected void getSize(final String packName) {
		try {
			Method method = pm.getClass().getDeclaredMethod(
					"getPackageSizeInfo",
					new Class[] { String.class, IPackageStatsObserver.class });

			method.invoke(pm, new Object[] { packName,
					new IPackageStatsObserver.Stub() {
						// notice! this is a async operation.
						@Override
						public void onGetStatsCompleted(PackageStats pStats,
								boolean succeeded) throws RemoteException {
							long cacheSize = pStats.cacheSize;
							if (cacheSize > 0) {
								mCacheinfo.put(packName, cacheSize);
								mCachePackNames.add(packName);
								sumSize += cacheSize;
							}

						}
					} });
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

	}

	public void btnCacheSortOnClick(View view) {

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

	// public class MyObserver extends IPackageStatsObserver.Stub{
	//
	// @Override
	// public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
	// throws RemoteException {
	//
	//
	// }
	//
	// }

}
