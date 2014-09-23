package com.project.jamffy.mobilesafe2.test;

import java.util.List;

import com.project.jamffy.mobilesafe2.db.dao.CommNumDao;

import android.test.AndroidTestCase;

public class TestCommNumDao extends AndroidTestCase {

	public void testGetGroupCount(){
		
		System.out.println(CommNumDao.getGroupCount());
		
	}
	
	public void testgetGroupNames(){
		
		List<String> list=CommNumDao.getGroupNames();
		System.out.println(list);
	}
}
