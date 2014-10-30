package jamffy.example.lotterydemo.net.protocal;

import java.io.IOException;

import org.xmlpull.v1.XmlSerializer;

import jamffy.example.lotterydemo.ConstantValues;

/**
 * 封装xml数据用的 简单的叶子（name+value)
 * 
 * @author tmac
 *
 */
public class Leaf {

	private String tagName;
	private String tagValue;

	// 不使用无参构造方法，强制创建Leaf对象是必须至少有tagName
	public Leaf(String tagName) {
		super();
		this.tagName = tagName;
	}
	
	
	public Leaf(String tagName, String tagValue) {
		super();
		this.tagName = tagName;
		this.tagValue = tagValue;
	}
	
	public String getTagName() {
		return tagName;
	}


	public void setTagName(String tagName) {
		this.tagName = tagName;
	}


	public String getTagValue() {
		return tagValue;
	}


	public void setTagValue(String tagValue) {
		this.tagValue = tagValue;
	}


	public void serializerLeaf(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, tagName);
		if (tagValue == null) {
			tagValue = "";
		}
		serializer.text(tagValue);
		serializer.endTag(null, tagName);
	}
	
	

}
