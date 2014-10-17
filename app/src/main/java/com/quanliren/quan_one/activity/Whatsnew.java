package com.quanliren.quan_one.activity;

import java.util.ArrayList;

import org.json.JSONObject;

import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.base.BaseActivity;
import com.quanliren.quan_one.activity.user.LoginActivity;
import com.quanliren.quan_one.bean.LoginUser;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.BitmapCache;
import com.quanliren.quan_one.util.URL;

public class Whatsnew extends BaseActivity {
	
	ArrayList<View> views = new ArrayList<View>();
	
	@ViewInject(id=R.id.whatsnew_viewpager) ViewPager mViewPager;
	@ViewInject(id=R.id.page0) ImageView mPage0;
	@ViewInject(id=R.id.page1) ImageView mPage1;
	@ViewInject(id=R.id.page2) ImageView mPage2;
	@ViewInject(id=R.id.page3) ImageView mPage3;
	@ViewInject(id=R.id.enter_btn,click="startbutton") Button enter_btn;
	@ViewInject(id=R.id.text) TextView text;
	@ViewInject(id=R.id.pages) View pages;
	@ViewInject(id=R.id.bg)View bg;
	
	String[] str=new String[]{"","","",""};
	
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
	            WindowManager.LayoutParams.FLAG_FULLSCREEN);
		setContentView(R.layout.whatsnew_viewpager);
		setSwipeBackEnable(false);
//		String isFirstStart=ac.cs.getIsFirstStart();
//		isFirstStart="";
	/*	if("".equals(isFirstStart)){
			pages.setVisibility(View.VISIBLE);
			text.setText(str[0]);
			mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
			try {
				views = new ArrayList<View>();
				LayoutInflater mLi = LayoutInflater.from(this);
				View view1 = mLi.inflate(R.layout.whats1, null);
				view1.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_1, this)));
				views.add(view1);
				View view2 = mLi.inflate(R.layout.whats1, null);
				view2.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_2, this)));
				views.add(view2);
				View view3 = mLi.inflate(R.layout.whats1, null);
				view3.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_3, this)));
				views.add(view3);
				view3.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View arg0) {
						LoginUser user=ac.getUser();
						if(user==null){
							Intent intent = new Intent(Whatsnew.this, LoginActivity.class);
							startActivity(intent);
						}else{
							Intent intent = new Intent(Whatsnew.this, PropertiesActivity.class);
							startActivity(intent);
						}
						ac.cs.setIsFirstStart("1");
						finish();
					}
				});
//				View view4 = mLi.inflate(R.layout.whats1, null);
//				view4.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_4, this)));
//				views.add(view4);
			} catch (Exception e) {
				e.printStackTrace();
			}

			PagerAdapter mPagerAdapter = new PagerAdapter() {

				public boolean isViewFromObject(View arg0, Object arg1) {
					return arg0 == arg1;
				}

				public int getCount() {
					return views.size();
				}

				public void destroyItem(View container, int position, Object object) {
					((ViewPager) container).removeView(views.get(position));
				}

				public Object instantiateItem(View container, int position) {
					((ViewPager) container).addView(views.get(position));
					return views.get(position);
				}
			};

			mViewPager.setAdapter(mPagerAdapter);
		}else{
			bg.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome, this)));
			text.setText(str[0]);
			new Handler().postDelayed(new Runnable() {
				
				public void run() {
					LoginUser user=ac.getUser();
					if(user==null){
						Intent intent = new Intent(Whatsnew.this, LoginActivity.class);
						startActivity(intent);
					}else{
						Intent intent = new Intent(Whatsnew.this, PropertiesActivity.class);
						startActivity(intent);
					}
					ac.cs.setIsFirstStart("1");
					finish();
				}
			}, 1000);
		}
		createShorcut(R.drawable.icon);*/
