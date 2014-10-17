package com.quanliren.quan_one.fragment.mypro;

import java.util.ArrayList;
import java.util.List;

import me.maxwin.view.XXListView;
import me.maxwin.view.XXListView.IXListViewListener;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.adapter.GiftAdapter;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;

public class GiftFragment extends MenuFragmentBase implements LoaderImpl,IXListViewListener{
	
	private static final String TAG="GiftFragment";
	private String CACHEKEY="";
	@ViewInject(id=R.id.listview)XXListView listview;
	GiftAdapter adapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.quanpulllistview, null);
		FinalActivity.initInjectedView(this, view);
		return view;
	}


	@Override
	public void refresh() {
		if(getActivity()!=null&&init.compareAndSet(false, true)){
			CACHEKEY=TAG+ac.getUser().getId();
			List<User> list=new ArrayList<User>();
			for (int i = 0; i < 100; i++) {
				list.add(null);
			}
			adapter=new GiftAdapter(getActivity(), list);
			listview.setAdapter(adapter);
		}
	}


	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLoadMore() {
		// TODO Auto-generated method stub
		
	}
}
