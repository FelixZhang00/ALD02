package jamffy.example.lotterydemo.test;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import jamffy.example.lotterydemo.ConstantValues;
import jamffy.example.lotterydemo.net.protocal.Element;
import jamffy.example.lotterydemo.net.protocal.Message;
import jamffy.example.lotterydemo.net.protocal.element.CurrentIssueElement;

import org.xmlpull.v1.XmlSerializer;

import android.test.AndroidTestCase;
import android.util.Log;
import android.util.Xml;

/**
 * 
 * @author tmac
 *
 */
public class XmlTest extends AndroidTestCase {

	

	private static final String TAG = "XmlTest";

	/**
	 * 用面向对象的思路，封装xml信息。 把更多的代码放在工具类里，大大简化了客户端的代码量
	 */
	public void createXml3() {
		Message message=new Message();
		Element element=new CurrentIssueElement();
		String xml= message.getXml(element);
		Log.i(TAG, xml);
	}

	/**
	 * 用面向对象的思路，封装xml信息
	 */
	public void createXml2() {
		// 序列化
		XmlSerializer serializer = Xml.newSerializer();
		// 流可以根据需要更改，比如改成文件流FileWriter
		StringWriter writer = new StringWriter();
		try {
			serializer.setOutput(writer);
			// This method can only be called just after setOutput.
			serializer.startDocument(ConstantValues.ENCODING, null);
			
			Message message=new Message();
			message.serializerMessage(serializer);
			
			serializer.endDocument();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 用这种方法创建xml不方便协作开发,太繁琐
	 */
	public void createXml1() {
		// 序列化
		XmlSerializer serializer = Xml.newSerializer();
		// 流可以根据需要更改，比如改成文件流FileWriter
		StringWriter writer = new StringWriter();

		try {
			serializer.setOutput(writer);
			// This method can only be called just after setOutput.
			serializer.startDocument(ConstantValues.ENCODING, null);

			serializer.startTag(null, "message");
			serializer.startTag(null, "header");

			serializer.startTag(null, "agenterid");
			serializer.text(ConstantValues.AGENTER_ID);
			serializer.endTag(null, "agenterid");

			serializer.startTag(null, "agenterid");
			serializer.text(ConstantValues.AGENTER_ID);
			serializer.endTag(null, "agenterid");

			serializer.startTag(null, "agenterid");
			serializer.text(ConstantValues.AGENTER_ID);
			serializer.endTag(null, "agenterid");

			serializer.endTag(null, "header");
			serializer.startTag(null, "body");
			serializer.endTag(null, "body");
			serializer.endTag(null, "message");

			serializer.endDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
