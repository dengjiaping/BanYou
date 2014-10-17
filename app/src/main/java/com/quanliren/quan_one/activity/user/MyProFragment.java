package com.quanliren.quan_one.activity.user;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;

import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
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
import com.quanliren.quan_one.activity.shop.ProInfoActivity;
import com.quanliren.quan_one.adapter.MyProAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.bean.ProBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;
import com.quanliren.quan_one.util.URL;

public class MyProFragment extends MenuFragmentBase implements LoaderImpl, IXListViewListener,OnItemClickListener{
	
	private static final String TAG="MyProActivity";
	private String CACHEKEY=TAG;
	public static final String REFERE="com.quanliren.quan_one.activity.user.MyProActivity.REFERE";
	@ViewInject(id=R.id.listview)XXListView listview;
	MyProAdapter adapter;
	LoginUser user;
	private int p=0;
	
	View view;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if(view==null){
			view=inflater.inflate(R.layout.my_pro, null);
			FinalActivity.initInjectedView(this, view);
		}else{
			ViewParent parent=view.getParent();
			if(parent!=null&&parent instanceof ViewGroup){
				((ViewGroup)parent).removeView(view);
			}
		}
		return view;
	}

	@Override
	public void rightClick(View v) {
		
	}
	
	@Override
	public void onRefresh() {
		p=0;
		ac.finalHttp.post(URL.MY_PRO,getAjaxParams().put("p", p+""), callBack);
	}

	@Override
	public void onLoadMore() {
		ac.finalHttp.post(URL.MY_PRO,getAjaxParams().put("p", p+""), callBack);
	};
	
	AjaxCallBack<String> callBack=new AjaxCallBack<String>() {
		public void onStart() {};
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			listview.stop();
			showIntentErrorToast();
		};
		public void onSuccess(String t) {
			try {
				JSONObject jo= new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo = jo.getJSONObject(URL.RESPONSE);
					List<ProBean> list = new Gson().fromJson(
							jo.getString(URL.LIST),
							new TypeToken<ArrayList<ProBean>>() {
							}.getType());
					if (p == 0) {
						CacheBean cb = new CacheBean(CACHEKEY,
								jo.getString(URL.LIST), new Date().getTime());
						ac.finalDb.deleteById(CacheBean.class, CACHEKEY);
						ac.finalDb.save(cb);
						adapter.setList(list);
					} else {
						adapter.addNewsItems(list);
					}
					adapter.notifyDataSetChanged();
					if(jo.has(URL.PAGEINDEX))
						listview.setPage(p = jo.getInt(URL.PAGEINDEX));
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
	};

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		if(position>0&&position<=adapter.getCount()){
			ProBean pb=(ProBean) adapter.getItem(position-1);
			Intent i = new Intent(getActivity(),ProInfoActivity.class);
			i.putExtra("bean", pb);
			startActivity(i);
		}
	}
	
	Handler broad=new Handler(){
		public void dispatchMessage(android.os.Message msg) {
			Intent i = (Intent) msg.obj;
			String action = i.getAction();
			if (action.equals(REFERE)) {
				listview.startRefresh();
			}
			super.dispatchMessage(msg);
		};
	};

	@Override
	public void refresh() {
		// TODO Auto-generated method stub
		if(getActivity()!=null&&init.compareAndSet(false, true)){
			user=ac.getUser();
			CACHEKEY+=user.getId();
			
			CacheBean cb=ac.finalDb.findById(CACHEKEY, CacheBean.class);
			List<ProBean> users=new ArrayList<ProBean>();
			if(cb!=null){
				users=new Gson().fromJson(cb.getValue(), new TypeToken<ArrayList<ProBean>>(){}.getType());
			}
			adapter=new MyProAdapter(getActivity(), users);
			listview.setAdapter(adapter);
			listview.setXListViewListener(this);
			
			listview.setOnItemClickListener(this);
			
			receiveBroadcast(REFERE, broad);
		}
	}
}
