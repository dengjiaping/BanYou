package com.quanliren.quan_one.fragment;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONObject;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.QuanPersonalActivity;
import com.quanliren.quan_one.activity.seting.AboutUsActivity;
import com.quanliren.quan_one.activity.seting.FunctionActivity;
import com.quanliren.quan_one.activity.seting.InviteFriendActivity;
import com.quanliren.quan_one.activity.seting.RemindMessageActivity;
import com.quanliren.quan_one.activity.shop.ShopVipDetail;
import com.quanliren.quan_one.activity.user.BlackListActivity;
import com.quanliren.quan_one.activity.user.LoginActivity;
import com.quanliren.quan_one.activity.user.ModifyPasswordActivity;
import com.quanliren.quan_one.activity.user.MyCareActivity;
import com.quanliren.quan_one.activity.user.MyProActivity;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.adapter.SetAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.SetBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.bean.UserTable;
import com.quanliren.quan_one.custom.IosCustomDialog;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.util.ImageUtil;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;
import com.quanliren.quan_one.util.Util;

public class SetingMoreFragment extends MenuFragmentBase {

	public static final String UPDATE_USERINFO="com.quanliren.quan_one.fragment.SetingMoreFragment.UPDATE_USERINFO";
	@ViewInject(id = R.id.listview)
	ListView listview;
	List<SetBean> list = new ArrayList<SetBean>();
	ImageView userlogo;
	TextView nickname;
	TextView powerCount;
	View vip;
	View head = null;
	View vip_txt;
	TextView vip_num;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.seting, null);
		FinalActivity.initInjectedView(this, view);
		setTitleTxt(R.string.seting);
		
		listview.addHeaderView(head = inflater.inflate(R.layout.seting_head,
				null));
		userlogo = (ImageView) head.findViewById(R.id.userlogo);
		nickname = (TextView) head.findViewById(R.id.nickname);
		powerCount = (TextView) head.findViewById(R.id.power_num);
		vip=head.findViewById(R.id.vip);
		vip_txt=head.findViewById(R.id.vip_txt);
		vip_num=(TextView)head.findViewById(R.id.vip_num);

		Intent i = new Intent(getActivity(), QuanPersonalActivity.class);
		i.putExtra("type", QuanPullListViewFragment.ONCE);
		i.putExtra("otherid", "");
		list.add(new SetBean(R.drawable.set_icon_1, "个人动态", false, i));

		list.add(new SetBean(R.drawable.set_icon_2, "会员中心", false, new Intent(
				getActivity(), ShopVipDetail.class)));
		list.add(new SetBean(R.drawable.set_icon_3, "修改密码", false, new Intent(
				getActivity(), ModifyPasswordActivity.class)));
		list.add(new SetBean(R.drawable.set_icon_4, "邀请好友", true, new Intent(
				getActivity(), InviteFriendActivity.class)));
		list.add(new SetBean(R.drawable.set_icon_5, "我的抽奖", true, new Intent(getActivity(),MyProActivity.class)));
		list.add(new SetBean(R.drawable.set_icon_6, "赠送好友体力", false, new Intent(getActivity(),MyCareActivity.class)));
