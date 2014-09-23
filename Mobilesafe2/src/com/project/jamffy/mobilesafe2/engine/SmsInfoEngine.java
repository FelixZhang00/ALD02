package com.project.jamffy.mobilesafe2.engine;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import com.project.jamffy.mobilesafe2.utils.Logger;
import android.util.Xml;
import android.widget.Toast;

import com.project.jamffy.mobilesafe2.domain.SmsInfo;

public class SmsInfoEngine {
	private SmsInfo smsInfo;
	private Context context;

	// 全部短信
	private static final String SMS_ALL = "content://sms/";
	private static final String TAG = "SmsInfoEngine";
	private SimpleDateFormat dateFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");
	private SimpleDateFormat dateFormat2 = new SimpleDateFormat(
			"yyyyMMddHHmmss");

	public SmsInfoEngine(Context context) {
		this.context = context;
	}

	public long getCounts() {
		long counts = 0;
		List<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse(SMS_ALL);
		Cursor cursor = resolver.query(uri, new String[] { "_id" }, null, null,
				null);
		counts = cursor.getCount();
		return counts;
	}

	public List<SmsInfo> getAllSms() {
		List<SmsInfo> smsInfos = new ArrayList<SmsInfo>();
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse(SMS_ALL);
		String[] projection = { "_id", "address", "date", "read", "type",
				"body" };
		Cursor cursor = resolver
				.query(uri, projection, null, null, "date asc"); // 查询并按日期倒序

		while (cursor.moveToNext()) {
			smsInfo = new SmsInfo();
			String address = cursor.getString(cursor.getColumnIndex("address"));
			smsInfo.setAddress(address);

			String date = dateFormat.format(cursor.getLong(cursor
					.getColumnIndex("date")));
			smsInfo.setDate(date);

			String body = cursor.getString(cursor.getColumnIndex("body"));
			smsInfo.setBody(body);

			long id = cursor.getLong(cursor.getColumnIndex("_id"));
			smsInfo.setId(id);

			int read = cursor.getInt(cursor.getColumnIndex("read"));
			smsInfo.setRead(read);

			int type = cursor.getInt(cursor.getColumnIndex("type"));
			smsInfo.setType(type);

			smsInfos.add(smsInfo);
			smsInfo = null;

		}
//		System.out.println(smsInfos);
		return smsInfos;
	}

	/**
	 * @param pd
	 * @return 返回true表示备份文件存在
	 * @throws Exception
	 */
	public boolean restoreSms(ProgressDialog pd) throws Exception {
		Logger.i(TAG, "开始还原短信");

		// 找到备份好的文件
		String absolutePath = Environment.getExternalStorageDirectory()
				+ "/SMSBackup/message.xml";
		File file = new File(absolutePath);
		if (!file.exists()) {
			return false;
		}

		String max = null;
		int currentcount = 0;

		ContentResolver resolver = null;
		ContentValues values = null;

		XmlPullParser parser = Xml.newPullParser(); // 新建一个解析器
		FileInputStream is = new FileInputStream(file);
		parser.setInput(is, "utf-8");
		int type = parser.getEventType();
		while (type != XmlPullParser.END_DOCUMENT) {
			switch (type) {
			case XmlPullParser.START_DOCUMENT:
				resolver = this.context.getContentResolver();
				break;

			case XmlPullParser.START_TAG:
				// 判断根节点是否符合要求 不符合就直接return
				if ("count".equals(parser.getName())) {
					max = parser.nextText();
					pd.setMax(Integer.parseInt(max));
				} else if ("smss".equals(parser.getName())) {
					Logger.i(TAG, "找到了正确的文件头");
				} else if ("sms".equals(parser.getName())) {
					values = new ContentValues();
				} else if ("address".equals(parser.getName())) {
					values.put("address", parser.nextText());
				} else if ("body".equals(parser.getName())) {
					values.put("body", parser.nextText());
				} else if ("read".equals(parser.getName())) {
					values.put("read", parser.nextText());
				} else if ("type".equals(parser.getName())) {
					values.put("type", parser.nextText());
				} else if ("date".equals(parser.getName())) {
					String date = dateFormat2.format(parser.nextText());
					Logger.i(TAG, date);
					values.put("date", date);
				}
				break;
			case XmlPullParser.END_TAG:
				if ("sms".equals(parser.getName())) {
					// 上传一条短信记录
					Uri uri = Uri.parse(SMS_ALL);
					resolver.insert(uri, values);
					values = null;
					pd.setProgress(currentcount++);
				}
				break;

			default:
				break;
			}
			type = parser.next();
		}
		return true;

	}
}