//		createShorcut(R.drawable.icon1);
		bg.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome, this)));
		new Handler().postDelayed(new Runnable() {
			
			public void run() {
				String isFirstStart=ac.cs.getIsFirstStart();
				if("".equals(isFirstStart)){
					pages.setVisibility(View.VISIBLE);
					text.setText(str[0]);
					mViewPager.setOnPageChangeListener(new MyOnPageChangeListener());
					try {
						views = new ArrayList<View>();
						LayoutInflater mLi = LayoutInflater.from(Whatsnew.this);
						View view1 = mLi.inflate(R.layout.whats1, null);
						view1.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_1, Whatsnew.this)));
						views.add(view1);
						View view2 = mLi.inflate(R.layout.whats1, null);
						view2.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_2, Whatsnew.this)));
						views.add(view2);
						View view3 = mLi.inflate(R.layout.whats1, null);
						view3.setBackgroundDrawable(new BitmapDrawable(BitmapCache.getInstance().getBitmap(R.drawable.welcome_3, Whatsnew.this)));
						views.add(view3);
						view3.setOnClickListener(new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								LoginUser user=ac.getUser();
								if(user==null){
									Intent intent = new Intent(Whatsnew.this, LoginActivity.class);
									startActivity(intent);
								}else{
									Intent intent = new Intent(Whatsnew.this, PropertiesActivity.class);
									startActivity(intent);
								}
								ac.cs.setIsFirstStart("1");
								finish();
							}
						});
					} catch (Exception e) {
						e.printStackTrace();
					}

					PagerAdapter mPagerAdapter = new PagerAdapter() {

						public boolean isViewFromObject(View arg0, Object arg1) {
							return arg0 == arg1;
						}

						public int getCount() {
							return views.size();
						}

						public void destroyItem(View container, int position, Object object) {
							((ViewPager) container).removeView(views.get(position));
						}

						public Object instantiateItem(View container, int position) {
							((ViewPager) container).addView(views.get(position));
							return views.get(position);
						}
					};

					mViewPager.setAdapter(mPagerAdapter);
				}else{
					LoginUser user=ac.getUser();
					if(user==null){
						Intent intent = new Intent(Whatsnew.this, LoginActivity.class);
						startActivity(intent);
					}else{
						Intent intent = new Intent(Whatsnew.this, PropertiesActivity.class);
						startActivity(intent);
					}
					ac.cs.setIsFirstStart("1");
					finish();
				}
			}
		}, 1000);
		
		createShorcut(R.drawable.icon);
		
		/**统计**/
		String firstsend=ac.cs.getIsFirstSend();
		if("".equals(firstsend)){
			AjaxParams ap = new AjaxParams();
			ap.put("appname", ac.cs.getVersionName());
			ap.put("appcode", ac.cs.getVersionCode()+"");
			ap.put("channelname", ac.cs.getChannel());
			ap.put("deviceid", ac.cs.getDeviceId());
			ap.put("devicetype", "0");
			ap.put("oscode", android.os.Build.VERSION.SDK);
			ap.put("osversion", android.os.Build.VERSION.RELEASE);
			ap.put("model", android.os.Build.MODEL);
			
			ac.finalHttp.post(URL.TONGJI,ap, new AjaxCallBack<String>() {
				@Override
				public void onSuccess(String t) {
					try {
						JSONObject jo = new JSONObject(t);
						int status=jo.getInt(URL.STATUS);
						switch (status) {
						case 0:
							ac.cs.setIsFirstSend("1");
							break;
						}
					} catch (Exception e) {
					}
				}
			});
		}
	}

	public class MyOnPageChangeListener implements OnPageChangeListener {

		public void onPageSelected(int arg0) {
			switch (arg0) {
			case 0:
				mPage0.setImageResource(R.drawable.page_now);
				mPage1.setImageResource(R.drawable.page);
				break;
			case 1:
				mPage1.setImageResource(R.drawable.page_now);
				mPage0.setImageResource(R.drawable.page);
				mPage2.setImageResource(R.drawable.page);
				break;
			case 2:
				mPage2.setImageResource(R.drawable.page_now);
				mPage1.setImageResource(R.drawable.page);
				mPage3.setImageResource(R.drawable.page);
				break;
			case 3:
				mPage3.setImageResource(R.drawable.page_now);
				mPage2.setImageResource(R.drawable.page);
				break;
			}
			if(arg0==3){
				enter_btn.setVisibility(View.VISIBLE);
			}else{
				enter_btn.setVisibility(View.GONE);
			}
			text.setText(str[arg0]);
		}

		public void onPageScrollStateChanged(int arg0) {
		}
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}
	}
	
	public void startbutton(View v) {
		LoginUser user=ac.getUser();
		if(user==null){
			Intent intent = new Intent(Whatsnew.this, LoginActivity.class);
			startActivity(intent);
		}else{
			Intent intent = new Intent(Whatsnew.this, PropertiesActivity.class);
			startActivity(intent);
		}
		ac.cs.setIsFirstStart("1");
		finish();
	}

	private void createShorcut(int id) {
		if (ac.cs.getFastStartIcon() == CommonShared.OPEN) {
			return;
		} else {
			ac.cs.setFastStartIcon(CommonShared.OPEN);
		}
		Intent shortcutintent = new Intent(
				"com.android.launcher.action.INSTALL_SHORTCUT");
		// 不允许重复创建
		shortcutintent.putExtra("duplicate", false);
		// 需要现实的名称
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_NAME,
				getString(R.string.app_name));
		// 快捷图片
		Parcelable icon = Intent.ShortcutIconResource.fromContext(
				getApplicationContext(), id);
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
		// 点击快捷图片，运行的程序主入口
		
		//点击快捷方式的操作
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        intent.addFlags(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setClass(Whatsnew.this, Whatsnew.class);
		
		shortcutintent.putExtra(Intent.EXTRA_SHORTCUT_INTENT, intent);
		// 发送广播。OK
		sendBroadcast(shortcutintent);
	}
}
