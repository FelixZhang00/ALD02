package com.project.jamffy.mobilesafe2.engine;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.net.rtp.RtpStream;

import com.project.jamffy.mobilesafe2.domain.ContactInfo;

public class ContactInfoService {
	private Context context;

	public ContactInfoService(Context context) {
		this.context = context;
	}

	/**
	 * 1.获取联系人的id 2.根据联系人的id 数据的type 获取对应的数据（名字、电话。。。）
	 *
	 * @return
	 */
	public  List<ContactInfo> getContactInfo() {
		List<ContactInfo> infos=new ArrayList<ContactInfo>();
		ContactInfo info;
		ContentResolver resolver = context.getContentResolver();
		Uri uri = Uri.parse("content://com.android.contacts/contacts"); // 首先查询的是raw_contacts表
		Cursor cursor = resolver.query(uri, new String[] { "_id" }, null, null,
				null); // 首先查询的是raw_contacts表
		while (cursor.moveToNext()) {
			info=new ContactInfo();
			StringBuilder sb = new StringBuilder(); // 创建一个字符构造对象
			String cursorid = cursor.getString(cursor.getColumnIndex("_id"));
			sb.append("id:" + cursorid + "\t");
//			System.out.println(sb.toString());

			Uri uri2 = Uri.parse("content://com.android.contacts/contacts/"
					+ cursorid + "/data"); // 查询data表
			Cursor cursor2 = resolver.query(uri2, new String[] { "mimetype",
					"data1" }, null, null, null);
			while (cursor2.moveToNext()) {
				String type = cursor2.getString(cursor2
						.getColumnIndex("mimetype"));
				String data = cursor2
						.getString(cursor2.getColumnIndex("data1"));
				// if (type.equals("vnd.android.cursor.item/email_v2")) {
				// sb.append("email:" + data + "\t");
				// }

				if (type.equals("vnd.android.cursor.item/phone_v2")) {
					sb.append("phone:" + data + "\t");
					info.setPhone(data);
				}
				if (type.equals("vnd.android.cursor.item/name")) {
					sb.append("name:" + data + "\t");
					info.setName(data);
				}
				// if (type.equals("vnd.android.cursor.item/postal-address_v2"))
				// {
				// sb.append("address:" + data + "\t");
				// }
			}
			sb.append("\n");
//			System.out.println(sb.toString());
			infos.add(info);
			cursor2.close();
		}
		cursor.close();
		return infos;
	}
}
