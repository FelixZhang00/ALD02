package jamffy.example.lotterydemo.net.protocal;

import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import org.apache.commons.codec.digest.DigestUtils;
import org.xmlpull.v1.XmlSerializer;

import jamffy.example.lotterydemo.ConstantValues;

/*
 * 头结点的封装
 * @author tmac
 *
 */
public class Header {
	/*
	 * <header> <agenterid>889931</agenterid> <source>ivr</source>
	 * <compress>DES</compress>
	 * 
	 * <messengerid>20091113101533000001</messengerid>
	 * <timestamp>20091113101533</timestamp>
	 * <digest>7ec8582632678032d25866bd4bce114f</digest>
	 * 
	 * <transactiontype>12002</transactiontype> <username>13200000000</username>
	 * </header>
	 */

	private Leaf agenterid = new Leaf("agenterid", ConstantValues.AGENTER_ID);
	private Leaf source = new Leaf("source", ConstantValues.SOURCE);
	private Leaf compress = new Leaf("compress", ConstantValues.COMPRESS);
	private Leaf messengerid = new Leaf("messengerid");
	private Leaf timestamp = new Leaf("timestamp");
	private Leaf digest = new Leaf("digest");
	private Leaf transactiontype = new Leaf("transactiontype");
	private Leaf username = new Leaf("username");

	public void serializerHeader(XmlSerializer serializer, String body)
			throws Exception {

		setConstantLeaf(body);

		serializer.startTag(null, "header");
		agenterid.serializerLeaf(serializer);
		source.serializerLeaf(serializer);
		compress.serializerLeaf(serializer);
		messengerid.serializerLeaf(serializer);
		timestamp.serializerLeaf(serializer);
		digest.serializerLeaf(serializer);
		transactiontype.serializerLeaf(serializer);
		username.serializerLeaf(serializer);
		serializer.endTag(null, "header");
	}

	/**
	 * 设置messengerid、timestamp、digest的数据
	 * 
	 * @param body
	 *            xml中的 body 部分,digest加密部分需要用到
	 */
	private void setConstantLeaf(String body) {
		// timestamp:时间戳
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
		String time = dateFormat.format(new Date());

		timestamp.setTagValue(time);

		// messengerid:当前传递的xml文件的id(时间戳+六位的随机数)
		Random random = new Random();
		int num = random.nextInt(999999) + 1;// [0, n).[1,999999]
		DecimalFormat decimalFormat = new DecimalFormat("000000");
		String decimal = decimalFormat.format(num);

		messengerid.setTagValue(time + decimal);

		// digest：MD5的签名信息 (时间戳+子代理商的密码+body 之后再加密)
		String original = time + ConstantValues.AGENT_PASSWORD + body;
		String md5Info = DigestUtils.md5Hex(original);
		digest.setTagValue(md5Info);
	}

	// 这两个节点需要交给知道相关信息的类来设置value
	public Leaf getTransactiontype() {
		return transactiontype;
	}

	public Leaf getUsername() {
		return username;
	}

}
