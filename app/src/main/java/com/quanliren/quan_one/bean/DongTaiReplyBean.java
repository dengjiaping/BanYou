package com.quanliren.quan_one.bean;

import java.io.Serializable;

public class DongTaiReplyBean implements Serializable{

	private String id;
	private String content;
	private String ctime;
	private String userid;
	private String nickname;
	private String avatar;
	private String replyuid;
	private String replyuname;
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public String getReplyuid() {
		return replyuid;
	}
	public void setReplyuid(String replyuid) {
		this.replyuid = replyuid;
	}
	public String getReplyuname() {
		return replyuname;
	}
	public void setReplyuname(String replyuname) {
		this.replyuname = replyuname;
	}
	public DongTaiReplyBean(String id, String content, String ctime,
			String userid, String nickname, String avatar, String replyuid,
			String replyuname) {
		super();
		this.id = id;
		this.content = content;
		this.ctime = ctime;
		this.userid = userid;
		this.nickname = nickname;
		this.avatar = avatar;
		this.replyuid = replyuid;
		this.replyuname = replyuname;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public DongTaiReplyBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	
}
