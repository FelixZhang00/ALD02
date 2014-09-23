package com.project.jamffy.mobilesafe2.test;

import com.project.jamffy.mobilesafe2.db.AppLockDBHelper;
import com.project.jamffy.mobilesafe2.db.dao.AppLockDao;

import android.test.AndroidTestCase;

public class TestAppLockDB extends AndroidTestCase {

	public void testAdd() throws Exception {
		AppLockDao dao = new AppLockDao(this.getContext());
		dao.add("com.add.1");
		dao.add("com.add.2");
		dao.add("com.add.3");
	}

	public void testDelete() throws Exception {
		AppLockDao dao = new AppLockDao(getContext());
		dao.delete("com.add.1");
	}

}
