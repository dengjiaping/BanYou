package com.quanliren.quan_one.bean;

import java.io.Serializable;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.annotation.sqlite.Id;
import com.net.tsz.afinal.annotation.sqlite.Table;

@Table(name="GroupBeanTable")
public class GroupBeanTable implements Serializable{
	@Id
	private String id;
	private String content;
	private GroupBean groupBean;
	public GroupBean getGroupBean() {
		return groupBean;
	}
	public void setGroupBean(GroupBean groupBean) {
		this.groupBean = groupBean;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getContent() {
		if(groupBean==null){
			return content;
		}
		return new Gson().toJson(groupBean);
	}
	public void setContent(String content) {
		this.content = content;
		groupBean=new Gson().fromJson(content, new TypeToken<GroupBean>(){}.getType());
	}
	public GroupBeanTable(GroupBean groupBean) {
		super();
		this.id = groupBean.getId();
		this.groupBean = groupBean;
	}
	public GroupBeanTable() {
		super();
		// TODO Auto-generated constructor stub
	}
}
