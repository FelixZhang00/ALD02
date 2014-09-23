package com.project.jamffy.mobilesafe2.domain;

import android.graphics.drawable.Drawable;

import com.project.jamffy.mobilesafe2.R.drawable;

public class TaskInfo {
	private String appname;
	private Drawable appicon;
	private int pid;
	private int memorysize; //以kb为单位
	private boolean ischecked;
	private String packname;
	private boolean systemApp;

	public TaskInfo() {
	}

	public TaskInfo(String appname, Drawable appicon, int pid, int memorysize,
			boolean ischecked, String packname, boolean systemApp) {
		super();
		this.appname = appname;
		this.appicon = appicon;
		this.pid = pid;
		this.memorysize = memorysize;
		this.ischecked = ischecked;
		this.packname = packname;
		this.systemApp = systemApp;
	}

	public boolean isSystemApp() {
		return systemApp;
	}

	public void setSystemApp(boolean systemApp) {
		this.systemApp = systemApp;
	}

	public String getAppname() {
		return appname;
	}

	public void setAppname(String appname) {
		this.appname = appname;
	}

	public Drawable getAppicon() {
		return appicon;
	}

	public void setAppicon(Drawable appicon) {
		this.appicon = appicon;
	}

	public int getPid() {
		return pid;
	}

	public void setPid(int pid) {
		this.pid = pid;
	}

	public int getMemorysize() {
		return memorysize;
	}

	public void setMemorysize(int memorysize) {
		this.memorysize = memorysize;
	}

	public boolean isIschecked() {
		return ischecked;
	}

	public void setIschecked(boolean ischecked) {
		this.ischecked = ischecked;
	}

	public String getPackname() {
		return packname;
	}

	public void setPackname(String packname) {
		this.packname = packname;
	}

	@Override
	public String toString() {
		return "TaskInfo [appname=" + appname + ", appicon=" + appicon
				+ ", pid=" + pid + ", memorysize=" + memorysize
				+ ", ischecked=" + ischecked + ", packname=" + packname
				+ ",systemAPP" + systemApp + "]";
	}

}
