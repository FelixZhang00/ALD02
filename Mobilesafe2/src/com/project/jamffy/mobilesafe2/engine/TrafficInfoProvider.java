package com.project.jamffy.mobilesafe2.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.net.TrafficStats;

import com.project.jamffy.mobilesafe2.domain.TrafficInfo;

public class TrafficInfoProvider {

	private Context context;
	private PackageManager pm;

	public TrafficInfoProvider(Context context) {
		super();
		this.context = context;
		pm = context.getPackageManager();
	}

	/**
	 * 返回所有 具有internet访问权限的app的流量信息
	 * 流量信息在adapter中实现，方便实时更新
	 * @return
	 */
	public List<TrafficInfo> getTrafficInfos() {
		// 获取到配置权限信息的应用程序
		List<PackageInfo> packageInfos = pm
				.getInstalledPackages(PackageManager.GET_PERMISSIONS);
		// 存放具有Internet权限信息的应用
		List<TrafficInfo> trafficInfos = new ArrayList<TrafficInfo>();
		for (PackageInfo packageInfo : packageInfos) {
			// 获取该应用的所有权限信息
			String[] permissions = packageInfo.requestedPermissions;
			if (permissions != null && permissions.length > 0) {
				for (String permission : permissions) {
					// 筛选出具有Internet权限的应用程序
					if (permission.equals("android.permission.INTERNET")) {
						// 用于封装具有Internet权限的应用程序信息
						TrafficInfo trafficInfo = new TrafficInfo();
						trafficInfo.setPackname(packageInfo.packageName);
						trafficInfo.setAppname(packageInfo.applicationInfo
								.loadLabel(pm).toString());
						trafficInfo.setIcon(packageInfo.applicationInfo
								.loadIcon(pm));

						// 准备获取流量信息

						// 获取到应用的uid（user id）
						int uid = packageInfo.applicationInfo.uid;

						// 获取所有app总的流量
						// long mobiletotal = TrafficStats.getMobileRxBytes()
						// + TrafficStats.getMobileTxBytes();
						// trafficInfo.setMobiletotal(mobiletotal);
						// long wifitotal = TrafficStats.getTotalRxBytes()
						// + TrafficStats.getTotalTxBytes() - mobiletotal;
						// trafficInfo.setWifitotal(wifitotal);

						// TrafficStats对象通过应用的uid来获取应用的下载、上传流量信息
						// long rx = TrafficStats.getUidRxBytes(uid);
						// long tx = TrafficStats.getUidTxBytes(uid);
						
						trafficInfos.add(trafficInfo);
						trafficInfo = null;
						break; // 跳出检索 权限信息的循环
					}
				}

			}
		}

		return trafficInfos;
	}
	
	

	/**
	 * @deprecated 已经注释掉了
	 * @return
	 */
	public List<TrafficInfo> getAllInfo() {
		// PackageManager pm = context.getPackageManager();
		// Intent intent = new Intent();
		// // 找到在launch界面上有入口的应用
		// intent.setAction("android.intent.action.MAIN");
		// intent.addCategory("android.intent.category.LAUNCHER");
		// List<ResolveInfo> resolveInfos = pm.queryIntentActivities(intent,
		// PackageManager.MATCH_DEFAULT_ONLY);
		return null;
	}

}
