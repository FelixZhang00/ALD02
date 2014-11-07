package jamffy.example.lotterydemo.net.protocal.element;

import org.xmlpull.v1.XmlSerializer;

import jamffy.example.lotterydemo.net.protocal.Element;
import jamffy.example.lotterydemo.net.protocal.Leaf;

/**
 * 具体的请求：获取当前彩票期的请求
 * 
 * @author tmac
 *
 */
public class CurrentIssueElement extends Element {
	// <lotteryid>118</lotteryid>
	// <issues>1</issues>
	private Leaf lotteryid = new Leaf("lotteryid");

	// 当成常量，直接赋值
	private Leaf issues = new Leaf("issues", "1");

	public Leaf getLotteryid() {
		return lotteryid;
	}
	
	/******************服务器回复**********************/
	private String issue;
	private String lasttime;
	
	
	public String getIssue() {
		return issue;
	}

	public void setIssue(String issue) {
		this.issue = issue;
	}

	public String getLasttime() {
		return lasttime;
	}

	public void setLasttime(String lasttime) {
		this.lasttime = lasttime;
	}

	/*********************************************/

	@Override
	public void serializerElement(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "element");
		lotteryid.serializerLeaf(serializer);
		issues.serializerLeaf(serializer);
		serializer.endTag(null, "element");

	}

	@Override
	public String getTransactionType() {
		return "12002";
	}

}
