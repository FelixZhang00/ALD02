package com.project.jamffy.mobilesafe2.engine;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;

import android.util.Xml;

import com.project.jamffy.mobilesafe2.domain.UpdateInfo;

public class UpdateInfoParser {

	/**
	 * @param is
	 *            解析，xml的InputStream
	 * @return UpdateInfo
	 * @throws Exception
	 */
	public static UpdateInfo getUpdateInfo(InputStream is) throws Exception {
		UpdateInfo info=new UpdateInfo();
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "utf-8");  //设置编码集
		int type = parser.getEventType(); // 返回parser事件的类型，这样就可以定位到xml文件的开头了
		
		while(type!=XmlPullParser.END_DOCUMENT){
			switch (type) {
			case XmlPullParser.START_TAG:
				if("version".equals(parser.getName())){
					String version= parser.nextText();
					info.setVersion(version);
				}else if("description".equals(parser.getName())){
					String description= parser.nextText();
					info.setDescription(description);
				}else if("apkurl".equals(parser.getName())){
					String apkurl= parser.nextText();
					info.setApkurl(apkurl);
				}
				break;

//			default:
//				break;
			}
			type=parser.next();
		}
		return info;
	}
}
