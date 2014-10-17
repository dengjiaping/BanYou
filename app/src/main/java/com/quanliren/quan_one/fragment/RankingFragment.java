package com.quanliren.quan_one.fragment;/*package com.quanliren.quan_one.fragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.PropertiesActivity;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.CreateGroupActivity;
import com.quanliren.quan_one.activity.user.UserInfoActivity;
import com.quanliren.quan_one.activity.user.UserOtherInfoActivity;
import com.quanliren.quan_one.adapter.GroupAdapter;
import com.quanliren.quan_one.adapter.NearPeopleAdapter;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.fragment.GroupFragment.MyOnPageChangeListener;
import com.quanliren.quan_one.fragment.GroupFragment.mPagerAdapter;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class RankingFragment extends MenuFragmentBase{

	
	@ViewInject(id=R.id.tab_left_btn,click="tab_left_click")public View tab_left_btn;
	@ViewInject(id=R.id.tab_left_txt)public TextView tab_left_txt;
	@ViewInject(id=R.id.tab_left_ll)public View tab_left_ll;
	@ViewInject(id=R.id.tab_right_btn,click="tab_right_click")public View tab_right_btn;
	@ViewInject(id=R.id.tab_right_txt)public TextView tab_right_txt;
	@ViewInject(id=R.id.tab_right_ll)public View tab_right_ll;
	
	@ViewInject(id = R.id.viewpager)private ViewPager viewpaper;
	List<Fragment> viewlist;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v=inflater.inflate(R.layout.ranking, null);
		FinalActivity.initInjectedView(this, v);
		setTitleTxt(R.string.ranking);
		setListener();
		return v;
	}
	
	public void setListener() {
		
		final MyOnPageChangeListener listener = new MyOnPageChangeListener();
		viewpaper.setOnPageChangeListener(listener);
		viewlist = new ArrayList<Fragment>();
		viewlist.add(new RankingPullListViewFragment());
		viewlist.add(new RankingPullListViewFragment());
		viewpaper.setAdapter(new mPagerAdapter(getChildFragmentManager()));
		viewpaper.post(new Runnable() {
			@Override
			public void run() {
				listener.onPageSelected(0);
			}
		});
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int position) {
			((LoaderImpl) viewlist.get(position)).refresh();
			change(position);
		}

		public void onPageScrolled(int position, float bai, int x) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}

	public void tab_left_click(View v) {
		change(0);
		viewpaper.setCurrentItem(0);
	}
	
	public void change(int num){
		switch (num) {
		case 0:
			tab_left_txt.setTextColor(getActivity().getResources().getColor(
					R.color.tab_press_txt));
			tab_left_ll.setVisibility(View.VISIBLE);

			tab_right_txt.setTextColor(getActivity().getResources().getColor(
					R.color.tab_normal_txt));
			tab_right_ll.setVisibility(View.GONE);
			break;
		case 1:
			tab_right_txt.setTextColor(getActivity().getResources().getColor(
					R.color.tab_press_txt));
			tab_right_ll.setVisibility(View.VISIBLE);

			tab_left_txt.setTextColor(getActivity().getResources().getColor(
					R.color.tab_normal_txt));
			tab_left_ll.setVisibility(View.GONE);
			break;
		default:
			break;
		}
	}

	public void tab_right_click(View v) {
		change(1);
		viewpaper.setCurrentItem(1);
	}


	class mPagerAdapter extends FragmentPagerAdapter {

		public mPagerAdapter(FragmentManager fm) {
			super(fm);
			// TODO Auto-generated constructor stub
		}

		@Override
		public Fragment getItem(int arg0) {
			return viewlist.get(arg0);
		}

		@Override
		public int getCount() {
			return viewlist.size();
		}

	};
}
*/