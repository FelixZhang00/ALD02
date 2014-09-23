package com.project.jamffy.mobilesafe2.engine;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import com.project.jamffy.mobilesafe2.utils.Logger;

/**
 * @author tmac 保证这个类只存在一个实例 目的在于 减少往系统服务注册监听，避免程序挂掉 ，减少耗电量
 */
public class GPSInfoProvider {
	private static Context context;
	private static LocationManager manager;
	private static GPSInfoProvider mGpsInfoProvider;
	private static MyLocationListener listener;// 位置变化的监听器。监听动作比较耗电
	private static SharedPreferences sp;// 持久化位置的信息（经纬度）

	// 1.私有化构造方法
	private GPSInfoProvider() {
	}

	// 2.提供一个静态方法可以返回它的一个实例
	public static synchronized GPSInfoProvider getInstance(Context context) {
		if (mGpsInfoProvider == null) {
			mGpsInfoProvider = new GPSInfoProvider();
			GPSInfoProvider.context = context;
			sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
		}
		return mGpsInfoProvider;
	}

	private synchronized MyLocationListener getListener() {
		if (listener == null) {
			listener = new MyLocationListener();
		}
		return listener;
	}

	/**
	 * 获取gps信息
	 * @return
	 */
	public String getLocation() {
		 manager = (LocationManager) context
				.getSystemService(Context.LOCATION_SERVICE);
		String provider = getProvider(manager);
		// 注册位置监听器
		manager.requestLocationUpdates(provider, 60000, 50, getListener());
		String lastlocation= sp.getString("last_location", ""); //获取最后一次的位置信息
		return lastlocation;
	}
	
	public void stopGPSListen(){
		manager.removeUpdates(getListener());
	} 

	private class MyLocationListener implements LocationListener {

		private String TAG="MyLocationListener";

		// 当手机的位置发生改变的时候 调用的方法
		@Override
		public void onLocationChanged(Location location) {
			String latitude = "latitude:" + location.getLatitude(); // 纬度
			String longitude = "longitude:" + location.getLongitude();// 经度
			String meter = "accuracy:" + location.getAccuracy(); // 精确度
//			System.out.println(latitude + "--" + longitude + "--" + meter);

			Editor editor = sp.edit();
			editor.putString("last_location", latitude + "--" + longitude
					+ "--" + meter);
			editor.commit();
		}

		// 当位置提供者 状态发生改变的时候 调用的方法
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {

		}

		// 当某个位置提供者可用的时候.
		@Override
		public void onProviderEnabled(String provider) {

		}

		// 当某个位置提供者 不可用的时候
		@Override
		public void onProviderDisabled(String provider) {
			Logger.i(TAG, "位置提供者不可用");
		}
	}

	/**
	 * 
	 * @param manager
	 *            位置管理服务
	 * @return 最好的位置提供者
	 */
	private String getProvider(LocationManager manager) {
		// 获取查询地理位置的查询条件对象（内部是一个Map集合）
		Criteria criteria = new Criteria();
		// 设置精确度，这里传递的是最精准的精确度
		criteria.setAccuracy(Criteria.ACCURACY_FINE);
		// gps定位是否允许产生开销（true表示允许，例如好用流量）
		criteria.setCostAllowed(true);
		// 手机的功耗消耗情况（实时定位时，应该设置为高）
		criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
		// 获取海拔信息
		criteria.setAltitudeRequired(false);
		// 对手机的移动的速度是否敏感
		criteria.setSpeedRequired(true);
		// 获取当前环境中最好的位置提供者。3g  or wifi
		return manager.getBestProvider(criteria, true); // 只使用打开设备
	}

}
