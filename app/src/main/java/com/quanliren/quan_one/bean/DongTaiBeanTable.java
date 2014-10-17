package com.quanliren.quan_one.bean;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.sqlite.Id;
import com.net.tsz.afinal.annotation.sqlite.Table;

@Table(name = "DongTaiBeanTable")
public class DongTaiBeanTable implements Serializable {
	@Id
	private String dyid;
	public String getDyid() {
		return dyid;
	}

	public void setDyid(String dyid) {
		this.dyid = dyid;
	}

	private String content;
	private DongTaiBean bean;


	public DongTaiBeanTable(String dyid, String content, DongTaiBean bean) {
		super();
		this.dyid = dyid;
		this.content = content;
		this.bean = bean;
	}

	public DongTaiBean getBean() {
		return bean;
	}

	public void setBean(DongTaiBean bean) {
		this.bean = bean;
	}

	public String getContent() {
		if (bean == null) {
			return content;
		}
		return new Gson().toJson(bean);
	}

	public void setContent(String content) {
		this.content = content;
		bean = new Gson().fromJson(content, new TypeToken<DongTaiBean>() {
		}.getType());
	}

	public DongTaiBeanTable() {
		super();
		// TODO Auto-generated constructor stub
	}

	public DongTaiBeanTable(DongTaiBean bean) {
		super();
		this.bean = bean;
		this.dyid=bean.getDyid();
	}
}
