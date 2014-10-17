package com.quanliren.quan_one.activity.user;


import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.ViewGroup;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.fragment.custom.PageTab;
import com.quanliren.quan_one.fragment.custom.PageTab.OnPageTitleClickListener;
import com.quanliren.quan_one.fragment.impl.LoaderImpl;
import com.quanliren.quan_one.fragment.message.MyCareGragment;
import com.quanliren.quan_one.fragment.message.MyLeaveMessageFragment;

public class MyProActivity extends BaseActivity implements OnPageTitleClickListener{
	
	private static final String TAG="MyProActivity";
	@ViewInject(id=R.id.viewpager) ViewPager viewpager;
	List<Fragment> views=new ArrayList<Fragment>();
	ArrayList<String> string;
	PageTab pt;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.my_pro_fragment);
		setTitleTxt("我的抽奖");
		
		pt=new PageTab();
		Bundle b=new Bundle();
		string=new ArrayList<String>();
		string.add("中奖礼品");
		string.add("抽奖号码");
		b.putStringArrayList("titles", string);
		pt.setArguments(b);
		pt.setListener(this);
		getSupportFragmentManager().beginTransaction().replace(R.id.tab_content, pt).commit();
		
		views.add(new MyProFragment());
		views.add(new MyProHistoryActivity());
		
		
		final MyOnPageChangeListener listener = new MyOnPageChangeListener();
		viewpager.setOnPageChangeListener(listener);
		viewpager.setAdapter(new mPagerAdapter(getSupportFragmentManager()));
		viewpager.post(new Runnable() {
			@Override
			public void run() {
				listener.onPageSelected(0);
			}
		});
		
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
