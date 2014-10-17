package com.quanliren.quan_one.adapter;

import java.util.List;

import com.quanliren.quan_one.activity.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

public class GiftAdapter extends ParentsAdapter{

	public GiftAdapter(Context c, List list) {
		super(c, list);
		// TODO Auto-generated constructor stub
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(c, R.layout.gift_item, null);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		return convertView;
	}

	class ViewHolder{
		
	}
}
