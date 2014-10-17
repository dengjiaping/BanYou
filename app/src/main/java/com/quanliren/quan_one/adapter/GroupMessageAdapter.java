package com.quanliren.quan_one.adapter;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.MessageListBean;
import com.quanliren.quan_one.custom.IMessageCustomLinearLayout;
import com.quanliren.quan_one.custom.MessageCustomLinearLayout;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

public class GroupMessageAdapter extends ParentsAdapter implements
		IMessageCustomLinearLayout {

	private static final int GROUPMESSAGE = 0;
	private static final int GROUPNOTIFY = 1;
	public Handler handler = null;
	private AtomicBoolean init = new AtomicBoolean(false);

	public GroupMessageAdapter(Context c, List list) {
		super(c, list);
	}

	@Override
	public int getItemViewType(int position) {
		MessageListBean entity = (MessageListBean) getList().get(position);
		return entity.getMsgtype();
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		MessageListBean bean = (MessageListBean) getItem(position);
		int type = getItemViewType(position);
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			switch (type) {
			case GROUPMESSAGE:
				convertView = View.inflate(c,
						R.layout.leave_message_item_group_message, null);
				holder.yes = convertView.findViewById(R.id.yes);
				holder.no = convertView.findViewById(R.id.no);
				holder.status_txt=(TextView) convertView.findViewById(R.id.status_txt);
				break;
			case GROUPNOTIFY:
				convertView = View.inflate(c,
						R.layout.leave_message_item_notify, null);
				break;
			}
			holder.top = (MessageCustomLinearLayout) convertView
					.findViewById(R.id.top);
			holder.top.setListener(this);
			holder.userlogo = (ImageView) convertView
					.findViewById(R.id.userlogo);
			holder.username = (TextView) convertView
					.findViewById(R.id.username);
			holder.signature = (TextView) convertView
					.findViewById(R.id.signature);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.delete = convertView.findViewById(R.id.delete);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		ImageLoader.getInstance().displayImage(
				bean.getAvatar() + StaticFactory._320x320, holder.userlogo);
		holder.time.setText(Util.getTimeDateStr(bean.getCreateTime()));
		holder.signature.setText(bean.getContent());
		switch (type) {
		case GROUPNOTIFY:
			holder.username.setText(bean.getNickname());
			break;
		case GROUPMESSAGE:
			holder.username.setText(bean.getCrowdname());
			holder.yes.setOnTouchListener(btnTouch);
			if (bean.getApplystatus().equals("0")) {
				holder.yes.setVisibility(View.VISIBLE);
				holder.no.setVisibility(View.VISIBLE);
				holder.top.changeBtn(3);
				holder.status_txt.setText("");
			}else if(bean.getApplystatus().equals("1")){
				holder.yes.setVisibility(View.GONE);
				holder.no.setVisibility(View.GONE);
				holder.top.changeBtn(1);
				holder.status_txt.setText("[已同意]");
			}else if(bean.getApplystatus().equals("2")){
				holder.yes.setVisibility(View.GONE);
				holder.no.setVisibility(View.GONE);
				holder.top.changeBtn(1);
				holder.status_txt.setText("[已拒绝]");
			}

			holder.yes.setTag(bean);
			holder.yes.setOnTouchListener(btnTouch);
			holder.no.setTag(bean);
			holder.no.setOnTouchListener(btnTouch);
			break;
		}
		holder.delete.setTag(bean);
		holder.delete.setOnTouchListener(btnTouch);
		holder.userlogo.setTag(position);
		holder.userlogo.setOnClickListener(userlogo);
		if (position == 0 && init.compareAndSet(false, true)) {
			holder.top.open();
		}
		holder.top.setTag(position);
		return convertView;
	}

	class ViewHolder {
		ImageView userlogo;
		TextView username, sex, time, signature, messagecount,status_txt;
		View yes, no, delete, click_item;
		MessageCustomLinearLayout top;
	}

	OnClickListener userlogo = new OnClickListener() {

		@Override
		public void onClick(View v) {
			Toast.makeText(c, "点击", Toast.LENGTH_SHORT).show();
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
				case R.id.yes:
					msg = handler.obtainMessage();
					msg.what = 2;
					msg.obj = v.getTag();
					msg.sendToTarget();
					break;
				case R.id.no:
					msg = handler.obtainMessage();
					msg.what = 3;
					msg.obj = v.getTag();
					msg.sendToTarget();
					break;
				default:
					break;
				}
			}
			return true;
		}
	};

	@Override
	public void change(MessageCustomLinearLayout layout) {
		if (this.layout != null && !this.layout.equals(layout)) {
			this.layout.close();
		}
		this.layout = layout;
	}

	MessageCustomLinearLayout layout;

	@Override
	public void startActivity(View v) {
	}

}
