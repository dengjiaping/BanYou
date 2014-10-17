package com.quanliren.quan_one.fragment;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.fragment.custom.PageTab;
import com.quanliren.quan_one.fragment.custom.PageTab.OnPageTitleClickListener;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;
import com.quanliren.quan_one.fragment.message.MyCareGragment;
import com.quanliren.quan_one.fragment.message.MyLeaveMessageFragment;

public class MessageFragment extends MenuFragmentBase implements OnPageTitleClickListener{

	@ViewInject(id=R.id.viewpager) ViewPager viewpager;
	List<Fragment> views=new ArrayList<Fragment>();
	ArrayList<String> string;
	PageTab pt;
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view=inflater.inflate(R.layout.message_fragment, null);
		FinalActivity.initInjectedView(this, view);
		pt=new PageTab();
		Bundle b=new Bundle();
		string=new ArrayList<String>();
		string.add("消息");
		string.add("好友");
		string.add("关注");
		string.add("粉丝");
		b.putStringArrayList("titles", string);
		pt.setArguments(b);
		pt.setListener(this);
		getChildFragmentManager().beginTransaction().replace(R.id.tab_content, pt).commit();
		
		
		views.add(new MyLeaveMessageFragment());
		MyCareGragment friend=new MyCareGragment();
		Bundle friendB=new Bundle();
		friendB.putInt("type", 1);
		friend.setArguments(friendB);
		views.add(friend);
		
		MyCareGragment care=new MyCareGragment();
		Bundle careB=new Bundle();
		careB.putInt("type", 2);
		care.setArguments(careB);
		views.add(care);
		
		MyCareGragment carem=new MyCareGragment();
		Bundle caremB=new Bundle();
		caremB.putInt("type", 3);
		carem.setArguments(caremB);
		views.add(carem);
		
		final MyOnPageChangeListener listener = new MyOnPageChangeListener();
		viewpager.setOnPageChangeListener(listener);
		viewpager.setAdapter(new mPagerAdapter(getChildFragmentManager()));
		viewpager.post(new Runnable() {
			@Override
			public void run() {
				listener.onPageSelected(0);
			}
		});
		
		setTitleTxt("消息");
		return view;
	}
	
	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int position) {
			setTitleTxt(string.get(position));
			pt.setCurrendIndex(position);
		}

		public void onPageScrolled(int position, float bai, int x) {
			pt.onPageScroll(position, bai, x);
		}

		public void onPageScrollStateChanged(int arg0) {
		}
		
	}
	
	class mPagerAdapter extends FragmentPagerAdapter {

		public mPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int arg0) {
			return views.get(arg0);
		}

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public void setPrimaryItem(ViewGroup container, int position,
				Object object) {
			((LoaderImpl)views.get(position)).refresh();
			super.setPrimaryItem(container, position, object);
		}
	}

	@Override
	public void click(int index) {
		viewpager.setCurrentItem(index);
	};
	
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}

}
