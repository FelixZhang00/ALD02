package jamffy.example.lotterydemo.engine;

import jamffy.example.lotterydemo.net.protocal.Message;

/**
 * 公共业务操作接口
 * 
 * @author tmac
 *
 */
public interface CommInfoEngine {

	/**
	 * 获取当前彩票销售信息
	 * 
	 * @param integer
	 *            ：彩种的标示
	 * @return
	 */
	Message getCurrentIssueInfo(Integer lotteryid);
}
