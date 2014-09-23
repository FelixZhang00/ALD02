package com.project.jamffy.mobilesafe2.db.dao;

import java.util.ArrayList;
import java.util.List;

import com.project.jamffy.mobilesafe2.db.AppLockDBHelper;
import com.project.jamffy.mobilesafe2.db.BlackNumberDBHelper;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.project.jamffy.mobilesafe2.utils.Logger;

public class AppLockDao {

	private Context context;
	private AppLockDBHelper dbHelper;
	private String TAG = "AppLockDao";

	public AppLockDao(Context context) {
		this.context = context;
		this.dbHelper = new AppLockDBHelper(context);
	}

	/**
	 * 查询记录
	 * 
	 * @param name
	 *            要查询的包名
	 * @return 存在则返回true
	 */
	public boolean find(String name) {
		boolean result = false;
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery("SELECT * from applock WHERE name=?",
					new String[] { name });
			if (cursor.moveToNext()) {
				result = true;
			}
			cursor.close();
			// 因为其他操作会用到这个操作就，在这里就不关了，在其他操作里关
			db.close();
		}

		return result;
	}

	/**
	 * 添加数据
	 * 
	 * @param name
	 */
	public boolean add(String name) {
		boolean result = false;
		if (find(name)) {
			return result;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		Logger.i(TAG, "add开始执行");
		if (db.isOpen()) {
			db.execSQL("INSERT INTO applock (name)  VALUES(?)",
					new String[] { name });
			Logger.i(TAG, "add:" + name);
			result = true;
			db.close();
		}
		return result;

	}

	/**
	 * 删除数据
	 * 
	 * @param name
	 */
	public boolean delete(String name) {
		boolean result = false;
		if (!find(name)) {
			return result;
		}
		Logger.i(TAG, "delete开始执行");
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			
				db.execSQL("DELETE FROM applock WHERE name=?",
						new String[] { name });
				result = true;
			
			db.close();
		}
		return result;
	}

	/**
	 * 更新数据
	 * 
	 * @param oldname
	 *            旧包名
	 * @param newname
	 *            新包名
	 */
	public boolean update(String oldname, String newname) {
		boolean result = false;
		if (!find(oldname)) {
			return result;
		}
		SQLiteDatabase db = dbHelper.getWritableDatabase();
		if (db.isOpen()) {
			
				db.execSQL("UPDATE applock SET name=? WHERE name=?",
						new String[] { newname, oldname });
				result = true;
			
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
		List<String> names = new ArrayList<String>();
		SQLiteDatabase db = dbHelper.getReadableDatabase();
		if (db.isOpen()) {
			Cursor cursor = db.rawQuery(
					"SELECT * from applock ORDER BY id desc", null); // 将最新的记录放在第一行
			while (cursor.moveToNext()) {
				String name = cursor.getString(cursor.getColumnIndex("name"));
				names.add(name);
			}
			cursor.close();
		}
		db.close();
//		System.out.println(names.toString());
		return names;
	}

}
