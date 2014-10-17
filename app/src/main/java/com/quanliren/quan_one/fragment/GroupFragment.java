package com.quanliren.quan_one.fragment;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.nineoldandroids.view.ViewPropertyAnimator;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.group.CreateGroupActivity;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;

public class GroupFragment extends MenuFragmentBase {

	// @ViewInject(id=R.id.listview)PullToRefreshListView pull;

	@ViewInject(id = R.id.viewpager)
	private ViewPager viewpaper;

	List<GroupPullListViewFragment> viewlist;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.group, null);
		FinalActivity.initInjectedView(this, v);
		setTitleTxt(R.string.group);
		setTitleRightTxt(R.string.create);
		initView();
		setListener();
		return v;
	}

	public void setListener() {
		
		final MyOnPageChangeListener listener = new MyOnPageChangeListener();
		viewpaper.setOnPageChangeListener(listener);
		viewlist = new ArrayList<GroupPullListViewFragment>();
		viewlist.add(new GroupPullListViewFragment());
		GroupPullListViewFragment my=new GroupPullListViewFragment();
		Bundle myb=new Bundle();
		myb.putInt("type", 1);
		my.setArguments(myb);
		viewlist.add(my);
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
			ViewPropertyAnimator.animate(tab_icon).translationX(offset * position)
            .setDuration(200);
		}

		public void onPageScrolled(int position, float bai, int x) {
		}

		public void onPageScrollStateChanged(int arg0) {
		}
	}

	@Override
	public void rightClick(View v) {
		// TODO Auto-generated method stub
		Intent i = new Intent(getActivity(), CreateGroupActivity.class);
		startActivityForResult(i, 1);
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

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			((LoaderImpl)viewlist.get(position)).refresh();
			super.setPrimaryItem(container, position, object);
		}
	};

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case 1:
			switch (resultCode) {
			case 1:
				for (GroupPullListViewFragment f : viewlist) {
					f.refere();
				}
				break;

			default:
				break;
			}
			break;

		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	List<TextView> tabs;
	int offset;
	int currentIndex;
	@ViewInject(id = R.id.tab1)
	TextView tab1;
	@ViewInject(id = R.id.tab2)
	TextView tab2;
	@ViewInject(id = R.id.tab_icon)
	ImageView tab_icon;
	public void initView() {
		int width = getResources().getDisplayMetrics().widthPixels;
		offset = (int) ((float) width / 2);
		RelativeLayout.LayoutParams iconlp = new RelativeLayout.LayoutParams(
				offset, RelativeLayout.LayoutParams.WRAP_CONTENT);
		iconlp.addRule(RelativeLayout.ALIGN_BOTTOM, R.id.tab_ll);
		tab_icon.setLayoutParams(iconlp);
		tabs = new ArrayList<TextView>();
		tabs.add(tab2);
		tabs.add(tab1);


		for (final TextView tv : tabs) {
			tv.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View arg0) {
					viewpaper.setCurrentItem(tabs.indexOf(tv));
				}
			});
		}
	}
}
