package com.quanliren.quan_one.bean;

import java.io.Serializable;

import com.net.tsz.afinal.annotation.sqlite.Id;
import com.net.tsz.afinal.annotation.sqlite.Table;

@Table(name="CustomFilterBean")
public class CustomFilterBean implements Serializable{
	
	public String title;
	public String defaultValue;
	@Id
	public String key;
	public int id;
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDefaultValue() {
		return defaultValue;
	}
	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	public String getKey() {
		return key;
	}
	public void setKey(String key) {
		this.key = key;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public CustomFilterBean(String title, String defaultValue, String key,
			int id) {
		super();
		this.title = title;
		this.defaultValue = defaultValue;
		this.key = key;
		this.id = id;
	}
	public CustomFilterBean() {
		super();
		// TODO Auto-generated constructor stub
	}
}