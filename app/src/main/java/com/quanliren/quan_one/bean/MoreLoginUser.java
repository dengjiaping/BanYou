package com.quanliren.quan_one.bean;

import com.net.tsz.afinal.annotation.sqlite.Table;

@Table(name="moreloginuser_table")
public class MoreLoginUser {

	private Integer id;
	private String username;
	private String password;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public MoreLoginUser() {
		super();
		// TODO Auto-generated constructor stub
	}
	public MoreLoginUser(String username, String password) {
		super();
		this.username = username;
		this.password = password;
	}
}
