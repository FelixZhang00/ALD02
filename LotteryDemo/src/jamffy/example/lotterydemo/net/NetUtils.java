package jamffy.example.lotterydemo.net;

import jamffy.example.lotterydemo.GlobalParams;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

/**
 * @author tmac
 *
 */
public class NetUtils {

	/**
	 * 检测当前网络类型
	 * 
	 * @return 是否有网络
	 */
	public static boolean checkNet(Context context) {
		boolean result = true;
		// wifi
		boolean wifiConnected = isConnectbyType(context,
				ConnectivityManager.TYPE_WIFI);
		// mobile
		boolean mobileConnected = isConnectbyType(context,
				ConnectivityManager.TYPE_MOBILE);

		if (!wifiConnected && !mobileConnected) {
			result = false;
		}

		// 如果Mobile在链接，判断是哪个APN被选中了
		if (mobileConnected && !wifiConnected) {
			// 获取当前联网apn的代理ip和端口
			setApnProxyInfo(context);
		}

		return result;
	}

	/**
	 * 获取当前联网apn的代理ip和端口
	 * 
	 * @param context
	 */
	private static void setApnProxyInfo(Context context) {
		ContentResolver resolver = context.getContentResolver();
		Uri PREFERRED_APN_URI = Uri
				.parse("content://telephony/carriers/preferapn");
		Cursor cursor = null;

		try {
			cursor = resolver.query(PREFERRED_APN_URI, null, null, null, null);
			if (cursor != null && cursor.getCount() >= 1) {
				if (cursor.moveToFirst()) {
					String proxy = cursor.getString(cursor.getColumnIndex("proxy"));
					int port = cursor.getInt(cursor.getColumnIndex("port"));
					GlobalParams.PROXY_IP = proxy;
					GlobalParams.PROXY_PORT = port;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			if (cursor!=null) {
				cursor.close();							
			}
		}
	}

	/**
	 * 判断网络类型
	 * 
	 * @param context
	 * @param networkType
	 * @return
	 */
	private static boolean isConnectbyType(Context context, int networkType) {
		ConnectivityManager connectivityManager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo networkInfo = connectivityManager
				.getNetworkInfo(networkType);
		if (networkInfo != null && networkInfo.isConnected()) {
			return true;
		}
		return false;
	}

}
