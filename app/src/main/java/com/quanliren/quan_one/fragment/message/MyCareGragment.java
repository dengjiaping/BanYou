package com.quanliren.quan_one.fragment.message;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity;
import com.quanliren.quan_one.adapter.FriendPeopleAdapter;
import com.quanliren.quan_one.adapter.NearPeopleAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;
import com.quanliren.quan_one.util.URL;

public class MyCareGragment extends MenuFragmentBase implements IXListViewListener,LoaderImpl{

	public static final String TAG="MyCareGragment";
	
	@ViewInject(id=R.id.listview)XXListView listview;
	FriendPeopleAdapter adapter;
	int p=0;
	LoginUser user;
	int type=1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		user=ac.getUser();
		type=getArguments().getInt("type");
	}
	View view;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view==null){
			view=inflater.inflate(R.layout.my_care_list, null);
			FinalActivity.initInjectedView(this, view);
		}else{
			ViewParent parent=view.getParent();
			if(parent!=null&&parent instanceof ViewGroup){
				((ViewGroup)parent).removeView(view);
			}
		}
		return view;
	}

	
	public void initAdapter() {
		CacheBean cb=ac.finalDb.findById(TAG+user.getId()+type, CacheBean.class);
		List<User> users=new ArrayList<User>();
		if(cb!=null){
			users=new Gson().fromJson(cb.getValue(), new TypeToken<ArrayList<User>>(){}.getType());
		}
		adapter = new FriendPeopleAdapter(getActivity(),users);
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position>0&&position<=adapter.getCount()){
					User user=adapter.getItem(position);
					Intent i = new Intent(getActivity(),user.getId().equals(ac.getUser().getId())?UserInfoActivity.class:UserOtherInfoActivity.class);
					i.putExtra("id", user.getId());
					startActivity(i);
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		p=0;
		ac.finalHttp.post(URL.CONCERNLIST,getAjaxParams("p", "0").put("type", type+""), callBack);
	}

	@Override
	public void onLoadMore() {
		ac.finalHttp.post(URL.CONCERNLIST,getAjaxParams("p", p+"").put("type", type+""), callBack);
	}
	
	AjaxCallBack<String> callBack=new AjaxCallBack<String>() {
		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					JSONObject response=jo.getJSONObject(URL.RESPONSE);
					String list=response.getString(URL.LIST);
					List<User> users=new Gson().fromJson(list, new TypeToken<ArrayList<User>>(){}.getType());
					if(p==0){
						ac.finalDb.deleteById(CacheBean.class, TAG+user.getId()+type);
						ac.finalDb.save(new CacheBean(TAG+user.getId()+type, list, new Date().getTime()));
						adapter.setList(users);
					}else{
						adapter.addNewsItems(users);
					}
					adapter.notifyDataSetChanged();
					listview.setPage(p=response.getInt(URL.PAGEINDEX));
					break;
				default:
					showFailInfo(jo);
					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				listview.stop();
			}
		};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			listview.stopRefresh();
		};
	};
	@Override
	public void refresh() {
		if(getActivity()!=null&&init.compareAndSet(false, true)){
			initAdapter();
		}
	}
	
}
