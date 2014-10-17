package com.quanliren.quan_one.adapter;

import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.bean.SetBean;
import com.quanliren.quan_one.util.ImageUtil;

public class SetAdapter extends ParentsAdapter{

	public SetAdapter(Context c, List list) {
		super(c, list);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if(convertView==null){
			holder=new ViewHolder();
			convertView=View.inflate(c, R.layout.seting_item, null);
			holder.icon=(ImageView) convertView.findViewById(R.id.icon);
			holder.text=(TextView) convertView.findViewById(R.id.text);
			holder.center=(LinearLayout) convertView.findViewById(R.id.center);
			holder.lp1=(LinearLayout.LayoutParams) holder.center.getLayoutParams();
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		SetBean sb=(SetBean) list.get(position);
		holder.icon.setImageResource(sb.icon);
		holder.text.setText(sb.title);
		if(sb.isFirst){
			holder.lp1.topMargin=ImageUtil.dip2px(c, 8);
		}else{
			holder.lp1.topMargin=ImageUtil.dip2px(c, 1);
		}
		holder.center.setLayoutParams(holder.lp1);
		return convertView;
	}

	class ViewHolder{
		ImageView icon;
		TextView text;
		LinearLayout center;
		LinearLayout.LayoutParams lp1;
	}
}
