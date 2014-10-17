package com.quanliren.quan_one.fragment;/*package com.quanliren.quan_one.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.GroupInfoActivity;
import com.quanliren.quan_one.adapter.NearPeopleAdapter;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;

public class RankingPullListViewFragment extends Fragment implements LoaderImpl {

	@ViewInject(id=R.id.listview)PullToRefreshListView pull;
	NearPeopleAdapter adapter;
	ListView listview;
	boolean refreshed=false;
	View mView;
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
		// initAdapter();
		return mView;
	}

	public void initAdapter() {
		List<User> list = new ArrayList<User>();
		for (int i = 0; i < 8; i++) {
			list.add(new User());
		}
		adapter = new NearPeopleAdapter(getActivity(), list);
		listview = pull.getRefreshableView();
		listview.setAdapter(adapter);
	}

	@Override
	public void refresh() {
		if(!refreshed){
			initAdapter();
			setListener();
			refreshed=true;
		}
	}

	public void setListener() {
		listview.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				Intent i = new Intent(getActivity(), GroupInfoActivity.class);
				startActivity(i);
			}
		});
	}
}*/