package com.quanliren.quan_one.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.ImageView;

public class QuanPicAdapter extends ParentsAdapter {

	int imgWidth;
	public QuanPicAdapter(Context c, List list,int imgwidth) {
		super(c, list);
		imgWidth=imgwidth;
	}

	public boolean ismy=false;
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		if (convertView == null) {
			convertView = View.inflate(c, R.layout.quan_pic_item, null);
			((ImageView)convertView).setLayoutParams(new AbsListView.LayoutParams(imgWidth, imgWidth));
		} 
		ImageBean ib=(ImageBean) list.get(position);
		ImageLoader.getInstance().displayImage(ib.imgpath+StaticFactory._320x320, (ImageView)convertView);
		((ImageView)convertView).setTag(position);
		((ImageView)convertView).setOnClickListener(imgClick);
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
	}

	OnClickListener imgClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			int positon=(Integer) v.getTag();
			Intent intent = new Intent(c, ImageBrowserActivity.class);
			intent.putExtra("position", positon);
			MessageList ml=new MessageList();
			ml.imgList=list;
			intent.putExtra("ismy", ismy);
			intent.putExtra("entity_profile", ml);
			c.startActivity(intent);
		}
	};
}
