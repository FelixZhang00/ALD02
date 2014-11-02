package jamffy.example.lotterydemo.engine;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.codec.digest.DigestUtils;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.GlobalParams;
import jamffy.example.lotterydemo.net.HttpClientUtil;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.util.DES;

public abstract class BaseEngine {

	/**
	 * 获取从服务器中获取的信息，先经过此父类校验分析， 确定是指定的服务器发过来的就将message数据交给相应的孩子处理。
	 * 
	 * @param xml
	 *            手机端向服务器发送的xml数据
	 * @return
	 */
	public Message getResult(String xml) {
		// 第二步：发送xml到服务器
		// HttpClientUtil.sendXml();
		HttpClientUtil httpClientUtil = new HttpClientUtil();
		InputStream is = httpClientUtil
				.sendXml(ConstantValues.LOTTERY_URI, xml);
		// 判断输入流非空
		if (is != null) {
			Message resultMessage = new Message();

			// 第三步：数据的校验（MD5数据校验）
			// timestamp+digest+body

			/*
			 * 解析通用步骤
			 */
			// 给解析器设置输入流
			// 解析事件类型直到文档结束
			// 当解析到文档的开始tag时，就可以找name，并处理value
			// 递增，让解析器继续往下找
			XmlPullParser pullParser = Xml.newPullParser();
			try {
				pullParser.setInput(is, ConstantValues.ENCODING);
				int eventType = pullParser.getEventType();
				String name = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						name = pullParser.getName();
						if ("timestamp".equals(name)) {
							resultMessage.getHeader().getTimestamp()
									.setTagValue(pullParser.nextText());
						}
						if ("digest".equals(name)) {
							resultMessage.getHeader().getDigest()
									.setTagValue(pullParser.nextText());
						}
						if ("body".equals(name)) {
							resultMessage.getBody()
									.setServiceBodyInsideDESInfo(
											pullParser.nextText());
						}
						break;

					default:
						break;
					}
					eventType = pullParser.next();

				}
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			// 时间戳（解析）+密码（常量）+body明文（解析+解密DES）
			DES des = new DES();
			String body = "<body>"
					+ des.authcode(resultMessage.getBody()
							.getServiceBodyInsideDESInfo(), "ENCODE",
							ConstantValues.DES_PASSWORD) + "</body>";
			// 保存解析出来的body部分，子类还要用
			GlobalParams.XML_BODY = body;

			String orgInfo = resultMessage.getHeader().getTimestamp()
					.getTagValue()
					+ ConstantValues.AGENT_PASSWORD + body;
			// 利用工具生成手机端的MD5，再与服务器端的比对
			String md5Hex = DigestUtils.md5Hex(orgInfo);
			if (md5Hex.equals(resultMessage.getHeader().getDigest()
					.getTagValue())) {
				// 校验通过
				return resultMessage;
			}
		}

		return null;
	}

}
