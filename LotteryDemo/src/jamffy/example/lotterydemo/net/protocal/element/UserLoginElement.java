package jamffy.example.lotterydemo.net.protocal.element;

import org.xmlpull.v1.XmlSerializer;

import jamffy.example.lotterydemo.net.protocal.Element;
import jamffy.example.lotterydemo.net.protocal.Leaf;

public class UserLoginElement extends Element {

	private Leaf actpassword=new Leaf("actpassword");

	public Leaf getActpassword() {
		return actpassword;
	}

	@Override
	public void serializerElement(XmlSerializer serializer) throws Exception {
		serializer.startTag(null, "element");
		actpassword.serializerLeaf(serializer);
		actpassword.serializerLeaf(serializer);
		serializer.endTag(null, "element");
	}

	@Override
	public String getTransactionType() {
		return "14001";
	}

}
