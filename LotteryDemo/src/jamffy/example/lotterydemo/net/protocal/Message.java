package jamffy.example.lotterydemo.net.protocal;

import jamffy.example.lotterydemo.ConstantValues;

import java.io.StringWriter;
import java.util.List;

import org.xmlpull.v1.XmlSerializer;

import android.util.Xml;

/**
 * 协议封装
 * 
 * @author tmac
 *
 */
public class Message {

	private Header header = new Header();
	private Body body = new Body();

	public Header getHeader() {
		return header;
	}

	public Body getBody() {
		return body;
	}

	/**
	 * 序列化整个协议
	 * 
	 * @param serializer
	 * @throws Exception
	 */
	public void serializerMessage(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "message");
		// <message version="1.0">
		serializer.attribute(null, "version", "1.0");

		header.serializerHeader(serializer, body.getWholeBody()); // 获取完整的body
		// body.serializerBody(serializer);
		// 不能再放明文了，得加密
		serializer.startTag(null, "body");
		serializer.text(body.getBodyDESInfo());
		serializer.endTag(null, "body");

		serializer.endTag(null, "message");

	}

	/**
	 * 转化成xml数据
	 * 
	 * @param element
	 *            请求对象,如果要扩展添加多个element的话， 
	 *            只要将类型改为List<Element>，后面用循环给body添加element即可
	 * @return
	 */
	public String getXml(Element element) {
		if (element == null) {
			throw new IllegalArgumentException("element is null!");
		}

		// 请求标示需要设置,请求内容需要设置
		header.getTransactiontype().setTagValue(element.getTransactionType());
		body.getElements().add(element);

		// 序列化
		XmlSerializer serializer = Xml.newSerializer();
		// 流可以根据需要更改，比如改成文件流FileWriter
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			// This method can only be called just after setOutput.
			serializer.startDocument(ConstantValues.ENCODING, null);
			this.serializerMessage(serializer);
			serializer.endDocument();
			return writer.toString();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return null;
	}

}
