package com.quanliren.quan_one.bean;

import java.io.Serializable;
import java.net.URLConnection;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.net.tsz.afinal.annotation.sqlite.Id;
import com.net.tsz.afinal.annotation.sqlite.Index;
import com.net.tsz.afinal.annotation.sqlite.Table;
import com.net.tsz.afinal.annotation.sqlite.Transient;
import com.quanliren.quan_one.service.SocketManage;
import com.quanliren.quan_one.util.Util;

@Table(name = "DfMessage")
public class DfMessage implements Serializable {
	@Transient
	public static final String TABLENAME = "DfMessage";
	@Id
	private int id;
	@Index
	private String msgid;
	@Index
	private String userid;
	@Index
	private String receiverUid;
	@Index
	private String sendUid;
	private String content;
	@Index
	private int isRead = 0;// 是否已读
	@Index
	private String ctime;// 信息发送时间
	@Transient
	private boolean showTime = false;// 是否显示信息
	@Index
	private int msgtype = 0;// 0、文字 1、图片 2、语音
	@Index
	private int download = 0;// 0 未下载 1已下载
	private int timel = 0;// 语音长度
	private String userlogo;
	private String nickname;
	private int resendCount = 0;

	public int getResendCount() {
		return resendCount;
	}

	public void setResendCount(int resendCount) {
		this.resendCount = resendCount;
	}

	public int getDownload() {
		return download;
	}

	public void setDownload(int download) {
		this.download = download;
	}

	@Transient
	private boolean playing;

	public boolean isPlaying() {
		return playing;
	}

	public void setPlaying(boolean playing) {
		this.playing = playing;
	}

	public void setIsRead(int isRead) {
		this.isRead = isRead;
	}


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

	public Integer getIsRead() {
		return isRead;
	}

	public void setIsRead(Integer isRead) {
		this.isRead = isRead;
	}

	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public boolean isShowTime() {
		return showTime;
	}

	public void setShowTime(boolean showTime) {
		this.showTime = showTime;
	}


	public void setMsgtype(Integer msgtype) {
		this.msgtype = msgtype;
	}

	public int getMsgtype() {
		return msgtype;
	}

	public void setMsgtype(int msgtype) {
		this.msgtype = msgtype;
	}

	public DfMessage() {
		super();
		// TODO Auto-generated constructor stub
	}

	public static JSONObject getMessage(User user, String content, User friend,
			int msgtype, int timel) {
		try {
			JSONObject msg = new JSONObject();
			msg.put("content", content);
			/*try {
				java.net.URL url = new java.net.URL("http://www.bjtime.cn");// 取得资源对象
				URLConnection uc = url.openConnection();// 生成连接对象
				uc.connect(); // 发出连接
				long ld = uc.getDate(); // 取得网站日期时间
				Date date = new Date(ld); // 转换为标准时间对象
				msg.put("ctime", Util.fmtDateTime.format(date));
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				msg.put("ctime", Util.fmtDateTime.format(new Date()));
			}*/
			msg.put("ctime", Util.fmtDateTime.format(new Date()));
			msg.put("receiverUid", friend.getId());
			msg.put("userid", user.getId());
			msg.put("timel", timel);
			msg.put("userlogo", user.getAvatar());
			msg.put("nickname", user.getNickname());
			msg.put("sendUid", user.getId());
//			msg.put("download", 1);
			msg.put("msgtype", msgtype);
			msg.put(SocketManage.MESSAGE_ID, new Date().getTime());
			return msg;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public static JSONObject getMessage(DfMessage d) {
		try {
			JSONObject msg = new JSONObject();
			msg.put("content", d.getContent());
			msg.put("ctime", d.getCtime());
			msg.put("receiverUid", d.getReceiverUid());
			msg.put("userid", d.getUserid());
			msg.put("timel", d.getTimel());
			msg.put("userlogo", d.getUserlogo());
			msg.put("nickname", d.getNickname());
			msg.put("sendUid", d.getSendUid());
			msg.put("msgtype", d.getMsgtype());
			msg.put(SocketManage.MESSAGE_ID, d.getMsgid());
			return msg;
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	public DfMessage(int id, String msgid, String userid, String receiverUid,
			String sendUid, String content, int isRead, String ctime,
			boolean showTime, int msgtype, int download, int timel,
			String userlogo, String nickname) {
		super();
		this.id = id;
		this.msgid = msgid;
		this.userid = userid;
		this.receiverUid = receiverUid;
		this.sendUid = sendUid;
		this.content = content;
		this.isRead = isRead;
		this.ctime = ctime;
		this.showTime = showTime;
		this.msgtype = msgtype;
		this.download = download;
		this.timel = timel;
		this.userlogo = userlogo;
		this.nickname = nickname;
	}

	public int getTimel() {
		return timel;
	}

	public void setTimel(int timel) {
		this.timel = timel;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMsgid() {
		return msgid;
	}

	public void setMsgid(String msgid) {
		this.msgid = msgid;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}

	public String getReceiverUid() {
		return receiverUid;
	}

	public void setReceiverUid(String receiverUid) {
		this.receiverUid = receiverUid;
	}

	public String getSendUid() {
		return sendUid;
	}

	public void setSendUid(String sendUid) {
		this.sendUid = sendUid;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}
}
