package jamffy.example.lotterydemo.util;

import java.io.IOException;
import java.util.Properties;

import jamffy.example.lotterydemo.engine.UserEngine;

/**
 * 工厂类
 * 
 * @author tmac
 *
 */
public class BeanFactory {
	private static Properties properties;
	// 我希望下面的代码只执行一次
	// 加载src目录下的配置文件到对象（内存）
	static {
		properties = new Properties();
		try {
			properties.load(BeanFactory.class.getClassLoader()
					.getResourceAsStream("bean.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 加载需要的类名
	 * 
	 * @param clazz
	 * @return
	 */
	public static <T> T getImp(Class<T> clazz) {
		if (properties != null) {
			// 得到接口类名的最后部分（因为在配置文件中，我就是以接口最后部分为key的）
			String key = clazz.getSimpleName();
			// 在配置文件中找到具体实现类的完整类名
			String className = properties.getProperty(key);
			try {
				// 根据类名得到类的一个实例
				return (T) Class.forName(className).newInstance();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}
