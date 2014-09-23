package com.project.jamffy.mobilesafe2.domain;

import android.graphics.drawable.Drawable;

public class TrafficInfo {

	private String packname;
	private Drawable icon;
	private String appname;
	private long mobiletotal;
	private long wifitotal;

	// private long rxuid;  //因为需要实时更新，写在adapter方便更新 
	// private long txuid;

	public TrafficInfo() {

	}

	// public long getRxuid() {
	// return rxuid;
	// }
	//
	//
	//
	// public void setRxuid(long rxuid) {
	// this.rxuid = rxuid;
	// }
	//
	//
	//
	// public long getTxuid() {
	// return txuid;
	// }
	//
	//
	//
	// public void setTxuid(long txuid) {
	// this.txuid = txuid;
	// }

	public String getPackname() {
		return packname;
	}

	public void setPackname(String packname) {
		this.packname = packname;
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

	public long getMobiletotal() {
		return mobiletotal;
	}

	public void setMobiletotal(long mobiletotal) {
		this.mobiletotal = mobiletotal;
	}

	public long getWifitotal() {
		return wifitotal;
	}

	public void setWifitotal(long wifitotal) {
		this.wifitotal = wifitotal;
	}

	@Override
	public String toString() {
		return "TrafficInfo [packname=" + packname + ", icon=" + icon
				+ ", appname=" + appname + ", mobiletotal=" + mobiletotal
				+ ", wifitotal=" + wifitotal + "]";
	}

}
