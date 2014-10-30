package jamffy.example.lotterydemo.net.protocal;

import org.xmlpull.v1.XmlSerializer;

/**
 * 请求数据的封装
 * 
 * @author tmac
 *
 */
public class Element {
	// 包含内容、序列化、特有（每个请求都有一个请求标示）

	// <lotteryid>118</lotteryid>
	// <issues>1</issues>
	private Leaf lotteryid = new Leaf("lotteryid");

	// 当成常量，直接赋值
	private Leaf issues = new Leaf("issues", "1");

	public Leaf getLotteryid() {
		return lotteryid;
	}

	/**
	 * 序列化请求
	 * 
	 * @param serializer
	 * @throws Exception
	 */
	public void serializerElement(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "element");
		lotteryid.serializerLeaf(serializer);
		issues.serializerLeaf(serializer);
		serializer.endTag(null, "element");

	}

	/**
	 * 获取请求标示
	 * 
	 * @return
	 */
	public String getTransactionType() {
		// 比如获取当前彩票期的请求标示
		return "12002";
	}

}
