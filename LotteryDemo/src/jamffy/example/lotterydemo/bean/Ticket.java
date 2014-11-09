package jamffy.example.lotterydemo.bean;

/**
 * 投注信息：红球、蓝球、注数
 * 
 * @author tmac
 *
 */
public class Ticket {
	private String redNum;
	private String blueNum;

	private int num;// 注数

	public String getRedNum() {
		return redNum;
	}

	public void setRedNum(String redNum) {
		this.redNum = redNum;
	}

	public String getBlueNum() {
		return blueNum;
	}

	public void setBlueNum(String blueNum) {
		this.blueNum = blueNum;
	}

	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	// 方便观察
	@Override
	public String toString() {
		return "Ticket [redNum=" + redNum + ", blueNum=" + blueNum + ", num="
				+ num + "]";
	}

}
