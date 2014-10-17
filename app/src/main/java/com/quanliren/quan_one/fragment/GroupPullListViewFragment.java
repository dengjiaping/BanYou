package com.quanliren.quan_one.fragment;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.GroupInfoActivity;
import com.quanliren.quan_one.adapter.GroupAdapter;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.GroupBean;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;
import com.quanliren.quan_one.util.URL;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

public class GroupPullListViewFragment extends MenuFragmentBase implements LoaderImpl,IXListViewListener {
	public static final String TAG="GroupPullListViewFragment";
	
	private String CACHEKEY=TAG;
	private int type=0;
	private static final int ALL=0;
	private static final int MY=1;
	
	@ViewInject(id = R.id.listview)
	XXListView listview;
	GroupAdapter adapter;
	View mView;
	AjaxParams ap;
	int p=0;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		if(getArguments()!=null&&getArguments().containsKey("type"))
			type=getArguments().getInt("type");
		super.onCreate(savedInstanceState);
		switch (type) {
		case MY:
			CACHEKEY=TAG+ac.getUser().getId();
			break;
		}
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		if (mView != null) {
			ViewGroup parent = (ViewGroup) mView.getParent();
			if (parent != null) {
				parent.removeView(mView);
			}
		} else {
			mView = inflater.inflate(R.layout.pulllistview, null);
			FinalActivity.initInjectedView(this, mView);
		}
		return mView;
	}

	public void initAdapter() {
		List<GroupBean> list = new ArrayList<GroupBean>();
		CacheBean cb=ac.finalDb.findById(CACHEKEY, CacheBean.class);
		if(cb!=null){
			list=new Gson().fromJson(cb.getValue(), new TypeToken<ArrayList<GroupBean>>(){}.getType());
		}
		adapter = new GroupAdapter(getActivity(), list);
		listview.setAdapter(adapter);
		listview.setXListViewListener(this);
	}

	@Override
	public void refresh() {
		if (getActivity()!=null&&init.compareAndSet(false, true)) {
			initAdapter();
			setListener();
		}
	}

	public void setListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int position,
					long arg3) {
				if(position>0&&position<=adapter.getCount()){
					Intent i = new Intent(getActivity(), GroupInfoActivity.class);
					i.putExtra("group", (GroupBean)adapter.getItem(position-1));
					startActivity(i);
				}
			}
		});
	}

	@Override
	public void onRefresh() {
		ap=getAjaxParams("ctype", type+"");
		p=0;
		ac.finalHttp.post(URL.GETGROUPLIST,ap.put("p",  p+""), callBack);
	}

	@Override
	public void onLoadMore() {
		ac.finalHttp.post(URL.GETGROUPLIST,ap.put("p",  p+""), callBack);
	}
	
	AjaxCallBack<String> callBack=new AjaxCallBack<String>() {
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			showIntentErrorToast();
			listview.stop();
		};
		public void onSuccess(String t) {
			try {
				JSONObject jo = new JSONObject(t);
				int status=jo.getInt(URL.STATUS);
				switch (status) {
				case 0:
					jo=jo.getJSONObject(URL.RESPONSE);
					List<GroupBean> list=new Gson().fromJson(jo.getString(URL.LIST), new TypeToken<ArrayList<GroupBean>>(){}.getType());
					if(p==0){
						CacheBean cb=new CacheBean(CACHEKEY, jo.getString(URL.LIST), new Date().getTime());
						ac.finalDb.deleteById(CacheBean.class, CACHEKEY);
						ac.finalDb.save(cb);
						adapter.setList(list);
					}else{
						adapter.addNewsItems(list);
					}
					adapter.notifyDataSetChanged();
					listview.setPage(p=jo.getInt(URL.PAGEINDEX));
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
	
	public void refere(){
		if (getActivity()!=null) {
			listview.startRefresh();	
		}
	}
}
