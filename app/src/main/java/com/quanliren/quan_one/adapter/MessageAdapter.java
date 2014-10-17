package com.quanliren.quan_one.adapter;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.AnimationDrawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.DfMessage;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.service.SocketManage;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

public class MessageAdapter extends ParentsAdapter {
	AppClass ac;
	User friend;
	User user;
	Handler hanlder;

	public void setFriend(User friend) {
		this.friend = friend;
	}

	private int COME_MSG = 0;
	private int TO_MSG = 1;

	public Object getItem(int position) {
		return super.getItem((position - 1));
	}

	public void removeObj(int position) {
		super.removeObj((position - 1));
	}

	public void addNewsItem(Object newsitem) {
		super.addNewsItem(newsitem);
	}

	private int windowWidth;

	public MessageAdapter(Context c, List list, User friend, Handler hanler) {
		super(c, list);
		this.friend = friend;
		this.hanlder = hanler;
		ac = (AppClass) c.getApplicationContext();
		user = ac.getUserInfo();
		windowWidth = ImageUtil.px2dip(c,
				c.getResources().getDisplayMetrics().widthPixels) - 130;
	}

	public int getItemViewType(int position) {
		DfMessage entity = (DfMessage) getList().get(position);
		if (entity.getSendUid().equals(friend.getId())) {
			return COME_MSG;
		} else {
			return TO_MSG;
		}
	}

	public int getViewTypeCount() {
		return 2;
	}

	LinearLayout.LayoutParams lpn = new LinearLayout.LayoutParams(
			LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);

