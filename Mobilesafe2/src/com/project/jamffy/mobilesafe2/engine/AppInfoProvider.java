package com.project.jamffy.mobilesafe2.engine;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.project.jamffy.mobilesafe2.domain.AppInfo;
import com.project.jamffy.mobilesafe2.utils.TextFormatUtil;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageStatsObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.RemoteException;
import android.text.format.Formatter;
import com.project.jamffy.mobilesafe2.utils.Logger;

/**
 * 得到app的名称 icon 大小
 * 
 * @author tmac
 *
 */
public class AppInfoProvider {
	private Context context;
	private String TAG = "AppInfoProvider";
	private long totalsize;
	// private String appName; // 临时实验
	private List<AppInfo> appInfos = null;

	public AppInfoProvider(Context context) {
		this.context = context;
		appInfos = new ArrayList<AppInfo>();
	}

	public List<AppInfo> getAllApps() {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		if (appInfos != null) {
			appInfos.clear();
			for (PackageInfo packageInfo : packageInfos) {
				AppInfo appInfo = new AppInfo();
				// 得到包的名字
				String packageName = packageInfo.packageName;
				// 获得app图标
				Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
				// 得到app名字
				String appName = (String) packageInfo.applicationInfo
						.loadLabel(pm);
				// 这种方法获得的app大小并不准确
				// // 得到app的路径
				// String appPath = packageInfo.applicationInfo.sourceDir;
				// // 得到app大小
				// File file = new File(appPath);
				// long length = file.length();

				// // 通过反射来获取app大小
				// try {
				// queryPacakgeSize(packageName, pm);
				// appInfo.setAppsize(formateFileSize(totalsize));
				// appInfo.setAppLength(totalsize + "");
				// } catch (Exception e) {
				// e.printStackTrace();
				// }

				setSize(packageName, appInfo);
				try {
					Thread.sleep(25);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				appInfo.setAppname(appName);
				// appInfo.setAppsize(length + "");
				appInfo.setIcon(icon);
				appInfo.setPackname(packageName);
				if (filterApp(packageInfo.applicationInfo)) {
					appInfo.setSystemApp(false);
				} else {
					appInfo.setSystemApp(true);
				}

				// 应用程序的标志，可以是任意标志的组合
				// int flags = packageInfo.applicationInfo.flags;
				// System.out.println(appInfo);

				long appLength = appInfo.getAppLength();
				appInfos.add(appInfo);
			}

		}

		return appInfos;
	}

	/**
	 * 因为内含异步操作。 实践证明把整个方法写成同步代码块也没有用 设置 appinfo 的缓存大小
	 * 
	 * @param packname
	 * @param appInfo
	 */
	public void setSize(final String packname, final AppInfo appInfo) {
		PackageManager pm = context.getPackageManager();

		try {
			Method method = PackageManager.class.getMethod(
					"getPackageSizeInfo", new Class[] { String.class,
							IPackageStatsObserver.class });
			method.invoke(pm, new Object[] { packname,
					new IPackageStatsObserver.Stub() {
						// 注意！ 这个操作是一个异步的操作
						@Override
						public void onGetStatsCompleted(PackageStats pStats,
								boolean succeeded) throws RemoteException {
							// From system's setting app,we can see system
							// distinguish between storage and cache.

							// long cacheSize = pStats.cacheSize;
							long codeSize = pStats.codeSize;
							long dataSize = pStats.dataSize;
							// System.out.println("cacheSize" + cacheSize);
							// System.out.println("codeSize" + codeSize);
							// System.out.println("dataSize" + dataSize);
							long result = codeSize + dataSize;
							appInfo.setAppLength(result);
							appInfo.setAppsize(TextFormatUtil
									.byteFormater(result));

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

	public List<AppInfo> getUserApps() {
		PackageManager pm = context.getPackageManager();
		List<PackageInfo> packageInfos = pm.getInstalledPackages(0);
		if (appInfos != null) {
			appInfos.clear();
			for (PackageInfo packageInfo : packageInfos) {
				if (filterApp(packageInfo.applicationInfo)) {
					// 是用户级应用

					AppInfo appInfo = new AppInfo();
					// 得到包的名字
					String packageName = packageInfo.packageName;
					// 获得app图标
					Drawable icon = packageInfo.applicationInfo.loadIcon(pm);
					// 得到app名字
					String appName = (String) packageInfo.applicationInfo
							.loadLabel(pm);
					// 这种方法获得的app大小并不准确
					// // 得到app的路径
					// String appPath = packageInfo.applicationInfo.sourceDir;
					// // 得到app大小
					// File file = new File(appPath);
					// long length = file.length();

					// 通过反射来获取app大小
					// try {
					// queryPacakgeSize(packageName, pm);
					// appInfo.setAppsize(formateFileSize(totalsize));
					// appInfo.setAppLength(totalsize + "");
					// } catch (Exception e) {
					// e.printStackTrace();
					// }
					setSize(packageName, appInfo);
					try {
						Thread.sleep(15);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					appInfo.setAppname(appName);
					// appInfo.setAppsize(length + "");
					appInfo.setIcon(icon);
					appInfo.setPackname(packageName);
					appInfo.setSystemApp(false);
					appInfos.add(appInfo);
				} else {
					// 不是用户级app，就不要管了

				}
				// 应用程序的标志，可以是任意标志的组合
				// int flags = packageInfo.applicationInfo.flags;
				// System.out.println(appInfo);

			}
		}

		return appInfos;
	}

	/**
	 * 判断某个app是否是第三方的
	 * 
	 * @param info
	 * @return true 是第三方的
	 */
	public boolean filterApp(ApplicationInfo info) {
		if ((info.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
			return true;
		} else if ((info.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
			return true;
		}
		return false;
	}

	// 系统函数，字符串转换 long -String (kb)
	private String formateFileSize(long size) {
		return Formatter.formatFileSize(context, size);
	}

	/**
	 * 查询app的大小
	 * 
	 * @deprecated 去用 getSize（）
	 * @param pkgName
	 *            包名
	 * @throws Exception
	 */
	public void queryPacakgeSize(String pkgName, PackageManager pm)
			throws Exception {
		if (pkgName != null) {
			// 使用放射机制得到PackageManager类的隐藏函数getPackageSizeInfo
			// PackageManager pm = getPackageManager(); //得到pm对象
			try {
				// 通过反射机制获得该隐藏函数
				Method getPackageSizeInfo = pm.getClass().getDeclaredMethod(
						"getPackageSizeInfo", String.class,
						IPackageStatsObserver.class);
				// 调用该函数，并且给其分配参数 ，待调用流程完成后会回调PkgSizeObserver类的函数
				getPackageSizeInfo.invoke(pm, pkgName, new PkgSizeObserver());
			} catch (Exception ex) {
				Logger.e(TAG, "NoSuchMethodException");
				ex.printStackTrace();
				throw ex; // 抛出异常
			}
		} else {
			Logger.i(TAG, "pkgName不存在");
		}
		return;
	}

	// aidl文件形成的Bindler机制服务类
	public class PkgSizeObserver extends IPackageStatsObserver.Stub {
		/***
		 * 回调函数
		 * 
		 * @param pStatus
		 *            返回数据封装在PackageStats对象中
		 * @param succeeded
		 *            代表回调成功
		 */
		@Override
		public void onGetStatsCompleted(PackageStats pStats, boolean succeeded)
				throws RemoteException {
			long cachesize = pStats.cacheSize; // 缓存大小
			long datasize = pStats.dataSize; // 数据大小
			long codesize = pStats.codeSize; // 应用程序大小
			totalsize = cachesize + datasize + codesize;
			Logger.i(TAG, "cachesize--->" + cachesize + " datasize---->"
					+ datasize + " codeSize---->" + codesize);
		}
	}

	/**
	 * @deprecated 去用getAllApp（）
	 * @return
	 */
	public List<AppInfo> getAllApps2() {
		List<AppInfo> appInfos = new ArrayList<AppInfo>();
		PackageManager pm = context.getPackageManager(); // 获得PackageManager对象
		Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
		mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
		// 通过查询，获得所有ResolveInfo对象.
		List<ResolveInfo> resolveInfos = pm
				.queryIntentActivities(mainIntent, 0);
		// 调用系统排序 ， 根据name排序
		// 该排序很重要，否则只能显示系统应用，而不能列出第三方应用程序
		Collections.sort(resolveInfos,
				new ResolveInfo.DisplayNameComparator(pm));
		for (ResolveInfo reInfo : resolveInfos) {
			// 创建一个AppInfo对象，并赋值
			AppInfo appInfo = new AppInfo();
			String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
			String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名
			String appLabel = (String) reInfo.loadLabel(pm); // 获得应用程序的Label
			Drawable icon = reInfo.loadIcon(pm); // 获得应用程序图标
			// 为应用程序的启动Activity 准备Intent
			Intent launchIntent = new Intent();
			launchIntent.setComponent(new ComponentName(pkgName, activityName));

			appInfos.add(appInfo); // 添加至列表中
			appInfo = null;
		}

		return null;
	}

}
