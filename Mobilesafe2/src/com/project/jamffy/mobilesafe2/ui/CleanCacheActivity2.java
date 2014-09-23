package com.project.jamffy.mobilesafe2.ui;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.project.jamffy.mobilesafe2.R;
import com.project.jamffy.mobilesafe2.domain.AppInfo;
import com.project.jamffy.mobilesafe2.ui.CtrlAppActivity.strlenComparator1;
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
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

/**
 * Use a ListView to sort app with cache conveniently.
 * 
 * @author tmac
 *
 */
public class CleanCacheActivity2 extends Activity implements
		OnItemClickListener {
	private boolean flag = true; // 由大到小为true
	protected static final String TAG = "CleanCacheActivity2";
	private ProgressBar mPb;
	private TextView mTv_status;
	private ListView mLv;
	private Button mBtn_sort;
	private PackageManager pm;
	private CleanCacheAdapter adapter;
	private ViewHolder mHolder;
	private long sumSize = 0;
	private List<String> mCachePackNames; // store package's name which has
											// cache
	private Map<String, Long> mCacheinfo; // packname cachesize

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.cleancache2);
		mPb = (ProgressBar) this.findViewById(R.id.pb_cleancache2);
		mTv_status = (TextView) this.findViewById(R.id.tv_cleancache2_status);
		mBtn_sort = (Button) this.findViewById(R.id.bt_clearcache2_sort);
		mBtn_sort.setClickable(false);
		mLv = (ListView) this.findViewById(R.id.lv_clearcache2);
		pm = this.getPackageManager();

		scanPackages();

		mLv.setOnItemClickListener(this);

	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		String packname = mCachePackNames.get(position);
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

	}

	private void initListViewUI() {
		adapter = new CleanCacheAdapter();
		mLv.setAdapter(adapter);
		mBtn_sort.setClickable(true);
	}

	private class CleanCacheAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			return mCachePackNames.size();
		}

		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View view = null;
			if (convertView == null) {
				view = View.inflate(getApplicationContext(),
						R.layout.cleancache_item, null);
				mHolder = new ViewHolder();
				mHolder.iv = (ImageView) view
						.findViewById(R.id.iv_cleancache_item_icon);
				mHolder.tv_name = (TextView) view
						.findViewById(R.id.tv_cleancache_item_name);
				mHolder.tv_size = (TextView) view
						.findViewById(R.id.tv_cleancache_item_size);
				view.setTag(mHolder);
			} else {
				view = convertView;
				mHolder = (ViewHolder) view.getTag();
			}
			String pkg = mCachePackNames.get(position);
			String appName = getAppName(pkg);
			Drawable drawable = getAppIcon(pkg);
			long size = mCacheinfo.get(pkg);

			mHolder.iv.setImageDrawable(drawable);
			mHolder.tv_name.setText(appName);
			mHolder.tv_size.setText(TextFormatUtil.byteFormater(size));
			return view;
		}

	}

	private static class ViewHolder {
		ImageView iv;
		TextView tv_name;
		TextView tv_size;
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
					// 50ms is just right.
					try {
						Thread.sleep(50);
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
				initListViewUI();
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
		if (flag) { // 起先的 由大到小 排列 改为 由小到大
			Collections.sort(mCachePackNames, new strlenComparator1());
			flag = false;
			Toast.makeText(getApplicationContext(), "由小到大排列", 0).show();
		} else {
			Collections.sort(mCachePackNames, new strlenComparator2());
			flag = true;
			Toast.makeText(getApplicationContext(), "由大到小排列", 0).show();
		}
		adapter.notifyDataSetChanged();
	}

	/**
	 * 定义比较器 由小到大
	 * 
	 * @author tmac
	 *
	 */
	class strlenComparator1 implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			String size1 = mCacheinfo.get(lhs) + "";
			Integer valueOf1 = Integer.valueOf(size1);

			String size2 = mCacheinfo.get(rhs) + "";
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
	class strlenComparator2 implements Comparator<String> {

		@Override
		public int compare(String lhs, String rhs) {
			String size1 = mCacheinfo.get(lhs) + "";
			Integer valueOf1 = Integer.valueOf(size1);

			String size2 = mCacheinfo.get(rhs) + "";
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
