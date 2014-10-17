package com.quanliren.quan_one.bean;

import com.net.tsz.afinal.annotation.sqlite.Id;
import com.net.tsz.afinal.annotation.sqlite.Table;

@Table(name="LoginUserTable")
public class LoginUser {
	@Id
	private String id;
	private String mobile;
	private String pwd;
	private String token;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public String getPwd() {
		return pwd;
	}
	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public LoginUser(String id, String mobile, String pwd, String token) {
		super();
		this.id = id;
		this.mobile = mobile;
		this.pwd = pwd;
		this.token = token;
	}
	public LoginUser() {
		super();
		// TODO Auto-generated constructor stub
	}
}
