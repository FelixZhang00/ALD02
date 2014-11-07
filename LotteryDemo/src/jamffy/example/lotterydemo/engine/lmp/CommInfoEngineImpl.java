package jamffy.example.lotterydemo.engine.lmp;

import java.io.IOException;
import java.io.StringReader;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.Xml;
import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.engine.BaseEngine;
import jamffy.example.lotterydemo.engine.CommInfoEngine;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.net.protocal.Oelement;
import jamffy.example.lotterydemo.net.protocal.element.CurrentIssueElement;
import jamffy.example.lotterydemo.util.DES;

public class CommInfoEngineImpl extends BaseEngine implements CommInfoEngine {

	@Override
	public Message getCurrentIssueInfo(Integer lotteryid) {
		// 设置要发送的数据（封装）
		CurrentIssueElement element = new CurrentIssueElement();
		element.getLotteryid().setTagValue(lotteryid.toString());
		Message message = new Message();

		// 发送经过协议封装过的数据，再从服务器上获取xml的数据
		String xml = message.getXml(element);
		// 经过父类校验后，将xml数据封装成message的形式
		Message result = super.getResult(xml);

		if (result != null) {
			// 请求结果的数据处理
			// body部分的第二次解析，解析的是明文内容
			XmlPullParser parser = Xml.newPullParser();

			DES des = new DES();
			String body = "<body>"
					+ des.authcode(result.getBody()
							.getServiceBodyInsideDESInfo(), "ENCODE",
							ConstantValues.DES_PASSWORD) + "</body>";

			try {
				parser.setInput(new StringReader(body));
				int eventType = parser.getEventType();
				String tagName;
				CurrentIssueElement currentIssueElement = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						tagName = parser.getName();
						// 处理公共数据
						if ("errorcode".equals(tagName)) {
							String text= parser.nextText();
							result.getBody().getOelement()
									.setErrorcode(text);
						}
						if ("errormsg".equals(tagName)) {
							String text= parser.nextText();
							result.getBody().getOelement()
									.setErrormsg(text);
						}

						// 处理特殊数据
						// 如果返回的数据中有element节点，代表有 当前彩票的销售信息
						if ("element".equals(tagName)) {
							currentIssueElement = new CurrentIssueElement();
							result.getBody().getElements()
									.add(currentIssueElement);
						}
						if ("issue".equals(tagName)) {
							String text= parser.nextText();
							if (currentIssueElement != null) {
								currentIssueElement.setIssue(text);
							}

						}

						if ("lasttime".equals(tagName)) {
							String text= parser.nextText();
							if (currentIssueElement != null) {
								currentIssueElement.setLasttime(text);
							}
						}
						break;

					default:
						break;
					}
					eventType = parser.next();
				}
			} catch (XmlPullParserException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			return result;
		}
		return null;
	}

}
