package com.quanliren.quan_one.bean;

import android.content.Intent;

public class SetBean {
	public int icon;
	public String title;
	public boolean isFirst;
	public Intent clazz;
	public SetBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public SetBean(int icon, String title, boolean isFirst, Intent clazz) {
		super();
		this.icon = icon;
		this.title = title;
		this.isFirst = isFirst;
		this.clazz = clazz;
	}
}
