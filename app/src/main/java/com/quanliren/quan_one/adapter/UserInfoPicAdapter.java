package com.quanliren.quan_one.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseUserActivity;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.bean.ImageBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.custom.PopFactory;
import com.quanliren.quan_one.util.StaticFactory;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;

public class UserInfoPicAdapter extends ParentsAdapter {

	public UserInfoPicAdapter(Context c, List list) {
		super(c, list);
	}

	public boolean ismy=false;
	
	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(c, R.layout.user_info_pic_item, null);
			holder.iv = (ImageView) convertView.findViewById(R.id.img);
//			holder.iv.setLayoutParams(new LinearLayout.LayoutParams(c.getResources().getDisplayMetrics().widthPixels/4, c.getResources().getDisplayMetrics().widthPixels/4));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		ImageBean ib=(ImageBean) list.get(position);
		ImageLoader.getInstance().displayImage(ib.imgpath+StaticFactory._320x320, holder.iv);
		holder.iv.setTag(ib.position);
		holder.iv.setOnClickListener(imgClick);
		holder.iv.setOnLongClickListener(longClick);
		return convertView;
	}

	class ViewHolder {
		ImageView iv;
	}

	OnClickListener imgClick = new OnClickListener() {

		@Override
		public void onClick(View v) {
			try {
				if(c instanceof BaseUserActivity){
					((BaseUserActivity)c).viewPic((Integer)v.getTag());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	};
	
	OnLongClickListener longClick=new OnLongClickListener() {
		
		@Override
		public boolean onLongClick(View v) {
			try {
				if(c instanceof UserInfoActivity){
					((UserInfoActivity)c).picSeting((Integer)v.getTag());
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	};
}
