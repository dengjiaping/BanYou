package com.quanliren.quan_one.fragment;/*package com.quanliren.quan_one.fragment;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import me.maxwin.view.XListView;
import me.maxwin.view.XListView.IXListViewListener;

import org.json.JSONObject;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.graphics.Shader.TileMode;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.net.tsz.afinal.FinalActivity;
import com.net.tsz.afinal.FinalDb;
import com.net.tsz.afinal.annotation.view.ViewInject;
import com.net.tsz.afinal.http.AjaxCallBack;
import com.net.tsz.afinal.http.AjaxParams;
import com.quanliren.quan_one.activity.PropertiesActivity;
import com.quanliren.quan_one.activity.R;
import com.quanliren.quan_one.activity.location.ILocationImpl;
import com.quanliren.quan_one.activity.location.Location;
import com.quanliren.quan_one.adapter.LBAdapter;
import com.quanliren.quan_one.adapter.StaggeredAdapter;
import com.quanliren.quan_one.bean.Advertisement;
import com.quanliren.quan_one.bean.CacheBean;
import com.quanliren.quan_one.bean.User;
import com.quanliren.quan_one.custom.GalleryNavigator;
import com.quanliren.quan_one.custom.ScrollViewPager;
import com.quanliren.quan_one.fragment.base.MenuFragmentBase;
import com.quanliren.quan_one.share.CommonShared;
import com.quanliren.quan_one.util.StaticFactory;
import com.quanliren.quan_one.util.URL;

public class HomeFragment extends MenuFragmentBase implements IXListViewListener,ILocationImpl{

	ScrollViewPager myGallery; // 图片轮播Gallery
	GalleryNavigator galleryNavigator; // 小红点
	@ViewInject(id = R.id.waterfall)
	XListView mAdapterView; // 下拉刷新控件
	private StaggeredAdapter mAdapter = null; // 瀑布流适配器
	private int currentPage = 0; // 当前页
	private Gson gson = new Gson();
	private Type type = new TypeToken<ArrayList<Advertisement>>() {
	}.getType();
	private LBAdapter adapter; // 图片轮播适配器
	private List<Advertisement> advlist; // 图片轮播数据源
	private FinalDb fd;
	private CommonShared cs;
	private Location location;
	
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.properties, null);
		FinalActivity.initInjectedView(this, v);
		cs = new CommonShared(getActivity().getApplication());
		setTitleTxt(R.string.home);
		
		rightBtn.setVisibility(View.VISIBLE);
		setLocation();
		// 左右btn
		rightBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
//				((PropertiesActivity) getActivity()).showRight();
			}
		});

		// 瀑布流的ListView
		mAdapterView = (XListView) v.findViewById(R.id.waterfall);

		View galleryView = null;

		// 添加轮播屏幕
		mAdapterView.addHeaderView(galleryView = inflater.inflate(R.layout.gallery, null));
//		galleryView = inflater.inflate(R.layout.gallery, null)
		mAdapter = new StaggeredAdapter(getActivity(),new ArrayList<User>());

		myGallery = (ScrollViewPager) galleryView.findViewById(R.id.homeGalley);

		galleryNavigator = (GalleryNavigator) galleryView
				.findViewById(R.id.gallerynavigator);

		fd = FinalDb.create(getActivity().getApplicationContext());

		CacheBean ud = fd.findById(StaticFactory.AD_CACHE, CacheBean.class);
		if(ud!=null){
			init(ud.getValue());
		}
		if (ud == null || ud.isAfter()) {
			if(ud!=null){
				init(ud.getValue());
			}
			ac.finalHttp.post(URL.AD, acb);
		} else {
			init(ud.getValue());
		}
		myGallery.setOnPageChangeListener(pageChange);

		mAdapterView.setXListViewListener(this);
		
		mAdapterView.setAdapter(mAdapter);

		CacheBean pbud = fd.findById(StaticFactory.PB_CACHE, CacheBean.class);
		if (pbud != null) {
			initPb(pbud.getValue(), 1);
		}
		
		location=new Location(getActivity().getApplicationContext());
		location.setLocationListener(this);
		
		
//		Shader shader =new LinearGradient(0, 0, 0, 100, Color.WHITE, getResources().getColor(R.color.gold), TileMode.CLAMP);title.getPaint().setShader(shader);
		return v;
	}

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		location.destory();
	}
	
	public void setLocation() {
		if(!title_right_txt.getText().equals(cs.getChoseLocation())){
			mAdapterView.startRefresh();
		}
		title_right_txt.setText(cs.getChoseLocation());
	}

	*//**
	 * 小红点
	 *//*
	OnItemSelectedListener osl = new OnItemSelectedListener() {

		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
			if (advlist != null && arg2 != 0 && advlist.size() > 0) {
				galleryNavigator.setPosition(arg2 % advlist.size());
			}
			if (arg2 == 0) {
				galleryNavigator.setPosition(0);
			}
			if (galleryNavigator != null) {
				galleryNavigator.invalidate();
			}
		}

		public void onNothingSelected(AdapterView<?> arg0) {
		}
	};

	*//**
	 * 广告
	 *//*
	AjaxCallBack<String> acb = new AjaxCallBack<String>() {
		@Override
		public void onLoading(long count, long current) { // 每1秒钟自动被回调一次
		}

		@Override
		public void onSuccess(String t) {
			try {
				JSONObject json = new JSONObject(t);
				int status = json.getInt(URL.STATUS);
				switch (status) {
				case 0:
					t = json.getString(URL.RESPONSE);
					CacheBean cb = new CacheBean(StaticFactory.AD_CACHE, t,
							new Date().getTime());
					fd.delete(cb);
					fd.save(cb);
					if (adapter != null) {
						advlist = gson.fromJson(t, type);
						galleryNavigator.setSize(advlist.size());
						galleryNavigator.setPaints(Color.RED, Color.WHITE);
						adapter.setList(advlist);
						adapter.notifyDataSetChanged();
					} else {
						init(t);
					}
					break;
				case -1:

					break;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void onStart() {
		}

		@Override
		public void onFailure(Throwable t, int errorNo, String strMsg) {
			super.onFailure(t, errorNo, strMsg);
		}
	};

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
	}

	*//**
	 * 初始化
	 * 
	 * @param str
	 *            轮播数据
	 *//*
	public void init(String str) {
		advlist = gson.fromJson(str, type);
		if (advlist != null && advlist.size() > 0) {
			galleryNavigator.setSize(advlist.size());
			galleryNavigator.setPaints(Color.RED, Color.WHITE);
			adapter = new LBAdapter(advlist);
			adapter.handler=handler;
			myGallery.setAdapter(adapter);
			adapter.notifyDataSetChanged();
			myGallery.startAutoSlide();
		}
	}

	*//**
	 * 查找瀑布
	 * 
	 * @param pageindex
	 * @param type
	 *//*
	private void AddItemToContainer(final int pageindex, final int type) {
		AjaxParams ap = getAjaxParams();
		ap.put("cityid", ac.cs.getChoseLocationID());
		ap.put("p", pageindex + "");
		ac.finalHttp.post(URL.HOME_USERLIST, ap, new AjaxCallBack<String>() {

			@Override
			public void onSuccess(String json) {
				initPb(json.toString(), type);
			}

			@Override
			public void onFailure(Throwable t, int errorNo, String strMsg) {
				super.onFailure(t, errorNo, strMsg);
				mAdapterView.stop();
				mAdapterView.setPullLoadEnable(true);
			}

		});
	}

	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		if (myGallery != null) {
			myGallery.startAutoSlide();
		}
	}

	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		if (myGallery != null) {
			myGallery.cancelAutoSlide();
		}
	}

	*//**
	 * 初始化瀑布
	 * 
	 * @param json
	 * @param type
	 *//*
	public void initPb(String json, int type) {
		try {
			JSONObject jo = new JSONObject(json);
			int status = jo.getInt(URL.STATUS);
			switch (status) {
			case 0:
				if (currentPage == 0) {
					CacheBean cb = new CacheBean(StaticFactory.PB_CACHE,
							json.toString(), new Date().getTime());
					fd.delete(cb);
					fd.save(cb);
				}
				jo = jo.getJSONObject(URL.RESPONSE);
				int p = jo.getInt(URL.PAGEINDEX);
				mAdapterView.setPage(p);
				currentPage = p;
				List<User> users = new Gson().fromJson(jo.getString(URL.LIST),
						new TypeToken<List<User>>() {
						}.getType());
				if (type == 1) {
					mAdapter.setList(users);
				} else if (type == 2) {
					mAdapter.addNewsItems(users);
				}
				break;
			default:
				showFailInfo(jo);
				break;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			mAdapter.notifyDataSetChanged();
			mAdapterView.stop();
		}
	}


	@Override
	public void onRefresh() {
		location.startLocation();
	}

	@Override
	public void onLoadMore() {
		AddItemToContainer(currentPage, 2);		
	}

	@Override
	public void onLocationSuccess() {
		currentPage = 0;
		AddItemToContainer(currentPage, 1);
		ac.finalHttp.post(URL.AD,getAjaxParams(), acb);
	}

	@Override
	public void onLocationFail() {
		onLocationSuccess();
	}

	OnPageChangeListener pageChange=new OnPageChangeListener() {
		
		@Override
		public void onPageSelected(int position) {
			if (advlist != null && position != 0 && advlist.size() > 0) {
				galleryNavigator.setPosition(position % advlist.size());
			}
			if (position == 0) {
				galleryNavigator.setPosition(0);
			}
			if (galleryNavigator != null) {
				galleryNavigator.invalidate();
			}
		}
		
		@Override
		public void onPageScrolled(int position, float positionOffset,
				int positionOffsetPixels) {
		}
		
		@Override
		public void onPageScrollStateChanged(int state) {
		}
	};
	
	Handler handler=new Handler(){
	@Override
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 1:
				myGallery.setLayoutParams(new RelativeLayout.LayoutParams(msg.arg1,msg.arg2));
				break;

			default:
				break;
			}
			super.dispatchMessage(msg);
		}	
	};
}
*/