package jamffy.example.lotterydemo.engine;

import jamffy.example.lotterydemo.bean.User;
import jamffy.example.lotterydemo.net.protocal.Message;

/**
 * 用户相关的业务操作的接口
 * 
 * @author tmac
 *
 */
public interface UserEngine {

	/**
	 * 处理用户登录
	 * 
	 * @param user
	 * @return
	 */
	Message login(User user);

}
