package com.quanliren.quan_one.fragment;/*package com.quanliren.quan_one.fragment;

import java.util.ArrayList;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.PropertiesActivity;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.LoginActivity;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.adapter.ParentsAdapter;
import com.quanliren.quan_one.application.AppClass;
import com.quanliren.quan_one.bean.LeftMenuItem;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.Util;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class SampleListFragment extends ListFragment {

	private List<LeftMenuItem> list; //数据源
	ImageView userlogo;
	TextView username;
	private SampleAdapter adapter;
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.menu_list, null);
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		View head=null;
		getListView().addHeaderView(head=View.inflate(getActivity(), R.layout.menu_list_head, null));
		userlogo= (ImageView) head.findViewById(R.id.userlogo);
		username=(TextView)head.findViewById(R.id.username);
		userlogo.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent i = new Intent(getActivity(),UserInfoActivity.class);
				getActivity().startActivity(i);
			}
		});
		list=new ArrayList<LeftMenuItem>();
		list.add(new LeftMenuItem(R.drawable.menu_home_icon_normal,R.drawable.menu_home_icon_normal,"最新加入",true,HomeFragment.class)); 
//		list.add(new LeftMenuItem(R.drawable.menu_raking_icon_normal,R.drawable.menu_raking_icon_normal,"最强排行",false,RankingFragment.class));
		list.add(new LeftMenuItem(R.drawable.menu_nearpeo_icon_normal,R.drawable.menu_nearpeo_icon_normal,"附近的人",false,NearPeopleFragment.class));
		list.add(new LeftMenuItem(R.drawable.menu_group_icon_normal,R.drawable.menu_group_icon_normal,"高大上群",false,GroupFragment.class));
		list.add(new LeftMenuItem(R.drawable.menu_quan_icon_normal,R.drawable.menu_quan_icon_normal,"陪游信息",false,QuanFragment.class));
		list.add(new LeftMenuItem(R.drawable.menu_shop_icon_normal,R.drawable.menu_shop_icon_normal,"陪游商城",false,ShopFragment.class));
		list.add(new LeftMenuItem(R.drawable.menu_seting_icon_normal,R.drawable.menu_seting_icon_normal,"设置",false,SetingFragment.class));
		
		//自定义适配器
		adapter = new SampleAdapter(getActivity().getApplication(),list);
		setListAdapter(adapter);
	}

	@Override
	public void onListItemClick(ListView lv, View v, int position, long id) {
		if(position>0){
			LeftMenuItem lmi=list.get(position-1);
			if(getActivity()==null){
				return;
			}
			PropertiesActivity pa=(PropertiesActivity) getActivity();
			pa.switchContent(lmi.clazz);
			for (LeftMenuItem item : list) {
				item.focus=false;
			}
			lmi.focus=true;
			adapter.notifyDataSetChanged();
		}
	}
	
	public class SampleAdapter extends ParentsAdapter {

		public SampleAdapter(Context context,List list) {
			super(context, list);
		}

		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder;
			if (convertView == null) {
				holder=new ViewHolder();
				convertView = LayoutInflater.from(c).inflate(R.layout.menu_list_item, null);
				holder.icon=(ImageView) convertView.findViewById(R.id.row_icon);
				holder.title=(TextView)convertView.findViewById(R.id.row_title);
				convertView.setTag(holder);
			}else{
				holder=(ViewHolder) convertView.getTag();
			}
			LeftMenuItem lmi=(LeftMenuItem) list.get(position);
			holder.icon.setImageResource(lmi.icon);
			if(lmi.focus)
				holder.title.setTextColor(getResources().getColor(R.color.title_press_txt));
			else
				holder.title.setTextColor(getResources().getColor(R.color.title));
			holder.title.setText(lmi.title);
			return convertView;
		}
		
		@Override
		public Object getItem(int position) {
			return super.getItem(position-1);
		}
		
		class ViewHolder{
			ImageView icon;
			TextView title;
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		AppClass ac=(AppClass) getActivity().getApplication();
		User user=ac.getUserInfo();
		if(user!=null){
			username.setText(user.getNickname());
			if(Util.isStrNotNull(user.getAvatar())){
				ImageLoader.getInstance().displayImage(user.getAvatar()+StaticFactory._320x320, userlogo);
			}
		}else{
			username.setText("");
			userlogo.setImageResource(R.drawable.defalut_logo);
		}
	}
}
*/