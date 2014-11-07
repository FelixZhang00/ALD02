package jamffy.example.lotterydemo.net.protocal;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.util.DES;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * 消息体节点封装
 * 
 * @author tmac
 *
 */
public class Body {

	/*
	 * <body> <elements> <element>
	 *  <lotteryid>118</lotteryid> <issues>1</issues>
	 * 
	 * </element> 
	 * </elements> 
	 * </body>
	 */

	private List<Element> elements = new ArrayList<Element>();

	public List<Element> getElements() {
		return elements;
	}

	/**
	 * 序列化请求
	 * 
	 * @param serializer
	 * @throws Exception
	 */
	public void serializerBody(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "body");
		serializer.startTag(null, "elements");
		for (Element element : elements) {
			element.serializerElement(serializer);
		}

		serializer.endTag(null, "elements");
		serializer.endTag(null, "body");

	}

	/**
	 * 获取完整的body
	 * 
	 * @return
	 */
	public String getWholeBody() {
		// 需要重新获取一个纯净的序列化对象
		XmlSerializer tempSerializer = Xml.newSerializer();
		// 流可以根据需要更改，比如改成文件流FileWriter
		StringWriter writer = new StringWriter();
		try {
			tempSerializer.setOutput(writer);
			this.serializerBody(tempSerializer);
			tempSerializer.flush();
			return writer.toString();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

	/**
	 * 获取body内的DES加密数据
	 * 
	 * @return
	 */
	public String getBodyDESInfo() {
		String wholebody = getWholeBody();
		// 原数据,不包括 <body>标签
		String orgDesInfo = StringUtils.substringBetween(wholebody, "<body>",
				"</body>");

		// 加密
		DES des = new DES();

		return des.authcode(orgDesInfo, "DECODE", ConstantValues.DES_PASSWORD);
	}

	/********************* 处理服务器回复 *************************/
	private String serviceBodyInsideDESInfo;// 服务器端回复的body中的DES加密的信息
	private Oelement oelement = new Oelement();

	public Oelement getOelement() {
		return oelement;
	}

	public String getServiceBodyInsideDESInfo() {
		return serviceBodyInsideDESInfo;
	}

	public void setServiceBodyInsideDESInfo(String serviceBodyInsideDESInfo) {
		this.serviceBodyInsideDESInfo = serviceBodyInsideDESInfo;
	}
	/********************* 处理服务器回复 *************************/

}