//		list.add(new SetBean(R.drawable.set_icon_7, "体力抽奖活动", false, null));
		list.add(new SetBean(R.drawable.set_icon_8, "功能介绍", true, new Intent(getActivity(),FunctionActivity.class)));
		list.add(new SetBean(R.drawable.set_icon_9, "关于我们", false, new Intent(getActivity(),AboutUsActivity.class)));
		list.add(new SetBean(R.drawable.set_icon_12, "消息通知", true, new Intent(getActivity(),RemindMessageActivity.class)));
		list.add(new SetBean(R.drawable.set_icon_13, "清除缓存", false, null));
		list.add(new SetBean(R.drawable.set_icon_10, "黑名单", true, new Intent(getActivity(),BlackListActivity.class)));
		list.add(new SetBean(R.drawable.set_icon_11, "退出当前账号", false, null));

		View v=new View(getActivity());
		AbsListView.LayoutParams lp=new AbsListView.LayoutParams(AbsListView.LayoutParams.FILL_PARENT,ImageUtil.dip2px(getActivity(), 8));
		v.setLayoutParams(lp);
		listview.addFooterView(v);
		listview.setAdapter(new SetAdapter(getActivity(), list));

		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View view,
					int position, long arg3) {
				if (position == list.size()) {
					loginout(view);
				}else if(position==10){
					clearCache();
				}else {
					if (position > 0) {
						SetBean sb = list.get(position - 1);
						if (sb.clazz != null)
							startActivity(sb.clazz);
					}
				}
			}
		});

		head.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View arg0) {
				Intent i = new Intent(getActivity(), UserInfoActivity.class);
				getActivity().startActivity(i);
			}
		});
		
		receiveBroadcast(UPDATE_USERINFO, handler);
		return view;
	}
	
	Handler handler=new Handler(){
		
		public void dispatchMessage(android.os.Message msg) {
			
			Intent i = (Intent) msg.obj;
			String action=i.getAction();
			if(action.equals(UPDATE_USERINFO)){
				ac.finalHttp.post(URL.GET_USER_INFO, getAjaxParams(),
						callBack);
			}
			super.dispatchMessage(msg);
		};
	};
	
	AjaxCallBack<String> callBack = new AjaxCallBack<String>() {

		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status = jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					User temp = new Gson().fromJson(jo.getString(URL.RESPONSE),
							User.class);
					UserTable ut = new UserTable(temp);
					ac.finalDb.delete(ut);
					ac.finalDb.save(ut);
					
					initSource(temp);
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		};
	};

	public void loginout(View v) {
		new IosCustomDialog.Builder(getActivity())
				.setMessage("您确定要残忍的离开吗？")
				.setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						ac.finalHttp.post(URL.LOGOUT, getAjaxParams(), null);
						ac.finalDb.deleteByWhere(LoginUser.class, "");
						startActivity(new Intent(getActivity(),
								LoginActivity.class));
						getActivity().finish();
					}
				})
				.setNegativeButton("取消", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
					}
				}).create().show();
		;
	}


	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		User user = ac.getUserInfo();

		initSource(user);
	}
	
	public void initSource(User user){
		ImageLoader.getInstance().displayImage(
				user.getAvatar() + StaticFactory._320x320, userlogo,ac.options_userlogo);
		nickname.setText(user.getNickname());
		powerCount.setText(user.getPowernum());
		if(user.getIsvip()==1){
			vip.setVisibility(View.VISIBLE);
			vip_txt.setVisibility(View.VISIBLE);
			vip_num.setVisibility(View.VISIBLE);
			vip_num.setText(Html.fromHtml("<font color=\"#e71d1d\">"+Util.daysBetween(user.getViptime())+"</font>"+"天到期"));
			nickname.setTextColor(getResources().getColor(R.color.vip_name));
		}else{
			vip.setVisibility(View.GONE);
			vip_txt.setVisibility(View.GONE);
			vip_num.setVisibility(View.GONE);
			nickname.setTextColor(getResources().getColor(R.color.username));
		}
	}
	
	public void clearCache(){
		new IosCustomDialog.Builder(getActivity()).setMessage("您确定要清除缓存吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				customShowDialog("正在清理");
				new Thread(new Runnable() {
					
					@Override
					public void run() {
						ImageLoader.getInstance().clearDiskCache();
						ac.finalDb.deleteByWhere(CacheBean.class, "");
						if(getActivity()!=null){
							getActivity().runOnUiThread(new Runnable() {
								public void run() {
									customDismissDialog();
									showCustomToast("清理完成");
								}
							});
						}
					}
				}).start();
			}
		}).setNegativeButton("取消", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
			}
		}).create().show();
	}
	
}
