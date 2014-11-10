package jamffy.example.lotterydemo;

import android.util.DisplayMetrics;

/**
 * 专门存放常量的接口 为什么不用类？ 省去写public static final 的麻烦
 * 
 * @author tmac
 *
 */
public interface ConstantValues {

	String ENCODING = "utf-8";
	/**
	 * 代理商id
	 */
	String AGENTER_ID = "889931";

	/**
	 * 来源
	 */
	String SOURCE = "ivr";

	/**
	 * 加密形式
	 */
	String COMPRESS = "DES";

	/**
	 * 代理商密码(.so) JNI
	 */
	public static String AGENT_PASSWORD = "9ab62a694d8bf6ced1fab6acd48d02f8";

	/**
	 * des加密用密钥
	 */
	String DES_PASSWORD = "9b2648fcdfbad80f";
	/**
	 * 服务器地址
	 */
	String LOTTERY_URI = "http://10.61.223.140:8080/MyZCWService/Entrance";

	/******* 各种界面的ID *********/
	int VIEW_FIRST = 1;
	int VIEW_SECOND = 2;
	/**
	 * 购彩大厅
	 */
	int VIEW_HALL = 10;
	/**
	 * 双色球选号界面
	 */
	int VIEW_SSQ = 15;
	/**
	 * 购物车
	 */
	int VIEW_SHOPPING = 20;
	/**
	 * 追期和倍投的设置界面
	 */
	int VIEW_PREBET = 25;
	/**
	 * 用户登录
	 */
	int VIEW_LOGIN = 30;
	/**
	 * 双色球标示
	 */
	int SSQ = 118;

	/**
	 * 每注双色球红球的个数
	 */
	int SSQ_EACH_RED = 6;

	/**
	 * 每注双色球蓝球的个数
	 */
	int SSQ_EACH_BLUE = 1;

	/**
	 * 每注双色球的价格
	 */
	int SSQ_UNIT_PRICE = 2;
	/**
	 * 服务器验证正确
	 */
	String SUCCESS = "0";

	/**
	 * 选号球的个数
	 */
	int RED_POOL_NUM = 33;
	int BLUE_POOL_NUM = 16;

}
