package com.project.jamffy.mobilesafe2.db.dao;

import android.database.sqlite.SQLiteDatabase;

/**
 * @author tmac
 *数据库访问类
 */
public class AddressDao {
	/**
	 * @param path
	 *            数据库路径
	 * @return 数据库对象
	 */
	public static SQLiteDatabase getAddressDb(String path) {
		return SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READWRITE);
	}

	
}
