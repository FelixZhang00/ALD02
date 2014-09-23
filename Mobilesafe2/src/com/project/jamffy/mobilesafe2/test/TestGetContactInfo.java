package com.project.jamffy.mobilesafe2.test;

import java.util.List;

import com.project.jamffy.mobilesafe2.domain.ContactInfo;
import com.project.jamffy.mobilesafe2.engine.ContactInfoService;

import android.test.AndroidTestCase;

public class TestGetContactInfo extends AndroidTestCase {

	public void testGetInfo()throws Exception{
		ContactInfoService service=new ContactInfoService(getContext());
		service.getContactInfo();
	}
	public void testGetInfo2()throws Exception{
		ContactInfoService service=new ContactInfoService(getContext());
		List<ContactInfo> infos= service.getContactInfo();
		for (ContactInfo contactInfo : infos) {
			System.out.println(contactInfo.getName());
			System.out.println(contactInfo.getPhone());
		}
	}
	
}
