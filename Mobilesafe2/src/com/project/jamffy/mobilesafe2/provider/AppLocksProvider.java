package com.project.jamffy.mobilesafe2.provider;

import com.project.jamffy.mobilesafe2.db.dao.AppLockDao;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.net.Uri;

public class AppLocksProvider extends ContentProvider {

	private AppLockDao dao;
	private final static UriMatcher URI_MATCHER = new UriMatcher(
			UriMatcher.NO_MATCH); // 没有匹配的uri则返回-1
	private final static int INSERT = 1;
	private final static int DELETE = 2;
	static {
		URI_MATCHER.addURI(
				"com.project.jamffy.mobilesafe2.provider.AppLocksProvider",
				"insert", INSERT);
		URI_MATCHER.addURI(
				"com.project.jamffy.mobilesafe2.provider.AppLocksProvider",
				"delete", DELETE);
	}
	private final static Uri changeUri = Uri
			.parse("content://com.project.jamffy.mobilesafe2.provider.AppLocksProvider");

	@Override
	public boolean onCreate() {
		dao = new AppLockDao(this.getContext());
		return false;
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder) {
		return null;
	}

	@Override
	public String getType(Uri uri) {
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		int result = URI_MATCHER.match(uri);
		if (result == INSERT) {
			String name = (String) values.get("packname");
			dao.add(name);
			this.getContext().getContentResolver()
					.notifyChange(changeUri, null);
		} else {
			throw new IllegalArgumentException("Unkonow URI:" + uri);
		}
		return null;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		int result = URI_MATCHER.match(uri);
		if (result == DELETE) {
			String packname = selectionArgs[0];
			dao.delete(packname);
			this.getContext().getContentResolver()
					.notifyChange(changeUri, null);
		} else {
			throw new IllegalArgumentException("Unkonow URI:" + uri);
		}
		return 0;

	}

	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs) {
		return 0;
	}

}
