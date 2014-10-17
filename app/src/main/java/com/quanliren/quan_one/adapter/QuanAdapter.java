package com.quanliren.quan_one.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.image.ImageBrowserActivity;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity;
import com.quanliren.quan_one.bean.DongTaiBean;
import com.quanliren.quan_one.bean.MessageList;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

public class QuanAdapter extends ParentsAdapter {

	IQuanAdapter listener;
	int imgWidth=0;
	public QuanAdapter(Context c, List list,IQuanAdapter listener) {
		super(c, list);
		imgWidth=(c.getResources().getDisplayMetrics().widthPixels-ImageUtil.dip2px(c, 104))/3;
		this.listener=listener;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup arg2) {
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(c, R.layout.quan_item, null);
			holder.gridView = (GridView) convertView.findViewById(R.id.pic_gridview);
			
			holder.userlogo = (ImageView) convertView
					.findViewById(R.id.userlogo);
			holder.username = (TextView) convertView
					.findViewById(R.id.nickname);
			holder.sex = (TextView) convertView.findViewById(R.id.sex);
			holder.signature = (TextView) convertView
					.findViewById(R.id.signature);
			holder.time = (TextView) convertView.findViewById(R.id.time);
//			holder.delete = convertView.findViewById(R.id.delete);
			holder.adapter=new QuanPicAdapter(c, new ArrayList<String>(),imgWidth);
			holder.gridView.setAdapter(holder.adapter);
			holder.lp=new LayoutParams(LayoutParams.WRAP_CONTENT, imgWidth);
			holder.lp.addRule(RelativeLayout.BELOW, R.id.signature);
			holder.location=(TextView) convertView.findViewById(R.id.location);
			holder.reply_btn=(TextView) convertView.findViewById(R.id.reply_btn);
			holder.gridView.setLayoutParams(holder.lp);
			holder.vip=convertView.findViewById(R.id.vip);
			holder.reply_ll=convertView.findViewById(R.id.reply_ll);
			holder.content_rl=convertView.findViewById(R.id.content_rl);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		DongTaiBean db = (DongTaiBean) list.get(position);
		if (db.getImglist() == null || db.getImglist().size() == 0) {
			holder.gridView.setVisibility(View.GONE);
		} else {
			holder.gridView.setVisibility(View.VISIBLE);
			holder.adapter.setList(db.getImglist());
			holder.adapter.notifyDataSetChanged();
			holder.lp=(LayoutParams) holder.gridView.getLayoutParams();
			holder.lp.height=imgWidth*Util.getLines(db.getImglist().size(), 3);
			int num=(db.getImglist().size()>3?3:db.getImglist().size());
			int lpwidth=((num-1)*ImageUtil.dip2px(c, 4))+num*imgWidth;
			holder.lp.width=lpwidth;
			holder.gridView.setNumColumns(num);
			holder.gridView.setLayoutParams(holder.lp);
		}
		ImageLoader.getInstance().displayImage(
				db.getAvatar() + StaticFactory._160x160, holder.userlogo,ac.options_userlogo);
		holder.username.setText(db.getNickname());
		holder.time.setText(Util.getTimeDateStr(db.getCtime()));
		if (db.getContent().trim().length() > 0) {
			holder.signature.setVisibility(View.VISIBLE);
			holder.signature.setText(db.getContent());
		} else {
			holder.signature.setVisibility(View.GONE);
		}
		switch (Integer.valueOf(db.getSex())) {
		case 0:
			holder.sex.setBackgroundResource(R.drawable.girl_icon);
			break;
		case 1:
			holder.sex.setBackgroundResource(R.drawable.boy_icon);
			break;
		default:
			break;
		}
		holder.sex.setText(db.getAge());

		holder.userlogo.setTag(position);
		holder.userlogo.setOnClickListener(userlogo);
		holder.location.setText(db.getArea());
		holder.reply_btn.setText(db.getCnum());
		holder.content_rl.setTag(db);
		holder.content_rl.setOnClickListener(detailClick);
		if(db.getIsvip()==1){
			holder.vip.setVisibility(View.VISIBLE);
			holder.username.setTextColor(c.getResources().getColor(R.color.vip_name));
		}else{
			holder.vip.setVisibility(View.GONE);
			holder.username.setTextColor(c.getResources().getColor(R.color.username));
		}
		if(Integer.valueOf(db.getCnum())<=0){
			holder.reply_ll.setVisibility(View.GONE);
		}else{
			holder.reply_ll.setVisibility(View.VISIBLE);
		}
		return convertView;
	}

	class ViewHolder {
		ImageView userlogo;
		GridView gridView;
		QuanPicAdapter adapter;
		TextView username, sex, time, signature,reply_btn,location;
		LayoutParams lp;
		View content_rl;
		View vip,reply_ll;
	}
	

	OnClickListener detailClick=new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			listener.detailClick((DongTaiBean)v.getTag());
			/*Intent i = new Intent(c,DongTaiDetail.class);
			i.putExtra("bean", (DongTaiBean)v.getTag());
			((QuanFragment)c).startActivityForResult(i,1);
			*/
		}
	};


	OnClickListener userlogo = new OnClickListener() {

		@Override
		public void onClick(View v) {
			DongTaiBean db = (DongTaiBean) list.get(Integer.valueOf(v.getTag()
					.toString()));
			Intent i = new Intent(c, db.getUserid()
					.equals(ac.getUser().getId()) ? UserInfoActivity.class
					: UserOtherInfoActivity.class);
			i.putExtra("id", db.getUserid());
			c.startActivity(i);
		}
	};
	
	public interface IQuanAdapter{
		public void detailClick(DongTaiBean bean);
	}
}
