package com.project.jamffy.mobilesafe2.db.dao;

import com.project.jamffy.mobilesafe2.utils.MD5Encoder;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

/**
 * Considering the method isVirus() called so continually, I separate the
 * operation which open and close db in two new methods.
 * 
 * So you should construct AvirusDao before the frequent switch to db. And you
 * you complete them,don't forget to call closeDb().
 * 
 * @author tmac
 *
 */
public class AvirusDao {

	private SQLiteDatabase db;

	public AvirusDao() {
		String path = "/data/data/com.project.jamffy.mobilesafe2/files/antivirus.db";
		db = SQLiteDatabase.openDatabase(path, null,
				SQLiteDatabase.OPEN_READONLY);
	}

	/**
	 * @param sign
	 *            Every app has its own sign alone stored in md5. the sign
	 *            needn't convert to md5,I already convert it in this method.
	 * @return if the virus db exist app with virus ,return true.
	 */
	public boolean isVirus(String sign) {
		boolean result = false;
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("SELECT * FROM datable WHERE md5=?",
					new String[] { MD5Encoder.encode(sign) });
			if (cursor.moveToFirst()) {
				result=true;
			}
			cursor.close();

		}

		return result;
	}

	public void closeDb() {

		if (db.isOpen()) {
			db.close();
		}
	}
}
