package jamffy.example.lotterydemo.engine.lmp;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.util.DebugUtils;
import android.util.Xml;
import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.GlobalParams;
import jamffy.example.lotterydemo.bean.User;
import jamffy.example.lotterydemo.engine.BaseEngine;
import jamffy.example.lotterydemo.engine.UserEngine;
import jamffy.example.lotterydemo.net.HttpClientUtil;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.net.protocal.element.UserLoginElement;
import jamffy.example.lotterydemo.util.DES;

public class UserEnginImpl extends BaseEngine implements UserEngine {

	@Override
	public Message login(User user) {
		// 第一步：获取登录用的xml
		// 创建登录用的element
		UserLoginElement element = new UserLoginElement();
		// 设置用户数据
		element.getActpassword().setTagValue(user.getPassword());
		// Message.getXml(element);
		Message message = new Message();
		message.getHeader().getUsername().setTagValue(user.getUesrname());
		String xml = message.getXml(element);
		Message resultMessage = getResult(xml);

		// 第四步：请求结果的数据处理
		// body部分的第二次解析，解析的是明文内容
		XmlPullParser pullParser = Xml.newPullParser();
		String body = GlobalParams.XML_BODY;
		if (StringUtils.isNotBlank(body)) {
			try {
				pullParser.setInput(new StringReader(body));
				int eventType = pullParser.getEventType();
				String name = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						name = pullParser.getName();
						if ("errorcode".equals(name)) {
							resultMessage.getBody().getOelement()
									.setErrorcode(pullParser.nextText());
						}
						if ("errormsg".equals(name)) {
							resultMessage.getBody().getOelement()
									.setErrormsg(pullParser.nextText());
						}
						break;

					default:
						break;
					}
					eventType = pullParser.next();

				}
				return resultMessage;
			} catch (XmlPullParserException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return null;

	}

	/**
	 * 用户登录(没有用到面向对象的思想，没用父类抽取出来的方法)
	 * 
	 * @param user
	 * @return
	 */
	@Deprecated
	public Message login1(User user) {
		// 第一步：获取登录用的xml
		// 创建登录用的element
		UserLoginElement element = new UserLoginElement();
		// 设置用户数据
		element.getActpassword().setTagValue(user.getPassword());
		// Message.getXml(element);
		Message message = new Message();
		message.getHeader().getUsername().setTagValue(user.getUesrname());
		String xml = message.getXml(element);

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

			String orgInfo = resultMessage.getHeader().getTimestamp()
					.getTagValue()
					+ ConstantValues.AGENT_PASSWORD + body;
			// 利用工具生成手机端的MD5，再与服务器端的比对
			String md5Hex = DigestUtils.md5Hex(orgInfo);
			if (md5Hex.equals(resultMessage.getHeader().getDigest()
					.getTagValue())) {
				// 第四步：请求结果的数据处理
				// body部分的第二次解析，解析的是明文内容
				pullParser = Xml.newPullParser();
				try {
					pullParser.setInput(new StringReader(body));
					int eventType = pullParser.getEventType();
					String name = null;
					while (eventType != XmlPullParser.END_DOCUMENT) {
						switch (eventType) {
						case XmlPullParser.START_TAG:
							name = pullParser.getName();
							if ("errorcode".equals(name)) {
								resultMessage.getBody().getOelement()
										.setErrorcode(pullParser.nextText());
							}
							if ("errormsg".equals(name)) {
								resultMessage.getBody().getOelement()
										.setErrormsg(pullParser.nextText());
							}
							break;

						default:
							break;
						}
						eventType = pullParser.next();

					}
					return resultMessage;
				} catch (XmlPullParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}
		return null;
	}
}
