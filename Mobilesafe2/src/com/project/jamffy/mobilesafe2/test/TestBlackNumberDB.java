package com.project.jamffy.mobilesafe2.test;

import com.project.jamffy.mobilesafe2.db.dao.BlackNumberDao;

import android.test.AndroidTestCase;

public class TestBlackNumberDB extends AndroidTestCase {

	public void testAdd() throws Exception {
		BlackNumberDao numberDao=new BlackNumberDao(this.getContext());
		for (int j = 0; j < 4; j++) {			
			numberDao.add("1234567"+j);
		}
		
	}
	
	public void testDelete(){
		BlackNumberDao numberDao=new BlackNumberDao(this.getContext());
		numberDao.delete("12345672");
	}
	public void testupdate(){
		BlackNumberDao numberDao=new BlackNumberDao(this.getContext());
		numberDao.update("12345671", "2222");
	}
}
