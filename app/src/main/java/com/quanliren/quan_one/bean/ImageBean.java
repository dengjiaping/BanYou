package com.quanliren.quan_one.bean;

import java.io.Serializable;

public class ImageBean implements Serializable{
	public int position;
	public String imgpath;
	public int[] wh;
	public ImageBean(int position, String path, int[] wh) {
		super();
		this.position = position;
		this.imgpath = path;
		this.wh = wh;
	}
	public ImageBean(int position, String path) {
		super();
		this.position = position;
		this.imgpath = path;
	}
	public ImageBean() {
		super();
	}
}