	public View getView(int position, View convertView, ViewGroup arg2) {
		DfMessage dm = (DfMessage) getList().get(position);
		int isComMsg = getItemViewType(position);
		ViewHolder holder = null;

		if (convertView == null) {
			holder = new ViewHolder();
			if (isComMsg == COME_MSG) {
				convertView = View.inflate(c,
						R.layout.chatting_item_msg_text_left, null);
			} else {
				convertView = View.inflate(c,
						R.layout.chatting_item_msg_text_right, null);
			}
			holder.progress = convertView.findViewById(R.id.progress);
			holder.user_logo = (ImageView) convertView
					.findViewById(R.id.chat_user_logo);
			holder.img_ll = convertView.findViewById(R.id.img_ll);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.timel=(TextView)convertView.findViewById(R.id.timel);
			holder.img = (ImageView) convertView.findViewById(R.id.img);
			holder.content = (TextView) convertView
					.findViewById(R.id.chat_context_tv);
			holder.error_btn = convertView.findViewById(R.id.error_btn);
			holder.voice = (ImageView) convertView.findViewById(R.id.voice);
			holder.voice_ll = convertView.findViewById(R.id.voice_ll);
			holder.content.setMaxWidth(ImageUtil.dip2px(c, windowWidth));
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.error_btn.setVisibility(View.GONE);
		holder.progress.setVisibility(View.GONE);
		if (isComMsg == COME_MSG) {
			ImageLoader.getInstance().displayImage(
					friend.getAvatar() + StaticFactory._160x160,
					holder.user_logo,ac.options_userlogo);
			holder.user_logo.setOnClickListener(logo_click);
		} else {
			ImageLoader.getInstance()
					.displayImage(user.getAvatar() + StaticFactory._160x160,
							holder.user_logo,ac.options_userlogo);
		}
		if (dm.getDownload() == SocketManage.D_downloading) {
			holder.progress.setVisibility(View.VISIBLE);
		} else if (dm.getDownload() == SocketManage.D_destroy) {
			holder.error_btn.setVisibility(View.VISIBLE);
			holder.error_btn.setTag(dm);
			holder.error_btn.setOnClickListener(logo_click);
		}
		holder.content.setVisibility(View.GONE);
		holder.img_ll.setVisibility(View.GONE);
		holder.voice_ll.setVisibility(View.GONE);
		holder.voice_ll.setLayoutParams(lpn);
		holder.time.setVisibility(View.GONE);
		holder.timel.setVisibility(View.GONE);
		switch (dm.getMsgtype()) {
		case 0:
			holder.content.setVisibility(View.VISIBLE);
			holder.content.setText(dm.getContent());
			holder.content.setTag(dm);
			holder.content.setOnLongClickListener(long_click);
			break;
		case 1:
			holder.img_ll.setVisibility(View.VISIBLE);
			holder.img_ll.setTag(dm);
			holder.img_ll.setOnLongClickListener(long_click);
			holder.img_ll.setOnClickListener(voice_click);
			if (dm.getContent().startsWith("http://")) {
				ImageLoader.getInstance().displayImage(
						dm.getContent() + StaticFactory._160x160, holder.img,
						ac.options_chat);
			} else {
				ImageLoader.getInstance().displayImage(
						Util.FILE + dm.getContent(), holder.img,
						ac.options_chat);
			}
			break;
		case 2:
			holder.voice_ll.setVisibility(View.VISIBLE);
			holder.voice_ll.setTag(dm);
			holder.voice_ll.setOnLongClickListener(long_click);
			holder.voice_ll.setOnClickListener(voice_click);
			holder.timel.setVisibility(View.VISIBLE);
			holder.timel.setText(dm.getTimel()+"''");
			int cha = dm.getTimel() - 5;
			if (cha > 0) {
				int sum = 60 + (cha * 3);
				if (sum > 150) {
					sum = 150;
				}
				if (holder.lp == null) {
					holder.lp = new LinearLayout.LayoutParams(ImageUtil.dip2px(
							c, sum), LayoutParams.WRAP_CONTENT);
				} else {
					holder.lp.width = ImageUtil.dip2px(c, sum);
				}
				holder.voice_ll.setLayoutParams(holder.lp);
			}
			if (dm.isPlaying()) {
				if (isComMsg == COME_MSG) {
					holder.voice.setImageDrawable(c.getResources().getDrawable(
							R.drawable.chat_left_animation));
				} else {
					holder.voice.setImageDrawable(c.getResources().getDrawable(
							R.drawable.chat_right_animation));
				}
				AnimationDrawable animationDrawable = (AnimationDrawable) holder.voice
						.getDrawable();
				animationDrawable.start();
			} else {
				if (isComMsg == COME_MSG) {
					holder.voice.setImageDrawable(c.getResources().getDrawable(
							R.drawable.chat_left_voice3));
				} else {
					holder.voice.setImageDrawable(c.getResources().getDrawable(
							R.drawable.chat_right_voice3));
				}
			}
			break;
		default:
			break;
		}
		holder.content.setText(dm.getContent());
		try {
			if (dm.isShowTime()
					|| (position > 0 && Util.fmtDateTime.parse(dm.getCtime())
							.getTime() - 60 * 1000 > Util.fmtDateTime.parse(
							((DfMessage) getList().get(position - 1))
									.getCtime()).getTime())) {
				holder.time.setVisibility(View.VISIBLE);
				holder.time.setText(Util.getChatTime(dm.getCtime()));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return convertView;
	}

	static class ViewHolder {
		ImageView user_logo, img, voice;
		TextView content, time,timel;
		View img_ll, voice_ll;
		View progress, error_btn;
		LinearLayout.LayoutParams lp;
	}

	OnClickListener voice_click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			DfMessage msg = (DfMessage) v.getTag();
			Message ms = hanlder.obtainMessage();
			ms.what = msg.getMsgtype();
			ms.obj = msg;
			ms.sendToTarget();
		}
	};
	OnClickListener logo_click = new OnClickListener() {

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.chat_user_logo:
				Intent i = new Intent(c, UserOtherInfoActivity.class);
				i.putExtra("id", friend.getId());
				c.startActivity(i);
				break;
			case R.id.error_btn:
				DfMessage msg = (DfMessage) v.getTag();
				Message ms = hanlder.obtainMessage();
				ms.what=6;
				ms.obj=msg;
				ms.sendToTarget();
				break;
			default:
				break;
			}
			
		}
	};

	OnLongClickListener long_click = new OnLongClickListener() {

		@Override
		public boolean onLongClick(View v) {
			DfMessage msg = (DfMessage) v.getTag();
			Message ms = hanlder.obtainMessage();
			switch (v.getId()) {
			case R.id.chat_context_tv:
				ms.what = 3;
				ms.obj = msg;
				ms.sendToTarget();
				break;
			case R.id.img_ll:
				ms.what = 4;
				ms.obj = msg;
				ms.sendToTarget();
				break;
			case R.id.voice_ll:
				ms.what =5;
				ms.obj = msg;
				ms.sendToTarget();
				break;
			}
			return true;
		}
	};
}
