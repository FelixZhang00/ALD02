package com.project.jamffy.mobilesafe2.engine;

import java.io.File;

import com.project.jamffy.mobilesafe2.db.dao.AddressDao;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class NumberAddressService {
	private final static String TAG = "NumberAddressService";

	/**
	 * @param number
	 *            手机号码
	 * @return 号码归属地
	 */
	public static String getAddress(String number) {
		String address = number;
		String pattern = "^1[3458]\\d{9}$"; // 手机号码的正则表达式

		if (isDBexist()) { // 只有在地址存在的时候才会执行查询，否则返回原号码
			SQLiteDatabase db = AddressDao
					.getAddressDb("/sdcard/mysafemobileaddress.db");
			if (db.isOpen()) {
				if (number.matches(pattern)) {
					Logger.i(TAG, "11位手机号");
					Cursor cursor = db.rawQuery(
							"select city FROM info where mobileprefix=?",
							new String[] { number.substring(0, 7) });
					if (cursor.moveToNext()) {
						address = cursor.getString(0);
					}
					cursor.close();
				} else { // 固定电话
					switch (number.length()) {
					case 4: // 模拟器
						address = "模拟器";
						break;
					case 7: // 本地号码
						address = "本地号码";
						break;
					case 8: // 本地号码
						address = "本地号码";
						break;
					case 10: // 3位区号+7位号码
						Logger.i(TAG, "11位");
						Cursor cursor = db.rawQuery(
								"select city FROM info where area=? LIMIT 1",
								new String[] { number.substring(0, 3) });
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
						break;
					case 11: // 3位区号+8位号码 or 4位区号+7位号码
						Logger.i(TAG, "11位");
						cursor = db.rawQuery(
								"select city FROM info where area=? LIMIT 1",
								new String[] { number.substring(0, 3) });
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
						cursor = db.rawQuery(
								"select city FROM info where area=? LIMIT 1",
								new String[] { number.substring(0, 4) });
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
						break;

					case 12: // 4位区号+8位号码
						Logger.i(TAG, "12位");
						cursor = db.rawQuery(
								"select city FROM info where area=? LIMIT 1",
								new String[] { number.substring(0, 4) });
						Logger.i(TAG, number.substring(0, 4));
						if (cursor.moveToNext()) {
							address = cursor.getString(0);
						}
						cursor.close();
						break;

					}
				}
				db.close();
			}

		}
		return address;
	}

	/**
	 * 判断来电归属地数据库是否存在
	 * 
	 * @return
	 */
	public static boolean isDBexist() {
		File file = new File("/sdcard/mysafemobileaddress.db");// 获得文件句柄
		return file.exists();
	}

}
