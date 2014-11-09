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
import jamffy.example.lotterydemo.bean.ShoppingCart;
import jamffy.example.lotterydemo.bean.Ticket;
import jamffy.example.lotterydemo.bean.User;
import jamffy.example.lotterydemo.engine.BaseEngine;
import jamffy.example.lotterydemo.engine.UserEngine;
import jamffy.example.lotterydemo.net.HttpClientUtil;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.net.protocal.element.BalanceElement;
import jamffy.example.lotterydemo.net.protocal.element.BetElement;
import jamffy.example.lotterydemo.net.protocal.element.CurrentIssueElement;
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
		// 得到服务器的结果
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

	@Override
	public Message getBalance(User user) {
		// 第一步：获取登录用的xml
		BalanceElement element = new BalanceElement();
		Message message = new Message();
		message.getHeader().getUsername().setTagValue(user.getUesrname());
		String xml = message.getXml(element);
		// 得到服务器的结果
		// 经过父类校验后，将xml数据封装成message的形式
		Message result = super.getResult(xml);

		if (result != null) {
			// 请求结果的数据处理
			// body部分的第二次解析，解析的是明文内容
			XmlPullParser parser = Xml.newPullParser();

			String body = GlobalParams.XML_BODY;
			try {
				parser.setInput(new StringReader(body));
				int eventType = parser.getEventType();
				String tagName;
				BalanceElement balanceElement = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						tagName = parser.getName();
						// 处理公共数据
						if ("errorcode".equals(tagName)) {
							String text = parser.nextText();
							result.getBody().getOelement().setErrorcode(text);
						}
						if ("errormsg".equals(tagName)) {
							String text = parser.nextText();
							result.getBody().getOelement().setErrormsg(text);
						}

						// 处理特殊数据
						// 如果返回的数据中有element节点，
						if ("element".equals(tagName)) {
							balanceElement = new BalanceElement();
							result.getBody().getElements().add(balanceElement);
						}
						if ("investvalues".equals(tagName)) {
							String text = parser.nextText();
							if (balanceElement != null) {
								balanceElement.setInvestvalues(text);
							}
							System.out.println("余额：" + text);

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

	@Override
	public Message bet(User user) {
		// 第一步：获取登录用的xml
		BetElement element = new BetElement();
		element.getLotteryid().setTagValue(
				ShoppingCart.getInstance().getLotteryid().toString());

		// 彩票的业务里面：
		// ①关于注数的计算
		// ②关于投注信息封装（用户投注号码）

		// 010203040506|01^01020304050607|01

		StringBuffer codeBuffer = new StringBuffer();
		for (Ticket item : ShoppingCart.getInstance().getTickets()) {
			codeBuffer.append("^").append(item.getRedNum().replaceAll(" ", ""))
					.append("|").append(item.getBlueNum().replaceAll(" ", ""));
		}

		element.getLotterycode().setTagValue(codeBuffer.substring(1));

		element.getIssue().setTagValue(ShoppingCart.getInstance().getIssue());
		element.getLotteryvalue().setTagValue(
				(ShoppingCart.getInstance().getLotteryvalue() * 100) + "");

		element.getLotterynumber().setTagValue(
				ShoppingCart.getInstance().getLotterynumber().toString());
		element.getAppnumbers().setTagValue(
				ShoppingCart.getInstance().getAppnumbers().toString());
		element.getIssuesnumbers().setTagValue(
				ShoppingCart.getInstance().getIssuesnumbers().toString());

		element.getIssueflag().setTagValue(
				ShoppingCart.getInstance().getIssuesnumbers() > 1 ? "1" : "0");

		Message message = new Message();
		message.getHeader().getUsername().setTagValue(user.getUesrname());

		String xml = message.getXml(element);
		// 得到服务器的结果
		// 经过父类校验后，将xml数据封装成message的形式
		Message result = super.getResult(xml);

		if (result != null) {
			// 请求结果的数据处理
			// body部分的第二次解析，解析的是明文内容
			XmlPullParser parser = Xml.newPullParser();

			String body = GlobalParams.XML_BODY;
			try {
				parser.setInput(new StringReader(body));
				int eventType = parser.getEventType();
				String tagName;
				BetElement betElement = null;
				while (eventType != XmlPullParser.END_DOCUMENT) {
					switch (eventType) {
					case XmlPullParser.START_TAG:
						tagName = parser.getName();
						// 处理公共数据
						if ("errorcode".equals(tagName)) {
							String text = parser.nextText();
							result.getBody().getOelement().setErrorcode(text);
						}
						if ("errormsg".equals(tagName)) {
							String text = parser.nextText();
							result.getBody().getOelement().setErrormsg(text);
						}

						// 处理特殊数据
						// 如果返回的数据中有element节点，
						if ("element".equals(tagName)) {
							betElement = new BetElement();
							result.getBody().getElements().add(betElement);
						}
						if ("actvalue".equals(tagName)) {
							String text = parser.nextText();
							if (betElement != null) {
								betElement.setActvalue(text);
							}
							System.out.println("余额：" + text);

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
