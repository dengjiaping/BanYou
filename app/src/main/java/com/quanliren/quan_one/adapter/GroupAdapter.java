package com.quanliren.quan_one.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.util.StaticFactory;

public class GroupAdapter extends ParentsAdapter {
	
	public GroupAdapter(Context c, List list) {
		super(c, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(c, R.layout.group_item, null);
			holder.logo=(ImageView) convertView.findViewById(R.id.grouplogo);
			holder.name=(TextView) convertView.findViewById(R.id.groupname);
			holder.desc=(TextView) convertView.findViewById(R.id.desc);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		GroupBean gb=(GroupBean) list.get(position);
		ImageLoader.getInstance().displayImage(gb.getAvatar()+StaticFactory._160x160, holder.logo);
		holder.name.setText(gb.getCrowdname());
		holder.desc.setText(gb.getSummary());
		return convertView;
	}

	class ViewHolder {
		ImageView logo;
		TextView name,desc;
	}
}
