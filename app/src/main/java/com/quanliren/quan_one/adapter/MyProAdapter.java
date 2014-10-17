package com.quanliren.quan_one.adapter;

import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.ProBean;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class MyProAdapter extends ParentsAdapter{

	public MyProAdapter(Context c, List list) {
		super(c, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(c, R.layout.my_pro_item, null);
			holder.img=(ImageView) convertView.findViewById(R.id.img);
			holder.title=(TextView) convertView.findViewById(R.id.title);
			holder.state=(TextView)convertView.findViewById(R.id.state);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		ProBean pb=(ProBean) list.get(position);
		holder.title.setText(pb.getTitle());
		switch (pb.getWinstate()) {
		case 0:
			holder.state.setText("[未使用]");
			break;
		case 1:
			holder.state.setText("[已使用]");
			break;
		case 2:
			holder.state.setText("[已退换]");
			break;
		case 3:
			holder.state.setText("[已过期]");
			break;
		case 4:
			holder.state.setText("[已删除]");
			break;
		case 5:
			holder.state.setText("[兑换中]");
			break;
		}
		return convertView;
	}

	class ViewHolder{
		ImageView img;
		TextView title,state;
	} 
}
