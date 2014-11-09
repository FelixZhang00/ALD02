package jamffy.example.lotterydemo;

import android.util.DisplayMetrics;

/**
 * 保存一些全局参数，比如临时的设置等
 * 
 * @author tmac
 *
 */
public class GlobalParams {

	/**
	 * 代理的ip
	 */
	public static String PROXY_IP = "";
	/**
	 * 代理的端口
	 */
	public static int PROXY_PORT = 0;

	/**
	 * xml数据中的body部分
	 */
	public static String XML_BODY = "";

	/**
	 * 屏幕尺寸
	 */
	public static DisplayMetrics metrics = null;

	/**
	 * 用户是否已登录
	 */
	public static boolean isLogined = false;

	/**
	 * 用户名
	 */
	public static String USER_NAME;

	/**
	 * 用户可用余额
	 */
	public static Float USER_BALANCE = 0f;

}
