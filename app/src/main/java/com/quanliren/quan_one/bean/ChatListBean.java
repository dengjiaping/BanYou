package com.quanliren.quan_one.bean;

import java.io.Serializable;

import com.net.tsz.afinal.annotation.sqlite.Id;
import com.net.tsz.afinal.annotation.sqlite.Index;
import com.net.tsz.afinal.annotation.sqlite.Table;
import com.net.tsz.afinal.annotation.sqlite.Transient;

@Table(name="ChatListBean")
public class ChatListBean implements Serializable{
	@Transient
	public static final String TableName="ChatListBean";
	@Id
	private int id;
	@Index
	private String userid;
	@Index
	private String friendid;
	private String content;
	private String ctime;
	private String userlogo;
	private String nickname;
	
	
	public String getUserlogo() {
		return userlogo;
	}
	public void setUserlogo(String userlogo) {
		this.userlogo = userlogo;
	}
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	private User friend;
	private int msgCount=0;
	
	public User getFriend() {
		return friend;
	}
	public void setFriend(User friend) {
		this.friend = friend;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getMsgCount() {
		return msgCount;
	}
	public void setMsgCount(int msgCount) {
		this.msgCount = msgCount;
	}
	public ChatListBean() {
		super();
		// TODO Auto-generated constructor stub
	}
	public ChatListBean(User user,DfMessage msg) {
		this.userid=user.getId();
		this.friendid=msg.getSendUid().equals(user.getId())?msg.getReceiverUid():msg.getSendUid();
		this.ctime=msg.getCtime();
		this.userlogo=msg.getUserlogo();
		this.nickname=msg.getNickname();
		switch (msg.getMsgtype()) {
		case 0:
			this.content=msg.getContent();
			break;
		case 1:
			this.content="[图片]";
			break;
		case 2:
			this.content="[语音]";
			break;
		default:
			break;
		}
		
	}
	
	public ChatListBean(User user,DfMessage msg,User friend) {
		this.userid=user.getId();
		this.friendid=friend.getId();
		this.ctime=msg.getCtime();
		this.userlogo=friend.getAvatar();
		this.nickname=friend.getNickname();
		switch (msg.getMsgtype()) {
		case 0:
			this.content=msg.getContent();
			break;
		case 1:
			this.content="[图片]";
			break;
		case 2:
			this.content="[语音]";
			break;
		default:
			break;
		}
		
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getFriendid() {
		return friendid;
	}
	public void setFriendid(String friendid) {
		this.friendid = friendid;
	}
	public String getCtime() {
		return ctime;
	}
	public void setCtime(String ctime) {
		this.ctime = ctime;
	}
}
