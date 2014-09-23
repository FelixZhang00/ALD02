package com.project.jamffy.mobilesafe2.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.project.jamffy.mobilesafe2.db.BlackNumberDBHelper;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class BlackNumberDao {

	private Context context;
	private BlackNumberDBHelper dbHelper;
	private String TAG = "BlackNumberDao";

	public BlackNumberDao(Context context) {
		this.context = context;
		this.dbHelper = new BlackNumberDBHelper(context);
	}

	/**
	 * 查询记录
	 * 
	 * @param number
	 *            要查询的号码
	 * @return 存在则返回true
	 */
	public boolean find(String number) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"SELECT * from blacknumber WHERE number=?",
					new String[] { number });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
			// 因为其他操作会用到这个操作就，在这里就不关了，在其他操作里关 
			//经过实验，还是关了吧 
			 db.close();
		}

		return result;
	}

	/**
	 * 添加数据 
	 * 并且不允许重复添加数据
	 * @param number
	 */
	public boolean add(String number) {
		boolean result=false;
		if (find(number)) {
			return result;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Logger.i(TAG, "add开始执行");
		if (db.isOpen()) {
				db.execSQL("INSERT INTO blacknumber (number)  VALUES(?)",
						new String[] { number });
				Logger.i(TAG, "add:" + number);
				result=true;
			db.close();
		}
		return result;

	}

	/**
	 * 删除数据
	 * 
	 * @param number
	 */
	public boolean delete(String number) {
		boolean result=false;
		if (!find(number)) {
			return result;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
	
				db.execSQL("DELETE FROM blacknumber WHERE number=?",
						new String[] { number });
				result=true;
			db.close();
		}
		return result;
	}

	/**
	 * 更新数据
	 * 
	 * @param oldnumber
	 *            旧号码
	 * @param newnumber
	 *            新号码
	 */
	public boolean update(String oldnumber, String newnumber) {
		boolean result=false;
		if (!find(oldnumber)) {
			return result;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
				db.execSQL("UPDATE blacknumber SET number=? WHERE number=?",
						new String[] { newnumber, oldnumber });
				result=true;
			db.close();
			
		}
		return result;
	}

	/**
	 * 获取所有记录
	 * 
	 * @return
	 */
	public List<String> getAllData() {
		List<String> numbers = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"SELECT * from blacknumber ORDER BY id desc", null); // 将最新的记录放在第一行
			while (cursor.moveToNext()) {
				String number = cursor.getString(cursor
						.getColumnIndex("number"));
				numbers.add(number);
			}
			cursor.close();
		}
		db.close();
//		System.out.println(numbers.toString());
		return numbers;
	}

}
