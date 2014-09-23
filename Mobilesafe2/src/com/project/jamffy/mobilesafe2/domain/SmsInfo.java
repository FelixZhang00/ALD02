package com.project.jamffy.mobilesafe2.domain;

import java.util.Date;

public class SmsInfo {
	private String address;
	private String date;
	private String body;
	private long id;
	private int read;
	private int type;

	public SmsInfo() {
	}

	public SmsInfo(String address, String date, String body, long id, int read,
			int type) {
		this.address = address;
		this.date = date;
		this.body = body;
		this.id = id;
		this.read = read;
		this.type = type;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getBody() {
		return body;
	}

	public void setBody(String body) {
		this.body = body;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getRead() {
		return read;
	}

	public void setRead(int read) {
		this.read = read;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	@Override
	public String toString() {
		return "SmsInfo [_id=" + id + ", address=" + address + ", date=" + date
				+ ", body=" + body + ", read=" + read + ", type=" + type + "]";
	}

}
