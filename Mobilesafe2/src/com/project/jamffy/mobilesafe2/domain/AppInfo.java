package com.project.jamffy.mobilesafe2.domain;

import android.graphics.drawable.Drawable;

public class AppInfo {
	private Drawable icon;
	private String appname;
	private String packname;
	private String appsize;
	private long appLength; //方便按大小排序
	private boolean systemApp;

	public AppInfo() {
	}

	public AppInfo(Drawable icon, String appname, String packname,
			String apksize,boolean systemApp) {
		super();
		this.icon = icon;
		this.appname = appname;
		this.packname = packname;
		this.appsize = apksize;
		this.systemApp =systemApp;
	}

	
	
	public long getAppLength() {
		return appLength;
	}

	public void setAppLength(long appLength) {
		this.appLength = appLength;
	}

	public boolean isSystemApp() {
		return systemApp;
	}

	public void setSystemApp(boolean systemApp) {
		this.systemApp = systemApp;
	}

	public Drawable getIcon() {
		return icon;
	}

	public void setIcon(Drawable icon) {
		this.icon = icon;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public String getPackname() {
		return packname;
	}

	public void setPackname(String packname) {
		this.packname = packname;
	}

	public String getAppsize() {
		return appsize;
	}

	public void setAppsize(String appsize) {
		this.appsize = appsize;
	}

	@Override
	public String toString() {
		return "AppInfo [icon=" + icon + ", appname=" + appname + ", packname="
				+ packname + ", appsize=" + appsize + ", appLength="
				+ appLength + ", systemApp=" + systemApp + "]";
	}


}
