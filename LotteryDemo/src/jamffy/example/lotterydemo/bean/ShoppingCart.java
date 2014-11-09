package jamffy.example.lotterydemo.bean;

import jamffy.example.lotterydemo.GlobalParams;

import java.util.ArrayList;
import java.util.List;

/**
 * 购物车
 * 
 * @author tmac
 *
 */
public class ShoppingCart {

	// 投注
	// @deprecated: lotterycode string * 投注号码，注与注之间^分割
	// 用list取代
	// issueflag int * 是否多期追号 0否，1多期
	// bonusstop int * 中奖后是否停止：0不停，1停

	/**
	 * 玩法编号
	 */
	private Integer lotteryid;

	/**
	 * 期号（当前销售期）
	 */
	private String issue;

	/**
	 * 投注号码
	 */
	private List<Ticket> tickets = new ArrayList<Ticket>();
	/**
	 * 注数
	 */
	private Integer lotterynumber;
	/**
	 * 方案金额，以分为单位
	 */
	private Integer lotteryvalue;

	/**
	 * 倍数
	 */
	private Integer appnumbers = 1;
	/**
	 * 追期
	 */
	private Integer issuesnumbers = 1;

	// 希望每个用户只有一辆购物车
	// 单例设计模式
	private ShoppingCart() {
		super();
	}

	private static ShoppingCart shoppingCart;

	public static ShoppingCart getInstance() {
		if (shoppingCart == null) {
			shoppingCart = new ShoppingCart();
		}
		return shoppingCart;
	}

	public Integer getLotteryid() {
		return lotteryid;
	}

	public void setLotteryid(Integer lotteryid) {
		this.lotteryid = lotteryid;
	}

	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public List<Ticket> getTickets() {
		return tickets;
	}

	public Integer getLotterynumber() {
		lotterynumber = 0;
		for (Ticket item : tickets) {
			lotterynumber += item.getNum();
		}
		return lotterynumber;
	}

	public Integer getLotteryvalue() {
		lotteryvalue = 2 * getLotterynumber() * appnumbers * issuesnumbers;
		return lotteryvalue;
	}

	public Integer getAppnumbers() {
		return appnumbers;
	}

	public Integer getIssuesnumbers() {
		return issuesnumbers;
	}

	/**
	 * 操作倍数
	 * 
	 * @param isAdd
	 *            true :增加倍数 ； false:减少倍数
	 * @return true 合法的倍数(1~99倍、不超过余额)
	 */
	public boolean addAppnumbers(boolean isAdd) {
		if (isAdd) {
			appnumbers++;
			if (appnumbers > 99) {
				appnumbers--;
				return false;
			}
			if (lotteryvalue > GlobalParams.USER_BALANCE) {
				appnumbers--;
				return false;
			}

		} else {
			appnumbers--;
			if (appnumbers == 0) {
				appnumbers++;
				return false;
			}
		}
		return true;
	}

	/**
	 * 操作追期
	 * 
	 * @param b
	 * @return
	 */
	public boolean addIssuesnumbers(boolean isAdd) {
		if (isAdd) {
			issuesnumbers++;
			if (issuesnumbers > 99) {
				issuesnumbers--;
				return false;
			}
			if (lotteryvalue > GlobalParams.USER_BALANCE) {
				issuesnumbers--;
				return false;
			}

		} else {
			issuesnumbers--;
			if (issuesnumbers == 0) {
				issuesnumbers++;
				return false;
			}
		}
		return true;
	}

	public void clear() {
		tickets.clear();
		lotterynumber = 0;
		lotteryvalue = 0;
		appnumbers = 1;
		issuesnumbers = 1;
	}

}
