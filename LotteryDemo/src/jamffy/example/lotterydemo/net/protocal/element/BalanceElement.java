package jamffy.example.lotterydemo.net.protocal.element;

import org.xmlpull.v1.XmlSerializer;

import jamffy.example.lotterydemo.net.protocal.Element;

public class BalanceElement extends Element {

	/***** 处理服务器发回来的数据 *****/
	private String investvalues;

	public String getInvestvalues() {
		return investvalues;
	}

	public void setInvestvalues(String investvalues) {
		this.investvalues = investvalues;
	}

	/***** 处理服务器发回来的数据 *****/

	@Override
	public void serializerElement(XmlSerializer serializer) throws Exception {
		// TODO Auto-generated method stub

	}

	@Override
	public String getTransactionType() {
		return "11007";
	}

}
