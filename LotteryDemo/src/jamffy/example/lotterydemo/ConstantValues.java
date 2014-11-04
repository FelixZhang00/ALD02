package jamffy.example.lotterydemo;

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
	String LOTTERY_URI = "http://10.61.91.126:8080/MyZCWService/Entrance";

	
	int VIEW_FIRST=1;
	int VIEW_SECOND=2;
	 
	
}
