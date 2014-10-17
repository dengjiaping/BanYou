package com.quanliren.quan_one.bean;

import java.io.Serializable;
import java.text.ParseException;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.quanliren.quan_one.util.Util;

public class User implements Serializable {
	private String id;
	private String avatar;
	private String mobile;
	private String pwd;
	private String sex;
	private String nickname;
	private String signature;
	private String qq;
	private String job;
	private String hobby;
	private int isvip;
	private String height;
	private String weight;
	private String nature;
	private String hometown;
	private String birthday;
	private int isblacklist;
	private String education;
	private String avtwidth;
	private String avtheight;
	private String connum;
	private String viptime;
	private String vipday;
	private String attenstatus;
	private String powernum;
	private String distance;
	private String levelname;
	private String dyid;
	private String dycontent;
	private String dyimgurl;
	private String dytime;
	private String constellation;
	private String cityname;
	private String levelid;
	private double longitude;
	private double latitude;
	public double getLongitude() {
		return longitude;
	}

	public String getViptime() {
		return viptime;
	}

	public void setViptime(String viptime) {
		this.viptime = viptime;
	}

	public String getVipday() {
		return vipday;
	}

	public void setVipday(String vipday) {
		this.vipday = vipday;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}


	private String token;
	private int userrole;
	private String userid;
	private String ctime;
	public String getCtime() {
		return ctime;
	}

	public void setCtime(String ctime) {
		this.ctime = ctime;
	}

	public int getIsblacklist() {
		return isblacklist;
	}

	public void setIsblacklist(int isblacklist) {
		this.isblacklist = isblacklist;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getDistance() {
		return distance;
	}

	public void setDistance(String distance) {
		this.distance = distance;
	}
	public int getUserrole() {
		return userrole;
	}

	public void setUserrole(int userrole) {
		this.userrole = userrole;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}


	public String getLevelid() {
		if(levelid.equals("")){
			return "0";
		}
		return levelid;
	}

	public void setLevelid(String levelid) {
		this.levelid = levelid;
	}


	private String imgs;
	public String getCityname() {
		return cityname;
	}

	public void setCityname(String cityname) {
		this.cityname = cityname;
	}
	
	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}
	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}
	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}


	public int getAvtwidth() {
		if (avtwidth == null || avtwidth.equals("")) {
			return 0;
		}
		return Integer.valueOf(avtwidth);
	}

	public void setAvtwidth(String avtwidth) {
		this.avtwidth = avtwidth;
	}

	public int getAvtheight() {
		if (avtheight == null || avtheight.equals("")) {
			return 0;
		}
		return Integer.valueOf(avtheight);
	}

	public void setAvtheight(String avtheight) {
		this.avtheight = avtheight;
	}

	public String getImgs() {
		return new Gson().toJson(imglist);
	}

	public void setImgs(String imgs) {
		this.imglist = new Gson().fromJson(imgs,
				new TypeToken<List<ImageBean>>() {
				}.getType());
		this.imgs = imgs;
	}

	private List<ImageBean> imglist;

	public String getUserAge() {
		try {
			if (birthday == null || "".equals(birthday)) {
				return "0";
			} else {
				return Util.getAge(Util.fmtDate.parse(birthday));
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return "0";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}


	public String getNickname() {
		return nickname;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getQq() {
		return qq;
	}

	public void setQq(String qq) {
		this.qq = qq;
	}

	public String getJob() {
		return job;
	}

	public void setJob(String job) {
		this.job = job;
	}

	public String getHobby() {
		return hobby;
	}

	public void setHobby(String hobby) {
		this.hobby = hobby;
	}


	public String getHeight() {
		return height;
	}

	public void setHeight(String height) {
		this.height = height;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getNature() {
		return nature;
	}

	public void setNature(String nature) {
		this.nature = nature;
	}

	public String getHometown() {
		return hometown;
	}

	public void setHometown(String hometown) {
		this.hometown = hometown;
	}

	public String getBirthday() {
		return birthday;
	}

	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}

	public String getEducation() {
		return education;
	}

	public void setEducation(String education) {
		this.education = education;
	}


	public String getConstellation() {
		return constellation;
	}

	public void setConstellation(String constellation) {
		this.constellation = constellation;
	}


	public List<ImageBean> getImglist() {
		return imglist;
	}

	public void setImglist(List<ImageBean> imglist) {
		this.imglist = imglist;
	}

	public User(String id, String avatar, String userName, String passWord,
			int sex, String nickname, String signature, String qq, String job,
			String hobby, int isvip, String height, String weight,
			String nature, String hometown, String birthday, String education,
			int powernum, String constellation, int isOnline,
			List<ImageBean> imglist) {
		super();
		this.id = id;
		this.avatar = avatar;
		this.nickname = nickname;
		this.signature = signature;
		this.qq = qq;
		this.job = job;
		this.hobby = hobby;
		this.height = height;
		this.weight = weight;
		this.nature = nature;
		this.hometown = hometown;
		this.birthday = birthday;
		this.education = education;
		this.constellation = constellation;
		this.imglist = imglist;
	}


	public int getIsvip() {
		return isvip;
	}

	public void setIsvip(int isvip) {
		this.isvip = isvip;
	}

	public String getConnum() {
		if(connum.equals("")){
			return "0";
		}
		return connum;
	}

	public void setConnum(String connum) {
		this.connum = connum;
	}

	public String getAttenstatus() {
		return attenstatus;
	}

	public void setAttenstatus(String attenstatus) {
		this.attenstatus = attenstatus;
	}

	public String getPowernum() {
		if(powernum==null||"".equals(powernum)){
			return "0";
		}
		return powernum;
	}

	public void setPowernum(String powernum) {
		this.powernum = powernum;
	}

	public User() {
		super();
		// TODO Auto-generated constructor stub
	}
	public User(String id,String avatar,String nickname){
		this.avatar=avatar;
		this.nickname=nickname;
		this.id=id;
	}
	public User(MessageListBean bean){
		this.avatar=bean.getAvatar();
		this.nickname=bean.getNickname();
		this.id=bean.getSenduid();
	}

	public String getLevelname() {
		return levelname;
	}

	public void setLevelname(String levelname) {
		this.levelname = levelname;
	}

	public String getDyid() {
		return dyid;
	}

	public void setDyid(String dyid) {
		this.dyid = dyid;
	}

	public String getDycontent() {
		return dycontent;
	}

	public void setDycontent(String dycontent) {
		this.dycontent = dycontent;
	}

	public String getDyimgurl() {
		return dyimgurl;
	}

	public void setDyimgurl(String dyimgurl) {
		this.dyimgurl = dyimgurl;
	}

	public String getDytime() {
		return dytime;
	}

	public void setDytime(String dytime) {
		this.dytime = dytime;
	}

}
