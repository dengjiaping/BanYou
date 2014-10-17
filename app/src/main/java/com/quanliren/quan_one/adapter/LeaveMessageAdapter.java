package com.quanliren.quan_one.adapter;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity;
import com.quanliren.quan_one.bean.ChatListBean;
import com.quanliren.quan_one.bean.MessageListBean;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

public class LeaveMessageAdapter extends ParentsAdapter{

	public Handler handler = null;

	public LeaveMessageAdapter(Context c, List list) {
		super(c, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ChatListBean bean = (ChatListBean) getItem(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(c, R.layout.leave_message_item_normal,
					null);
			holder.click_item = convertView.findViewById(R.id.click_item);
			holder.messagecount = (TextView) convertView  
					.findViewById(R.id.messagecount);
			holder.userlogo = (ImageView) convertView
					.findViewById(R.id.userlogo);
			holder.username = (TextView) convertView
					.findViewById(R.id.username);
			holder.signature = (TextView) convertView
					.findViewById(R.id.signature);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImageLoader.getInstance().displayImage(
				bean.getUserlogo()+ StaticFactory._160x160, holder.userlogo,ac.options_userlogo);
		holder.time.setText(Util.getTimeDateStr(bean.getCtime()));
		holder.signature.setText(bean.getContent());
		holder.username.setText(bean.getNickname());
		if (bean.getMsgCount()!= 0) {
			holder.messagecount.setVisibility(View.VISIBLE);
			holder.messagecount.setText(bean.getMsgCount()+"");
		} else {
			holder.messagecount.setVisibility(View.GONE);
		}
		holder.userlogo.setTag(bean.getFriendid());
		holder.userlogo.setOnClickListener(userlogo);
		return convertView;
	}

	class ViewHolder {
		ImageView userlogo;
		TextView username, time, signature, messagecount;
		View yes, no, click_item;
	}

	OnClickListener userlogo = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Intent i = new Intent(c, v.getTag().toString()
					.equals(ac.getUser().getId()) ? UserInfoActivity.class
					: UserOtherInfoActivity.class);
			i.putExtra("id", v.getTag().toString());
			c.startActivity(i);
		}
	};

	OnTouchListener btnTouch = new OnTouchListener() {

		@Override
		public boolean onTouch(View v, MotionEvent event) {
			int action = event.getAction();
			switch (action) {
			case MotionEvent.ACTION_UP:
				Message msg;
				switch (v.getId()) {
				case R.id.delete:
					msg = handler.obtainMessage();
					msg.what = 1;
					msg.obj = v.getTag();
					msg.sendToTarget();
					break;
				}
			}
			return true;
		}
	};

	/*@Override
	public void startActivity(View v) {
		int position = Integer.valueOf(v.getTag().toString());
		MessageListBean bean = (MessageListBean) getItem(position);
		if (bean.getMsgtype() == 0) {
			User user = new User(bean);
			Intent i = new Intent(c, ChatActivity.class);
			i.putExtra("friend", user);
			((PropertiesActivity) c).startActivityForResult(i, 1);
		}
	}*/
}
